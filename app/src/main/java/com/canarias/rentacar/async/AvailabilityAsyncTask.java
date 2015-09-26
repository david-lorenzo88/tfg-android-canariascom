package com.canarias.rentacar.async;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.canarias.rentacar.R;
import com.canarias.rentacar.adapters.SearchResultAdapter;
import com.canarias.rentacar.config.Config;
import com.canarias.rentacar.controller.WebServiceController;
import com.canarias.rentacar.db.dao.AttributeDataSource;
import com.canarias.rentacar.model.Car;
import com.canarias.rentacar.model.CarAttribute;
import com.canarias.rentacar.model.Extra;
import com.canarias.rentacar.model.SearchResult;
import com.canarias.rentacar.model.webservice.AvailabilityResponse;
import com.canarias.rentacar.model.webservice.Response;
import com.canarias.rentacar.utils.AnimationHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Tarea en segundo plano que realiza la consulta de disponibilidad de vehiculos
 * y genera el resultado
 */
public class AvailabilityAsyncTask extends
        AsyncTask<Void, Void, List<SearchResult>> {
    //Parámetros de búsqueda (Fecha y zonas de recogida y devolucion, etc.)
    HashMap<String, String> params;
    View rootView;
    Context context;
    //Contendrá el String serializado con los extras disponibles para reservar
    private String mExtrasString;
    //Argumentos actuales de la Activity para pasar al siguiente paso
    //de la reserva
    private Bundle currentArgs;
    //Modelo seleccionado cuando se realiza la búsqueda por modelo
    private String selectedModel;

    public AvailabilityAsyncTask(View rootView,
                                 HashMap<String, String> params, Context context,
                                 Bundle currentArgs) {
        this.params = params;
        this.rootView = rootView;
        this.context = context;
        this.currentArgs = currentArgs;
    }

    public AvailabilityAsyncTask(View rootView,
                                 HashMap<String, String> params, Context context,
                                 Bundle currentArgs, String selectedModel) {
        this.params = params;
        this.rootView = rootView;
        this.context = context;
        this.currentArgs = currentArgs;
        this.selectedModel = selectedModel;
    }

    /**
     * Metodo que se ejecuta en segundo plano y realiza la consulta de disponibilidad
     * al servicio web.
     * @param params Void
     * @return Lista de resultados
     */
    @Override
    protected List<SearchResult> doInBackground(Void... params) {
        //Realizamos la consulta de disponibilidad al servicio web
        WebServiceController wsc = new WebServiceController();
        Response result = wsc.availability(
                this.params.get(Config.ARG_PICKUP_DATE),
                this.params.get(Config.ARG_PICKUP_TIME),
                this.params.get(Config.ARG_DROPOFF_DATE),
                this.params.get(Config.ARG_DROPOFF_TIME),
                this.params.get(Config.ARG_PICKUP_POINT),
                this.params.get(Config.ARG_DROPOFF_POINT));

        //Comprobamos si el tipo devuelto es de tipo AvailabilityResponse
        if (result != null && result.getClass().equals(AvailabilityResponse.class)) {

            //Obtenemos la lista de resultados
            List<SearchResult> cars = ((AvailabilityResponse) result).getCars();



            if (cars == null)
                cars = new ArrayList<SearchResult>();

            //Obtenemos la lista de extras
            List<Extra> extras = ((AvailabilityResponse) result).getExtras();
            //Serializamos la lista de extras para pasarlos al siguiente paso
            //de la reserva
            mExtrasString = getExtrasString(extras);

            AttributeDataSource ds = new AttributeDataSource(context);
            try {

                //Para cada vehículo devuelto en la respuesta de disponibilidad
                //sacamos su lista de características para mostrarlas

                ds.open();


                Iterator<SearchResult> it = cars.iterator();

                List<SearchResult> resultWithAtts = new ArrayList<SearchResult>();

                while (it.hasNext()) {
                    SearchResult current = it.next();
                    if (current.getCar() == null)
                        current.setCar(new Car());

                    if (selectedModel == null || selectedModel.equals(current.getDescription())) {

                        current.getCar().setAttributes(
                                (ArrayList<CarAttribute>) ds.getCarAttributes(current.getDescription()));


                        resultWithAtts.add(current);

                    }

                }

                ds.close();

                //Devolvemos el resultado
                return resultWithAtts;


            } catch (SQLException ex) {
                Log.e("TEST", ex.getMessage());
            }  catch (Exception ex){
                Log.e("TEST", ex.getMessage());
            }


            return cars;
        } else
            return null;
    }

    /**
     * Ejecutado al finalizar el doInBackground().
     * Actualiza la interfaz gráfica mostrando el resultado
     * @param result Resultado generado por el doInBackground()
     */
    protected void onPostExecute(List<SearchResult> result) {


        if (result != null && result.size() > 0) {
            //Hay resultados

            //Creamos el Adapter
            SearchResultAdapter resultsAdapter = new SearchResultAdapter(
                    context, R.layout.search_result, result, mExtrasString,
                    currentArgs);


            ListView resultsListView = (ListView) rootView
                    .findViewById(R.id.listViewSearchResults);


            resultsListView.setAdapter(resultsAdapter);

            final View summary = rootView.findViewById(R.id.searchResultSummary);

            //Establecemos un listener que nos capture la acción de scroll sobre la ListView
            //Para ocultar el resumen de busqueda al hacer scroll hacia abajo
            //y mostrarlo al hacer scroll hacia arriba
            /*resultsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                private int mLastFirstVisibleItem;
                private boolean isCollapsed = false;
                private boolean firstItem = false;

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {

                    if (mLastFirstVisibleItem < firstVisibleItem && !isCollapsed && !firstItem) {
                        //Si hacemos scroll hacia abajo
                        //colapsamos el resumen
                        Log.v("TEST2", "scroll down");
                        AnimationHelper.collapse(summary);
                        firstItem = true;
                        new CountDownTimer(1500, 1500) {

                            public void onTick(long millisUntilFinished) {

                            }

                            public void onFinish() {
                                Log.v("TEST2", "timer down finished");
                                isCollapsed = true;
                            }
                        }.start();

                    }
                    if (mLastFirstVisibleItem > firstVisibleItem && isCollapsed && !firstItem) {
                        //Si hacemos scroll hacia arriba
                        //expandimos el resumen
                        AnimationHelper.expand(summary);
                        firstItem = true;
                        Log.v("TEST2", "scroll up");
                        new CountDownTimer(1500, 1500) {

                            public void onTick(long millisUntilFinished) {

                            }

                            public void onFinish() {
                                Log.v("TEST2", "timer up finished");
                                isCollapsed = false;
                            }
                        }.start();

                    }
                    //Guardamos siempre referencia al primer item visible
                    //de la lista para saber si estamos haciendo scroll
                    //hacia arriba o hacia abajo
                    mLastFirstVisibleItem = firstVisibleItem;

                    if (firstItem) {
                        firstItem = false;
                        Log.v("TEST2", "first item");
                    }

                }
            });*/


            rootView.findViewById(R.id.searchResultsContainer)
                    .setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.loadingLayout)
                    .setVisibility(View.GONE);
        } else {
            //No hay resultados, creamos un diálogo para mostrar mensaje al usuario

            rootView.findViewById(R.id.loadingLayout).setVisibility(View.GONE);

            final AlertDialog loadingDialog = new AlertDialog.Builder(context).create();

            loadingDialog.setTitle(context.getString(R.string.availability_error_dialog_title));

            loadingDialog.setButton(Dialog.BUTTON_POSITIVE, context.getString(R.string.accept),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadingDialog.dismiss();
                        }
                    });

            if (result == null)
                loadingDialog.setMessage(context.getString(R.string.availability_error_dialog_msg));

            if (result != null && result.size() == 0)
                loadingDialog.setMessage(context.getString(R.string.availability_error_dialog_msg_no_results));

            loadingDialog.show();
        }
    }

    /**
     * Ejecutado antes de comenzar el doInBackground()
     * Mostramos el Spinner y un mensaje indicando que se
     * está realizando la consulta de disponibilidad al servicio web.
     */
    protected void onPreExecute() {
        rootView.findViewById(R.id.loadingLayout).setVisibility(
                View.VISIBLE);

    }

    /**
     * Método que serializa una lista de extras a String
     * @param extras Lista de extras
     * @return String serializado
     */
    private String getExtrasString(List<Extra> extras) {
        StringBuilder builder = new StringBuilder();
        for (Extra extra : extras) {
            builder.append(extra.getCode());
            builder.append("##");
            builder.append(extra.getModelCode());
            builder.append("##");
            builder.append(extra.getName());
            builder.append("##");
            builder.append(extra.getXmlPrice().replace(" Euro", "").replace(".", "").replace(",", "."));
            builder.append("__");
        }
        return builder.toString().substring(0, builder.length() - 2);
    }
}