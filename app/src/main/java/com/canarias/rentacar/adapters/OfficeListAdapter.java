package com.canarias.rentacar.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.canarias.rentacar.OfficeListFragment;
import com.canarias.rentacar.R;
import com.canarias.rentacar.model.Office;
import com.canarias.rentacar.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 30/10/2014.
 * Adapter que gestiona las listas de oficinas mostradas en la aplicación.
 * Implementa la intefaz Filterable para permitir el filtrado de la lista
 * de items.
 */
public class OfficeListAdapter extends ArrayAdapter<Office> implements Filterable {

    Context context;
    //Lista original
    List<Office> resList;
    //Lista filtrada
    List<Office> filteredData;
    int layoutResID;
    private int selectedIndex = -1;
    //Filtrador
    private ItemFilter mFilter = new ItemFilter();

    public OfficeListAdapter (Context context, int layoutResourceID,
                                  List<Office> listItems) {
        super(context, layoutResourceID, listItems);
        this.context = context;
        this.resList = listItems;
        this.layoutResID = layoutResourceID;
        this.filteredData = listItems;
    }

    //Establece el ítem seleccionado
    public void setSelectedIndex(int ind)
    {
        selectedIndex = ind;
        notifyDataSetChanged();
    }

    public int getCount() {
        return filteredData.size();
    }

    public Office getItem(int position) {
        return filteredData.get(position);
    }

    public long getItemId(int position) {
        return position;
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
        ItemHolder drawerHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            drawerHolder = new ItemHolder();

            view = inflater.inflate(layoutResID, parent, false);
            drawerHolder.name = (TextView) view
                    .findViewById(R.id.txtOfficeName);

            drawerHolder.zone = (TextView) view
                    .findViewById(R.id.txtOfficeZone);

            drawerHolder.phone = (TextView) view
                    .findViewById(R.id.txtOfficePhone);

            drawerHolder.address = (TextView) view
                    .findViewById(R.id.txtOfficeAddress);

            drawerHolder.wrap = (LinearLayout) view.findViewById(R.id.officeListItemWrapperLayout);


            view.setTag(drawerHolder);

        } else {
            drawerHolder = (ItemHolder) view.getTag();

        }

        Office dItem = this.filteredData.get(position);

        drawerHolder.name.setText(Utils.trimStringToMaxSize(dItem.getName(), 23));
        drawerHolder.zone.setText(dItem.getZone().getName());
        drawerHolder.phone.setText(dItem.getPhone());
        drawerHolder.address.setText(dItem.getAddress());


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

    public Filter getFilter() {
        return mFilter;
    }
    //Clase Helper para filtrar la lista de oficinas
    private class ItemFilter extends Filter {
        /**
         * Método que realiza el filtrado
         * @param constraint restricción para filtrar la lista
         * @return Resultados del filtrado
         */
        @Override
        protected Filter.FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<Office> list = resList;

            int count = list.size();
            final ArrayList<Office> nlist = new ArrayList<Office>(count);

            Office office ;

            if(Integer.valueOf(filterString) != OfficeListFragment.ZONE_CODE_ALL) {

                for (int i = 0; i < count; i++) {
                    office = list.get(i);
                    if (office.getZone().getCode() == Integer.parseInt(filterString)) {
                        nlist.add(office);
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
        /**
         * Callback Ejecutado al finalizar el filtrado para publicar los resultados
         * @param constraint restricción de filtrado
         * @param results resultados del filtrado
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<Office>) results.values;
            selectedIndex = -1;
            notifyDataSetChanged();
        }

    }

    /**
     * Wrapper que se asocia a la vista de cada item de la lista
     * para almacenar las vistas que se van a modificar
     * en el método getView()
     */
    private static class ItemHolder {
        TextView name;
        TextView zone;
        TextView phone;
        TextView address;
        LinearLayout wrap;
    }
}

