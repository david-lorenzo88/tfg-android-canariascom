package com.canarias.rentacar.async;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.canarias.rentacar.R;
import com.canarias.rentacar.config.Config;
import com.canarias.rentacar.controller.WebServiceController;
import com.canarias.rentacar.model.Extra;
import com.canarias.rentacar.model.webservice.ErrorResponse;
import com.canarias.rentacar.model.webservice.GetExtrasResponse;
import com.canarias.rentacar.model.webservice.Response;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
/**
 * Created by David on 04/11/2014.
 * Tarea en segundo plano que realiza la consulta de disponibilidad para los extras
 * Es similar a la de los vehiculos, pero en este caso se devuelven solo los extras
 * disponibles para reservar.
 */
public class GetExtrasAsyncTask extends
        AsyncTask<Void, Void, List<Extra>> {
    //Parámetros a enviar en la petición
    private HashMap<String, String> params;
    private Context context;
    private ErrorResponse error;
    private AlertDialog loadingDialog;
    private ProgressDialog progress;
    private LinearLayout extrasContainer;

    private LayoutInflater inflater;
    //HashMap que almacena la cantidad de extras seleccionados
    private HashMap<Integer, Integer> extrasQuantity;
    private ViewGroup container;
    List<Extra> result;

    public GetExtrasAsyncTask(Context context,
                              HashMap<String, String> params, LinearLayout extrasContainer,
                               LayoutInflater inflater,
                              ViewGroup container, HashMap<Integer, Integer> extrasQuantity) {
        this.params = params;
        this.context = context;
        this.extrasContainer = extrasContainer;

        this.inflater = inflater;
        this.extrasQuantity = extrasQuantity;
        this.container = container;



    }
    /**
     * Metodo que se ejecuta en segundo plano y realiza la consulta de disponibilidad
     * de extras
     * @param params Void
     * @return Lista de Extras disponibles
     */
    @Override
    protected List<Extra> doInBackground(Void... params) {
        //Realizamos la consulta de disponibilidad con el servicio web
        WebServiceController wsc = new WebServiceController(
        );
        Response result = wsc.getExtras(this.params.get(Config.ARG_PICKUP_DATE),
                this.params.get(Config.ARG_PICKUP_TIME),
                this.params.get(Config.ARG_DROPOFF_DATE),
                this.params.get(Config.ARG_DROPOFF_TIME),
                this.params.get(Config.ARG_PICKUP_POINT),
                this.params.get(Config.ARG_DROPOFF_POINT));
        //Comprobamos si el tipo devuelto es de tipo GetExtrasResponse
        if (result != null && result.getClass().equals(GetExtrasResponse.class)) {

            GetExtrasResponse resp = (GetExtrasResponse) result;

            //Actualizamos la cantidad de extras seleccionados de cada uno
            //para prepopular los NumberPickers posteriormente
            //al crear el adapter
            for (Extra extra : resp.getExtras()) {
                if(extrasQuantity.containsKey(extra.getCode())){
                    extra.setQuantity(extrasQuantity.get(extra.getCode()));
                }
            }


            return resp.getExtras();
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
    protected void onPostExecute(List<Extra> result) {
        this.result = result;
        //Cerramos el diálogo de descarga
        progress.dismiss();
        if (result != null) {
            //Resultado OK

            //Para cada extra, cargamos la interfaz y rellenamos todas sus vistas
            //con los valores del extra
            for (Extra extra : result) {

                View extraWrap = inflater.inflate(R.layout.extra_item, container, false);

                if (!extrasQuantity.containsKey(extra.getCode())) {
                    extrasQuantity.put(extra.getCode(), extra.getQuantity());
                }
                NumberPicker numPicker = (NumberPicker) extraWrap.findViewById(R.id.extraSelector);
                numPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        extrasQuantity.put(Integer.parseInt(picker.getTag().toString()), newVal);
                    }
                });
                ImageView extraIcon = (ImageView) extraWrap.findViewById(R.id.extraIcon);
                if (Extra.extraIcons.containsKey(String.valueOf(extra.getModelCode()))) {
                    //Icono de este extra
                    extraIcon.setImageDrawable(context.getResources()
                            .getDrawable(Extra.extraIcons.get(String.valueOf(extra.getModelCode()))));
                } else {
                    //Icono por defecto
                    extraIcon.setImageDrawable(context.getResources()
                            .getDrawable(R.drawable.ic_action_new));
                }

                TextView extraName = (TextView) extraWrap.findViewById(R.id.extraName);
                extraName.setText(extra.getName());

                TextView extraPrice = (TextView) extraWrap.findViewById(R.id.extraPrice);
                if (Extra.extraPriceType.containsKey(String.valueOf(extra.getModelCode()))) {
                    //Tipo de precio para este extra
                    extraPrice.setText(
                            extra.getPrice() + "€ / " + context.getString(
                                    Extra.extraPriceType.get(String.valueOf(extra.getModelCode())).equals(Extra.PriceType.DAILY) ? R.string.priceTypeDaily : R.string.priceTypeTotal));
                } else {
                    //Tipo de precio por defecto
                    extraPrice.setText(
                            extra.getPrice() + "€ / " + context.getString(R.string.priceTypeTotal));
                }

                numPicker.setMaxValue(9);
                numPicker.setMinValue(0);
                numPicker.setWrapSelectorWheel(false);
                numPicker.setTag(extra.getCode());
                if (extra != null && extrasQuantity.containsKey(extra.getCode())) {
                    numPicker.setValue(extrasQuantity.get(extra.getCode()));
                } else {
                    numPicker.setValue(0);
                }

                extrasContainer.addView(extraWrap);

            }

        } else {
            //Error
            //Mostramos el error
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
     * Mostramos un diálogo indicando que se está consultando la disponibilidad
     * de los extras
     */
    protected void onPreExecute() {
        progress = new ProgressDialog(context);
        progress.setTitle(context.getString(R.string.getting_extras_title));
        progress.setMessage(context.getString(R.string.getting_extras));
        progress.show();
    }

    public HashMap<Integer, Integer> getExtrasQuantity() {
        return this.extrasQuantity;
    }

    public List<Extra> getResult(){ return result; }
}