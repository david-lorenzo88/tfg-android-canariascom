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
import com.canarias.rentacar.model.Car;
import com.canarias.rentacar.model.CarAttribute;
import com.canarias.rentacar.model.webservice.GetAllCarsResponse;
import com.canarias.rentacar.model.webservice.Response;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by David on 04/09/2014.
 */
public class DownloadCarsAsyncTask extends
        AsyncTask<Void, Void, String> {

    ProgressDialog progress;
    Context context;

    public DownloadCarsAsyncTask(Context context) {

        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        // TODO Auto-generated method stub
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
                return context.getString(R.string.cars_download_ok);

            } catch (SQLException ex) {

                return context.getString(R.string.cars_download_error) + ": " + ex.getMessage();
            }
        } else
            return context.getString(R.string.no_cars_downloaded);
    }

    protected void onPostExecute(String result) {
        // Meter los resultados en el listview
        //progress.setMessage(result);

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
        /*if (result != null) {
            //Hay resultados
            SearchResultAdapter resultsAdapter = new SearchResultAdapter(
                    searchResultsActivity, R.layout.search_result, result);


            ListView resultsListView = (ListView) searchResultsActivity
                    .findViewById(R.id.listViewSearchResults);

            resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    RelativeLayout p = (RelativeLayout) view;
                    Toast.makeText(searchResultsActivity, "aqui", Toast.LENGTH_LONG).show();
                }

            });

            resultsListView.setAdapter(resultsAdapter);


            searchResultsActivity.findViewById(R.id.searchResultsContainer)
                    .setVisibility(View.VISIBLE);
            searchResultsActivity.findViewById(R.id.loadingLayout)
                    .setVisibility(View.GONE);
        } else{
            //No hay resultados, mostrar mensaje
        }*/
    }

    protected void onPreExecute() {
        /*searchResultsActivity.findViewById(R.id.loadingLayout).setVisibility(
                View.VISIBLE);*/
        progress = new ProgressDialog(context);
        progress.setTitle(context.getString(R.string.downloading_cars));
        progress.setMessage(context.getString(R.string.downloading_cars));
        progress.show();

    }
}
