package com.canarias.rentacar.async;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.canarias.rentacar.R;
import com.canarias.rentacar.controller.WebServiceController;
import com.canarias.rentacar.db.dao.AttributeDataSource;
import com.canarias.rentacar.db.dao.CarDataSource;
import com.canarias.rentacar.db.dao.OfficeDataSource;
import com.canarias.rentacar.db.dao.ZoneDataSource;
import com.canarias.rentacar.model.Car;
import com.canarias.rentacar.model.CarAttribute;
import com.canarias.rentacar.model.Office;
import com.canarias.rentacar.model.webservice.GetAllCarsResponse;
import com.canarias.rentacar.model.webservice.ListDestinationsResponse;
import com.canarias.rentacar.model.webservice.Response;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by David on 09/11/2014.
 */
public class SyncDataAsyncTask extends AsyncTask<Void, String, String> {
    ProgressDialog progress;
    Context context;

    public SyncDataAsyncTask(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        WebServiceController wsc = new WebServiceController();
        Response result = wsc.getAllCars();

        if (result.getClass().equals(GetAllCarsResponse.class)) {
            List<Car> cars = ((GetAllCarsResponse) result).getCars();

            try {
                CarDataSource ds = new CarDataSource(context);
                ds.open();

                AttributeDataSource attDS = new AttributeDataSource(context);
                attDS.open();

                Iterator<Car> it = cars.iterator();

                while (it.hasNext()) {
                    Car cur = it.next();

                    long rs = ds.insert(cur);
                    if (rs == -1) {
                        Log.e("TEST", "Error insertando coche: " +
                                cur.getCode() + " - " + cur.getModel());
                    } else {
                        List<CarAttribute> atts = cur.getAttributes();
                        if (atts != null) {
                            Iterator<CarAttribute> attIt = atts.iterator();
                            while (attIt.hasNext()) {
                                CarAttribute at = attIt.next();
                                at.setCarModel(cur.getModel());
                                attDS.insert(at);
                            }
                        }
                    }
                }
                attDS.close();
                ds.close();

                publishProgress(context.getString(R.string.downloading_offices));

                result = wsc.listDestinations();

                if (result.getClass().equals(ListDestinationsResponse.class)) {
                    List<Office> offices = ((ListDestinationsResponse) result).getServicePoints();

                    try {
                        OfficeDataSource dsO = new OfficeDataSource(context);
                        dsO.open();

                        ZoneDataSource zoneDS = new ZoneDataSource(context);
                        zoneDS.open();

                        Iterator<Office> itO = offices.iterator();

                        while (itO.hasNext()) {
                            Office curO = itO.next();

                            //Insertamos / Actualizamos la zona
                            zoneDS.insert(curO.getZone());

                            long rs = dsO.insert(curO);
                            if (rs == -1) {
                                Log.e("TEST", "Error insertando oficina: " +
                                        curO.getCode() + " - " + curO.getName());
                            }
                        }
                        dsO.close();
                        zoneDS.close();
                        return context.getString(R.string.offices_and_cars_download_ok);

                    } catch (SQLException ex) {
                        return context.getString(R.string.offices_download_error) + ": " + ex.getMessage();
                    }
                } else
                    return context.getString(R.string.no_offices_downloaded);



            } catch (SQLException ex) {

                return context.getString(R.string.cars_download_error) + ": " + ex.getMessage();
            }
        } else
            return context.getString(R.string.no_cars_downloaded);



    }

    protected void onPostExecute(String result) {

        progress.dismiss();

        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(result);
        builder1.setCancelable(true);
        builder1.setNegativeButton(context.getString(R.string.close_label),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

        if(progress != null && values.length > 0){
            progress.setTitle(values[0]);
            progress.setMessage(values[0]);

        }

    }

    protected void onPreExecute() {

        progress = new ProgressDialog(context);
        progress.setTitle(context.getString(R.string.downloading_cars));
        progress.setMessage(context.getString(R.string.downloading_cars));
        progress.show();

    }
}
