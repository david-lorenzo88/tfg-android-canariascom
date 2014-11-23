package com.canarias.rentacar.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.canarias.rentacar.CarListFragment;
import com.canarias.rentacar.OfficeListFragment;
import com.canarias.rentacar.R;
import com.canarias.rentacar.async.ImageDownloader;
import com.canarias.rentacar.model.Car;
import com.canarias.rentacar.model.Office;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 05/09/2014.
 */
public class CarListAdapter extends ArrayAdapter<Car> {

    private final ImageDownloader imageDownloader;
    Context context;
    List<Car> carItemList;
    List<Car> filteredData;
    int layoutResID;
    private int selectedIndex = -1;
    private ItemFilter mFilter = new ItemFilter();

    public CarListAdapter(Context context, int layoutResourceID,
                          List<Car> listItems) {
        super(context, layoutResourceID, listItems);
        this.context = context;
        this.carItemList = listItems;
        this.layoutResID = layoutResourceID;
        this.filteredData = listItems;
        imageDownloader = new ImageDownloader(99999, context);

        Log.v("CAR", "CarListAdapter Creation "+listItems.size());
    }

    public void setSelectedIndex(int ind)
    {
        selectedIndex = ind;
        Log.v("ADAPTER", "selectedIndex SETTER = " + selectedIndex);
        notifyDataSetChanged();
    }

    public int getCount() {
        return filteredData.size();
    }

    public Car getItem(int position) {
        return filteredData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    public Filter getFilter() {
        return mFilter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        CarItemHolder drawerHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            drawerHolder = new CarItemHolder();

            view = inflater.inflate(layoutResID, parent, false);
            drawerHolder.model = (TextView) view
                    .findViewById(R.id.car_model);
            drawerHolder.image = (ImageView) view.findViewById(R.id.car_photo);

            drawerHolder.category = (TextView) view.findViewById(R.id.car_list_category_value);
            drawerHolder.group = (TextView) view.findViewById(R.id.car_list_group_value);
            drawerHolder.wrap = (LinearLayout) view.findViewById(R.id.carListItemWrapper);

            view.setTag(drawerHolder);

        } else {
            drawerHolder = (CarItemHolder) view.getTag();

        }

        Car dItem = this.filteredData.get(position);

        imageDownloader.download(dItem.getImageUrl()
                .replaceAll(" ", "%20"), drawerHolder.image);
        //drawerHolder.icon.setImageDrawable(view.getResources().getDrawable(
        //        dItem.getImageUrl()));
        drawerHolder.model.setText(dItem.getModel());

        drawerHolder.category.setText(dItem.getCategory());
        drawerHolder.group.setText(dItem.getGroup());


        if(selectedIndex!= -1 && position == selectedIndex)
        {
            view.setSelected(true);
            if(Build.VERSION.SDK_INT >= 16)
                drawerHolder.wrap.setBackground(context.getResources().getDrawable(
                        R.drawable.border_bottom_selected));
            else
                drawerHolder.wrap.setBackgroundDrawable(context.getResources().getDrawable(
                        R.drawable.border_bottom_selected));
        } else {
            view.setSelected(false);
            if(Build.VERSION.SDK_INT >= 16)
                drawerHolder.wrap.setBackground(context.getResources().getDrawable(
                        R.drawable.list_selector));
            else
                drawerHolder.wrap.setBackgroundDrawable(context.getResources().getDrawable(
                        R.drawable.list_selector));
        }

        return view;
    }

    private class ItemFilter extends Filter {
        @Override
        protected Filter.FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString();
            Log.v("CAR", "Filtering... "+filterString);

            FilterResults results = new FilterResults();

            final List<Car> list = carItemList;

            int count = list.size();
            final ArrayList<Car> nlist = new ArrayList<Car>(count);

            Car car ;

            if(!filterString.equals(context.getString(R.string.spinnerAllText))) {

                for (int i = 0; i < count; i++) {
                    car = list.get(i);
                    if (car.getCategory().equals(filterString)) {
                        nlist.add(car);
                    }
                }

                results.values = nlist;
                results.count = nlist.size();

            } else {
                results.values = list;
                results.count = list.size();
            }



            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<Car>) results.values;
            selectedIndex = -1;
            notifyDataSetChanged();
        }

    }

    private static class CarItemHolder {
        TextView model;
        ImageView image;
        TextView category;
        TextView group;
        LinearLayout wrap;
    }
}
