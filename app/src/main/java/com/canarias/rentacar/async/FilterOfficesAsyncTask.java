package com.canarias.rentacar.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import com.canarias.rentacar.OfficeListFragment;
import com.canarias.rentacar.R;
import com.canarias.rentacar.adapters.CarListAdapter;
import com.canarias.rentacar.adapters.OfficeListAdapter;
import com.canarias.rentacar.db.dao.CarDataSource;
import com.canarias.rentacar.db.dao.OfficeDataSource;
import com.canarias.rentacar.db.dao.ZoneDataSource;
import com.canarias.rentacar.model.Car;
import com.canarias.rentacar.model.Office;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by David on 11/09/2014.
 */
public class FilterOfficesAsyncTask extends
        AsyncTask<Integer, Void, List<Office>> {

    private ListView lv;
    private ProgressDialog dialog;
    private Context context;

    public FilterOfficesAsyncTask(ListView lv) {
        this.lv = lv;
        context = lv.getContext();
        dialog = new ProgressDialog(context);
    }

    protected void onPreExecute() {
        this.dialog.setMessage(context.getString(R.string.filterOfficesDialogText));
        this.dialog.show();
    }

    @Override
    protected List<Office> doInBackground(Integer... params) {

        int zoneCode = OfficeListFragment.ZONE_CODE_ALL;
        List<Office> offices = null;

        if (params.length > 0) {
            zoneCode = params[0];
        }



        OfficeDataSource ds = new OfficeDataSource(context);

        try {
            ds.open();

            String sZoneCode = zoneCode ==
                    OfficeListFragment.ZONE_CODE_ALL ? null : String.valueOf(zoneCode);

            offices = ds.getOffices(sZoneCode);

            ds.close();
        } catch (SQLException ex) {
            if (ex != null && ex.getMessage() != null)
                Log.e("TEST", ex.getMessage());
        }

        return offices;
    }

    @Override
    protected void onPostExecute(final List<Office> offices) {


        if (offices != null) {
            OfficeListAdapter adapter = new OfficeListAdapter(
                    context,
                    R.layout.office_list_item,
                    offices);
            lv.setAdapter(adapter);

            adapter.notifyDataSetChanged();

        }

        if (dialog.isShowing()) {
            dialog.dismiss();
        }


    }
}
