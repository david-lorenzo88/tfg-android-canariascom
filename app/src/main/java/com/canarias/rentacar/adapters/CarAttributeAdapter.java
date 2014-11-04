package com.canarias.rentacar.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.canarias.rentacar.R;
import com.canarias.rentacar.model.CarAttribute;

import java.util.List;

/**
 * Created by David on 05/09/2014.
 */
public class CarAttributeAdapter extends ArrayAdapter<CarAttribute> {

    Context context;
    List<CarAttribute> attList;
    int layoutResID;


    public CarAttributeAdapter(Context context, int layoutResourceID,
                               List<CarAttribute> listItems) {
        super(context, layoutResourceID, listItems);
        this.context = context;
        this.attList = listItems;
        this.layoutResID = layoutResourceID;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        CarAttributeItemHolder drawerHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            drawerHolder = new CarAttributeItemHolder();

            view = inflater.inflate(layoutResID, parent, false);
            drawerHolder.name = (TextView) view
                    .findViewById(R.id.car_detail_attribute_name);


            drawerHolder.value = (TextView) view.findViewById(R.id.car_detail_attribute_value);


            view.setTag(drawerHolder);

        } else {
            drawerHolder = (CarAttributeItemHolder) view.getTag();

        }

        CarAttribute dItem = (CarAttribute) this.attList.get(position);


        drawerHolder.name.setText(dItem.getName());
        drawerHolder.value.setText(dItem.getValue());

        return view;
    }

    private static class CarAttributeItemHolder {
        TextView name;

        TextView value;

    }
}
