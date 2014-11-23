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
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.canarias.rentacar.OfficeListFragment;
import com.canarias.rentacar.R;
import com.canarias.rentacar.model.Extra;
import com.canarias.rentacar.model.Office;
import com.canarias.rentacar.model.Reservation;
import com.canarias.rentacar.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by David on 30/10/2014.
 */
public class OfficeListAdapter extends ArrayAdapter<Office> implements Filterable {

    Context context;
    List<Office> resList;
    List<Office> filteredData;
    int layoutResID;
    private int selectedIndex = -1;
    private ItemFilter mFilter = new ItemFilter();

    public OfficeListAdapter (Context context, int layoutResourceID,
                                  List<Office> listItems) {
        super(context, layoutResourceID, listItems);
        this.context = context;
        this.resList = listItems;
        this.layoutResID = layoutResourceID;
        this.filteredData = listItems;
    }

    public void setSelectedIndex(int ind)
    {
        selectedIndex = ind;
        Log.v("ADAPTER", "selectedIndex SETTER = "+selectedIndex);
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


        Log.v("ADAPTER", "selectedIndex = "+selectedIndex);
        Log.v("ADAPTER", "position = "+position);
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

    private class ItemFilter extends Filter {
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

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<Office>) results.values;
            selectedIndex = -1;
            notifyDataSetChanged();
        }

    }


    private static class ItemHolder {
        TextView name;
        TextView zone;
        TextView phone;
        TextView address;
        LinearLayout wrap;
    }
}

