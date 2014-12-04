package com.canarias.rentacar.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
 * Adapter que gestiona la lista de reservas
 */
public class ReservationListAdapter extends ArrayAdapter<Reservation> {

    Context context;
    List<Reservation> resList;
    int layoutResID;
    private int selectedIndex = -1;

    public ReservationListAdapter(Context context, int layoutResourceID,
                                  List<Reservation> listItems) {
        super(context, layoutResourceID, listItems);
        this.context = context;
        this.resList = listItems;
        this.layoutResID = layoutResourceID;
    }
    //Establece el ítem seleccionado
    public void setSelectedIndex(int ind)
    {
        selectedIndex = ind;
        notifyDataSetChanged();
    }
    /**
     * Construye la vista del item de la lista
     * @param position posición del item
     * @param convertView vista usada anteriormente
     * @param parent vista padre
     * @return la nueva vista construída
     */
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

            drawerHolder.wrap = (LinearLayout) view.findViewById(
                    R.id.reservationListItemWrapperLayout);


            view.setTag(drawerHolder);

        } else {
            drawerHolder = (ReservationItemHolder) view.getTag();

        }

        Reservation dItem = this.resList.get(position);

        drawerHolder.model.setText(Utils.trimStringToMaxSize(dItem.getCar().getModel(), 23));
        drawerHolder.price.setText(String.format("%.02f", dItem.getPrice().getAmount()) + "€");

        //Contamos el número de extras que tiene la reserva
        int extrasCount = 0;
        Iterator<Extra> it = dItem.getExtras().iterator();

        while (it.hasNext()) {
            extrasCount += it.next().getQuantity();
        }
        drawerHolder.extras.setText(extrasCount + " " + context.getString(R.string.extras));

        //Formateamos las fechas de recogida y devolución
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yy");
        String dateText = sdf.format(dItem.getStartDate()) + " - " + sdf.format(dItem.getEndDate());
        drawerHolder.date.setText(dateText);

        drawerHolder.statusIcon.setImageDrawable(
                context.getResources().getDrawable(getStatusIconDrawableId(dItem.getState())));

        drawerHolder.localizer.setText(dItem.getLocalizer());

        //Establecemos el color de fondo si el ítem es seleccionado
        //Controlamos la versión del SDK en ejecución para
        //hacerlo siempre de forma compatible.
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

    /**
     * Establece el icono según el estado de la reserva
     * @param state Estado de la reserva
     * @return el identificador del recurso del icono a mostrar
     */
    private int getStatusIconDrawableId(String state) {
        if (state.toLowerCase().contains("confirm")) {
            return R.drawable.ic_done_black_48dp;
        } else if (state.toLowerCase().contains("curso")) {
            return R.drawable.ic_schedule_black_48dp;
        } else {
            return R.drawable.ic_cancel_black_48dp;
        }
    }
    /**
     * Wrapper que se asocia a la vista de cada item de la lista
     * para almacenar las vistas que se van a modificar
     * en el método getView()
     */
    private static class ReservationItemHolder {
        TextView model;
        TextView date;
        TextView localizer;
        TextView extras;
        TextView price;
        ImageView statusIcon;
        LinearLayout wrap;
    }
}

