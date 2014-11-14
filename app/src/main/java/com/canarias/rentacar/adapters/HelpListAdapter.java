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
import com.canarias.rentacar.model.HelpSlide;
import com.canarias.rentacar.model.Office;
import com.canarias.rentacar.model.Reservation;
import com.canarias.rentacar.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

/**
 * Created by David on 30/10/2014.
 */
public class HelpListAdapter extends ArrayAdapter<HelpSlide> {

    Context context;
    List<HelpSlide> list;
    int layoutResID;

    public HelpListAdapter (Context context, int layoutResourceID,
                              List<HelpSlide> listItems) {
        super(context, layoutResourceID, listItems);
        this.context = context;
        this.list = listItems;
        this.layoutResID = layoutResourceID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder drawerHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            drawerHolder = new ItemHolder();

            view = inflater.inflate(layoutResID, parent, false);
            drawerHolder.title = (TextView) view
                    .findViewById(R.id.title);

            drawerHolder.subtitle = (TextView) view
                    .findViewById(R.id.subtitle);

            view.setTag(drawerHolder);

        } else {
            drawerHolder = (ItemHolder) view.getTag();

        }

        HelpSlide dItem = this.list.get(position);

        drawerHolder.title.setText(dItem.getTitleAction());
        drawerHolder.subtitle.setText(dItem.getSubtitle());

        return view;
    }



    private static class ItemHolder {
        TextView title;
        TextView subtitle;

    }
}

