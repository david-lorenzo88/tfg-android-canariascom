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
import com.canarias.rentacar.model.Extra;
import com.canarias.rentacar.model.Office;
import com.canarias.rentacar.model.Reservation;
import com.canarias.rentacar.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

/**
 * Created by David on 30/10/2014.
 */
public class OfficeListAdapter extends ArrayAdapter<Office> {

    Context context;
    List<Office> resList;
    int layoutResID;

    public OfficeListAdapter (Context context, int layoutResourceID,
                                  List<Office> listItems) {
        super(context, layoutResourceID, listItems);
        this.context = context;
        this.resList = listItems;
        this.layoutResID = layoutResourceID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ReservationItemHolder drawerHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            drawerHolder = new ReservationItemHolder();

            view = inflater.inflate(layoutResID, parent, false);
            drawerHolder.name = (TextView) view
                    .findViewById(R.id.txtOfficeName);

            drawerHolder.zone = (TextView) view
                    .findViewById(R.id.txtOfficeZone);

            drawerHolder.phone = (TextView) view
                    .findViewById(R.id.txtOfficePhone);

            drawerHolder.address = (TextView) view
                    .findViewById(R.id.txtOfficeAddress);




            view.setTag(drawerHolder);

        } else {
            drawerHolder = (ReservationItemHolder) view.getTag();

        }

        Office dItem = (Office) this.resList.get(position);

        drawerHolder.name.setText(Utils.trimStringToMaxSize(dItem.getName(), 23));
        drawerHolder.zone.setText(dItem.getZone().getName());
        drawerHolder.phone.setText(dItem.getPhone());
        drawerHolder.address.setText(dItem.getAddress());



        return view;
    }



    private static class ReservationItemHolder {
        TextView name;
        TextView zone;
        TextView phone;
        TextView address;
    }
}

