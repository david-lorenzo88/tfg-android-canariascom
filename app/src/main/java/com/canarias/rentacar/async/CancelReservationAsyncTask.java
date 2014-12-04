package com.canarias.rentacar.async;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;

import com.canarias.rentacar.R;
import com.canarias.rentacar.config.Config;
import com.canarias.rentacar.controller.WebServiceController;
import com.canarias.rentacar.db.dao.ReservationDataSource;
import com.canarias.rentacar.model.webservice.CancelReservationResponse;
import com.canarias.rentacar.model.webservice.ErrorResponse;
import com.canarias.rentacar.model.webservice.Response;

import java.sql.SQLException;
import java.util.Locale;

/**
 * Tarea en segundo plano que realiza la cancelación de una reserva
 */
public class CancelReservationAsyncTask extends
        AsyncTask<Void, Void, String> {
    String resId;
    Context context;
    ErrorResponse error;
    AlertDialog loadingDialog;
    ProgressDialog progress;
    TextView statusTvRef;
    ImageView statusImRef;

    public CancelReservationAsyncTask(Context context,
                                      String resId, TextView statusText, ImageView statusIcon) {
        this.resId = resId;
        this.context = context;
        this.statusTvRef = statusText;
        this.statusImRef = statusIcon;
    }

    /**
     * Metodo que se ejecuta en segundo plano y realiza la cancelación de la reserva
     * @param params Void
     * @return Resultado
     */
    @Override
    protected String doInBackground(Void... params) {
        //Realizamos la cancelación de la reserva al servicio web
        WebServiceController wsc = new WebServiceController(
        );
        Response result = wsc.cancelReservation(resId);

        //Comprobamos si el tipo devuelto es de tipo CancelReservationResponse
        if (result != null && result.getClass().equals(CancelReservationResponse.class)) {

            CancelReservationResponse resp = (CancelReservationResponse) result;

            //Actualizamos el estado de la reserva en la base de datos local
            ReservationDataSource ds = new ReservationDataSource(context);
            try {
                ds.open();

                ds.updateReservationStatus(resId, resp.getStatus());

                ds.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            //Devolvemos el estado devuelto
            return resp.getStatus();
        } else {
            //Manejamos el error
            if (result != null && result.getClass().equals(ErrorResponse.class)) {
                error = (ErrorResponse) result;
            }
            return null;
        }
    }

    /**
     * Ejecutado al finalizar el doInBackground().
     * Actualiza la interfaz gráfica mostrando el resultado
     * @param result Resultado generado por el doInBackground()
     */
    protected void onPostExecute(String result) {

        //Cerramos el diálogo que inició el onPreExecute()
        progress.dismiss();

        if (result != null) {
            //Cancelación OK

            //Mostramos diálogo con mensaje
            loadingDialog = new AlertDialog.Builder(context).create();

            loadingDialog.setTitle(context.getString(R.string.cancel_reservation));


            loadingDialog.setButton(Dialog.BUTTON_POSITIVE, context.getString(R.string.accept),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadingDialog.dismiss();
                        }
                    });

            if (result.contains("Cancelado"))
                loadingDialog.setMessage(context.getString(R.string.cancelled_reservation));
            else
                loadingDialog.setMessage(context.getString(R.string.error) + context.getString(R.string.status) + ": " + result);


            loadingDialog.show();

            //Cambiamos el icono y mensaje de estado en la interfaz de detalle de la reserva
            //que actualmente se está mostrando por detrás del diálogo
            if (statusTvRef != null) {
                String sStatus = result.replace("&nbsp;", " ").substring(0, 10);
                try {
                    sStatus = Config.getLanguageCode(Locale.getDefault().getLanguage()).equals("es") ?
                            result.split("/")[0].replace("&nbsp;", " ").trim() :
                            result.split("/")[1].replace("&nbsp;", " ").trim();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                statusTvRef.setText(sStatus);
            }
            if (statusImRef != null) {
                statusImRef.setImageDrawable(
                        context.getResources().getDrawable(R.drawable.ic_cancel_black_48dp));
            }


        } else {
            //Error cancelando la reserva
            //Mostramos diálogo con error


            loadingDialog = new AlertDialog.Builder(context).create();

            loadingDialog.setTitle(context.getString(R.string.cancel_reservation));


            loadingDialog.setButton(Dialog.BUTTON_POSITIVE, context.getString(R.string.accept),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadingDialog.dismiss();
                        }
                    });

            if (error != null) {
                loadingDialog.setMessage(error.getCode() + error.getDescription());
            } else {
                loadingDialog.setMessage(context.getString(R.string.error));
            }


            loadingDialog.show();


        }
    }

    /**
     * Ejecutado antes de comenzar el doInBackground()
     * Mostramos un diálogo indicando que se está cancelando
     * la reserva.
     */
    protected void onPreExecute() {

        progress = new ProgressDialog(context);
        progress.setTitle(context.getString(R.string.cancelling_reservation_title));
        progress.setMessage(context.getString(R.string.cancelling_reservation));
        progress.show();


    }
}