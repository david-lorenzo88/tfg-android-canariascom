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
 * Created by David on 04/09/2014.
 */
public class DownloadDestinationsAsyncTask extends
        AsyncTask<Void, Void, String> {

    ProgressDialog progress;
    Context context;

    public DownloadDestinationsAsyncTask(Context context) {

        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        // TODO Auto-generated method stub
        WebServiceController wsc = new WebServiceController();
        Response result = wsc.listDestinations();

        if (result.getClass().equals(ListDestinationsResponse.class)) {
            List<Office> offices = ((ListDestinationsResponse) result).getServicePoints();

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
                return context.getString(R.string.offices_download_ok);

            } catch (SQLException ex) {
                return context.getString(R.string.offices_download_error) + ": " + ex.getMessage();
            }
        } else
            return context.getString(R.string.no_offices_downloaded);
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
        progress.setTitle(context.getString(R.string.downloading_offices));
        progress.setMessage(context.getString(R.string.downloading_offices));
        progress.show();

    }
}
