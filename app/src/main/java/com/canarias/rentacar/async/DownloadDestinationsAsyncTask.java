package com.canarias.rentacar.async;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.canarias.rentacar.R;
import com.canarias.rentacar.controller.WebServiceController;
import com.canarias.rentacar.db.dao.OfficeDataSource;
import com.canarias.rentacar.db.dao.ZoneDataSource;
import com.canarias.rentacar.model.Office;
import com.canarias.rentacar.model.webservice.ListDestinationsResponse;
import com.canarias.rentacar.model.webservice.Response;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by David on 04/11/2014.
 * Tarea en segundo plano que realiza la descarga de zonas desde el servicio web
 */
public class DownloadDestinationsAsyncTask extends
        AsyncTask<Void, Void, String> {

    ProgressDialog progress;
    Context context;

    public DownloadDestinationsAsyncTask(Context context) {

        this.context = context;
    }
    /**
     * Metodo que se ejecuta en segundo plano y realiza la descarga de zonas
     * @param params Void
     * @return Mensaje de resultado
     */
    @Override
    protected String doInBackground(Void... params) {
        //Realizamos la descarga de zonas desde el servicio web
        WebServiceController wsc = new WebServiceController();
        Response result = wsc.listDestinations();

        //Comprobamos si el tipo devuelto es de tipo ListDestinationsResponse
        if (result.getClass().equals(ListDestinationsResponse.class)) {
            List<Office> offices = ((ListDestinationsResponse) result).getServicePoints();

            //Actualizamos las zonas en la base de datos local
            try {
                OfficeDataSource ds = new OfficeDataSource(context);
                ds.open();

                ZoneDataSource zoneDS = new ZoneDataSource(context);
                zoneDS.open();

                Iterator<Office> it = offices.iterator();

                while (it.hasNext()) {
                    Office cur = it.next();

                    //Insertamos / Actualizamos la zona
                    zoneDS.insert(cur.getZone());

                    long rs = ds.insert(cur);
                    if (rs == -1) {
                        Log.e("TEST", "Error insertando oficina: " +
                                cur.getCode() + " - " + cur.getName());
                    }
                }
                ds.close();
                zoneDS.close();

                //Devolvemos el resultado
                return context.getString(R.string.offices_download_ok);

            } catch (SQLException ex) {
                return context.getString(R.string.offices_download_error) + ": " + ex.getMessage();
            }
        } else
            return context.getString(R.string.no_offices_downloaded);
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
     * Mostramos un diálogo indicando que se están descargando las zonas
     */
    protected void onPreExecute() {

        progress = new ProgressDialog(context);
        progress.setTitle(context.getString(R.string.downloading_offices));
        progress.setMessage(context.getString(R.string.downloading_offices_msg));
        progress.show();

    }
}
