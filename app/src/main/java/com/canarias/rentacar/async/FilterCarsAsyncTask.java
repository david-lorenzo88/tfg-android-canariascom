package com.canarias.rentacar.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import com.canarias.rentacar.R;
import com.canarias.rentacar.adapters.CarListAdapter;
import com.canarias.rentacar.db.dao.CarDataSource;
import com.canarias.rentacar.model.Car;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by David on 11/09/2014.
 */
public class FilterCarsAsyncTask extends
        AsyncTask<String, Void, List<Car>> {

    private ListView lv;
    private ProgressDialog dialog;
    private Context context;

    public FilterCarsAsyncTask(ListView lv) {
        this.lv = lv;
        context = lv.getContext();
        dialog = new ProgressDialog(context);
    }

    protected void onPreExecute() {
        this.dialog.setMessage(context.getString(R.string.filterCarsDialogText));
        this.dialog.show();
    }

    @Override
    protected List<Car> doInBackground(String... params) {

        String category = null;
        List<Car> cars = null;

        if (params.length > 0) {
            category = params[0];
        }

        //If category has default text, set it to null to show all cars
        if (category.equals(context.getString(R.string.spinnerAllText)))
            category = null;


        CarDataSource ds = new CarDataSource(context);

        try {
            ds.open();

            cars = ds.getCarsByCategory(category);

            ds.close();
        } catch (SQLException ex) {
            if (ex != null && ex.getMessage() != null)
                Log.e("TEST", ex.getMessage());
        }

        return cars;
    }

    @Override
    protected void onPostExecute(final List<Car> cars) {


        if (cars != null) {
            CarListAdapter adapter = new CarListAdapter(
                    context,
                    R.layout.car_list_item,
                    cars);
            lv.setAdapter(adapter);

            adapter.notifyDataSetChanged();

        }

        if (dialog.isShowing()) {
            dialog.dismiss();
        }


    }
}
