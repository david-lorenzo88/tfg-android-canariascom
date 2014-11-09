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
import com.canarias.rentacar.model.Reservation;
import com.canarias.rentacar.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

/**
 * Created by David on 30/10/2014.
 */
public class ReservationListAdapter extends ArrayAdapter<Reservation> {

    Context context;
    List<Reservation> resList;
    int layoutResID;

    public ReservationListAdapter(Context context, int layoutResourceID,
                                  List<Reservation> listItems) {
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
            drawerHolder.model = (TextView) view
                    .findViewById(R.id.txtCarModel);

            drawerHolder.localizer = (TextView) view
                    .findViewById(R.id.localizer);

            drawerHolder.date = (TextView) view
                    .findViewById(R.id.reservationDates);

            drawerHolder.price = (TextView) view
                    .findViewById(R.id.txtCarPrice);

            drawerHolder.statusIcon = (ImageView) view
                    .findViewById(R.id.reservation_list_icon_status);

            drawerHolder.extras = (TextView) view
                    .findViewById(R.id.reservationExtras);


            view.setTag(drawerHolder);

        } else {
            drawerHolder = (ReservationItemHolder) view.getTag();

        }

        Reservation dItem = (Reservation) this.resList.get(position);

        drawerHolder.model.setText(Utils.trimStringToMaxSize(dItem.getCar().getModel(), 23));
        drawerHolder.price.setText(String.format("%.02f", dItem.getPrice().getAmount()) + "€");

        int extrasCount = 0;
        Iterator<Extra> it = dItem.getExtras().iterator();

        while (it.hasNext()) {
            extrasCount += it.next().getQuantity();
        }
        drawerHolder.extras.setText(extrasCount + " " + context.getString(R.string.extras));

        //fecha
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yy");
        String dateText = sdf.format(dItem.getStartDate()) + " - " + sdf.format(dItem.getEndDate());
        drawerHolder.date.setText(dateText);

        drawerHolder.statusIcon.setImageDrawable(context.getResources().getDrawable(getStatusIconDrawableId(dItem.getState())));


        drawerHolder.localizer.setText(dItem.getLocalizer());

        return view;
    }

    private int getStatusIconDrawableId(String state) {
        if (state.toLowerCase().contains("confirm")) {
            return R.drawable.ic_done_black_48dp;
        } else if (state.toLowerCase().contains("curso")) {
            return R.drawable.ic_schedule_black_48dp;
        } else {
            return R.drawable.ic_cancel_black_48dp;
        }
    }

    private static class ReservationItemHolder {
        TextView model;
        TextView date;
        TextView localizer;
        TextView extras;
        TextView price;
        ImageView statusIcon;

    }
}

