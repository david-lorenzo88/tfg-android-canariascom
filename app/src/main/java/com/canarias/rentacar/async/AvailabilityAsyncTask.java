package com.canarias.rentacar.async;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class AvailabilityAsyncTask extends
        AsyncTask<Void, Void, List<SearchResult>> {
    HashMap<String, String> params;
    View rootView;
    Context context;
    private String mExtrasString;
    private Bundle currentArgs;

    public AvailabilityAsyncTask(View rootView,
                                 HashMap<String, String> params, Context context,
                                 Bundle currentArgs) {
        this.params = params;
        this.rootView = rootView;
        this.context = context;
        this.currentArgs = currentArgs;
    }

    @Override
    protected List<SearchResult> doInBackground(Void... params) {
        // TODO Auto-generated method stub
        WebServiceController wsc = new WebServiceController();
        Response result = wsc.availability(
                this.params.get(Config.ARG_PICKUP_DATE),
                this.params.get(Config.ARG_PICKUP_TIME),
                this.params.get(Config.ARG_DROPOFF_DATE),
                this.params.get(Config.ARG_DROPOFF_TIME),
                this.params.get(Config.ARG_PICKUP_POINT),
                this.params.get(Config.ARG_DROPOFF_POINT));

        if (result != null && result.getClass().equals(AvailabilityResponse.class)) {

            List<SearchResult> cars = ((AvailabilityResponse) result).getCars();

            List<Extra> extras = ((AvailabilityResponse) result).getExtras();

            mExtrasString = getExtrasString(extras);

            try {
                AttributeDataSource ds = new AttributeDataSource(context);
                ds.open();


                Iterator<SearchResult> it = cars.iterator();

                List<SearchResult> resultWithAtts = new ArrayList<SearchResult>();

                while (it.hasNext()) {
                    SearchResult current = it.next();
                    if (current.getCar() == null)
                        current.setCar(new Car());

                    current.getCar().setAttributes(
                            (ArrayList<CarAttribute>) ds.getCarAttributes(current.getDescription()));
                    resultWithAtts.add(current);
                }

                ds.close();


                //DEBUG
                /*Iterator<SearchResult> it2 = resultWithAtts.iterator();
                while(it2.hasNext()){
                    SearchResult c = it2.next();
                    Log.v("ATTS", c.getDescription());

                    Iterator<CarAttribute> it3 = c.getCar().getAttributes().iterator();
                    while(it3.hasNext()){
                        CarAttribute at = it3.next();
                        Log.v("ATTS", "\t- "+at.getName());
                    }
                    Log.v("ATTS", "");
                }*/

                return resultWithAtts;
            } catch (SQLException ex) {
                Log.e("TEST", ex.getMessage());
            }

            return cars;
        } else
            return null;
    }

    protected void onPostExecute(List<SearchResult> result) {
        // Meter los resultados en el listview

        if (result != null) {
            //Hay resultados
            SearchResultAdapter resultsAdapter = new SearchResultAdapter(
                    context, R.layout.search_result, result, mExtrasString,
                    currentArgs);


            ListView resultsListView = (ListView) rootView
                    .findViewById(R.id.listViewSearchResults);

            resultsListView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    RelativeLayout p = (RelativeLayout) view;
                    Toast.makeText(context, "aqui", Toast.LENGTH_LONG).show();
                }

            });

            resultsListView.setAdapter(resultsAdapter);


            rootView.findViewById(R.id.searchResultsContainer)
                    .setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.loadingLayout)
                    .setVisibility(View.GONE);
        } else {
            //No hay resultados, mostrar mensaje
        }
    }

    protected void onPreExecute() {
        rootView.findViewById(R.id.loadingLayout).setVisibility(
                View.VISIBLE);

    }

    private String getExtrasString(List<Extra> extras) {
        StringBuilder builder = new StringBuilder();
        for (Extra extra : extras) {
            builder.append(extra.getCode());
            builder.append("##");
            builder.append(extra.getModelCode());
            builder.append("##");
            builder.append(extra.getName());
            builder.append("##");
            builder.append(extra.getXmlPrice().replace(" Euro", "").replace(",", "."));
            builder.append("__");
        }
        Log.v("TEST", builder.toString().substring(0, builder.length() - 2));
        return builder.toString().substring(0, builder.length() - 2);
    }
}