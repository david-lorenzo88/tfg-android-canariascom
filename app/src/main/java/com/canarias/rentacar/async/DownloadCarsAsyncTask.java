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
 * Created by David on 04/11/2014.
 * Tarea en segundo plano que realiza la descarga de vehiculos desde el servicio web
 */
public class DownloadCarsAsyncTask extends
        AsyncTask<Void, Void, String> {

    ProgressDialog progress;
    Context context;


    public DownloadCarsAsyncTask(Context context) {

        this.context = context;
    }


    /**
     * Metodo que se ejecuta en segundo plano y realiza la descarga de vehículos
     * @param params Void
     * @return Mensaje de resultado
     */
    @Override
    protected String doInBackground(Void... params) {
        //Realizamos la descarga de vehiculos desde el servicio web
        WebServiceController wsc = new WebServiceController();
        Response result = wsc.getAllCars();

        //Comprobamos si el tipo devuelto es de tipo GetAllCarsResponse
        if (result.getClass().equals(GetAllCarsResponse.class)) {
            List<Car> cars = ((GetAllCarsResponse) result).getCars();

            //Actualizamos los vehículos en la base de datos local
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

                //Devolvemos el resultado
                return context.getString(R.string.cars_download_ok);

            } catch (SQLException ex) {

                return context.getString(R.string.cars_download_error) + ": " + ex.getMessage();
            }
        } else
            return context.getString(R.string.no_cars_downloaded);
    }
    /**
     * Ejecutado al finalizar el doInBackground().
     * Actualiza la interfaz gráfica mostrando el resultado
     * @param result Resultado generado por el doInBackground()
     */
    protected void onPostExecute(String result) {


        //Cerramos el dialogo de descarga
        progress.dismiss();

        //Mostramos un dialogo con el resultado
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
    /**
     * Ejecutado antes de comenzar el doInBackground()
     * Mostramos un diálogo indicando que se están descargando los vehículos
     */
    protected void onPreExecute() {

        progress = new ProgressDialog(context);
        progress.setTitle(context.getString(R.string.downloading_cars));
        progress.setMessage(context.getString(R.string.downloading_cars_msg));
        progress.show();

    }
}
