package com.canarias.rentacar.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.canarias.rentacar.R;
import com.canarias.rentacar.async.ImageDownloader;
import com.canarias.rentacar.model.Car;

import java.util.List;

/**
 * Created by David on 05/09/2014.
 */
public class CarListAdapter extends ArrayAdapter<Car> {

    private final ImageDownloader imageDownloader;
    Context context;
    List<Car> carItemList;
    int layoutResID;

    public CarListAdapter(Context context, int layoutResourceID,
                          List<Car> listItems) {
        super(context, layoutResourceID, listItems);
        this.context = context;
        this.carItemList = listItems;
        this.layoutResID = layoutResourceID;

        imageDownloader = new ImageDownloader(99999, context);
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


            view.setTag(drawerHolder);

        } else {
            drawerHolder = (CarItemHolder) view.getTag();

        }

        Car dItem = (Car) this.carItemList.get(position);

        imageDownloader.download(dItem.getImageUrl()
                .replaceAll(" ", "%20"), drawerHolder.image);
        //drawerHolder.icon.setImageDrawable(view.getResources().getDrawable(
        //        dItem.getImageUrl()));
        drawerHolder.model.setText(dItem.getModel());

        drawerHolder.category.setText(dItem.getCategory());
        drawerHolder.group.setText(dItem.getGroup());


        return view;
    }

    private static class CarItemHolder {
        TextView model;
        ImageView image;
        TextView category;
        TextView group;

    }
}
