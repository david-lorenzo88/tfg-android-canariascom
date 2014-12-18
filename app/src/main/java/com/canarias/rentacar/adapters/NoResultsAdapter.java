package com.canarias.rentacar.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.canarias.rentacar.HomeActivity;
import com.canarias.rentacar.R;
import com.canarias.rentacar.SearchFragment;

import java.util.List;

/**
 * Created by David on 09/12/2014.
 * Muestra un mensaje de No Resultados en el listado de reservas
 * cuando no hay reservas aún. Asi como un botón para iniciar una nueva reserva
 */
public class NoResultsAdapter extends ArrayAdapter<String> {

    private List<String> items;
    private Context context;
    private int layoutResourceId;

    public NoResultsAdapter(Context context, int layoutResourceId, List<String> items ){
        super(context, layoutResourceId, items);
        this.context = context;
        this.items = items;
        this.layoutResourceId = layoutResourceId;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String item = items.get(position);

        if(item != null) {

            if (convertView == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();


                convertView = inflater.inflate(layoutResourceId, parent, false);

                TextView msg = (TextView) convertView.findViewById(R.id.msg);
                msg.setText(item);

                convertView.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(context, HomeActivity.class);
                        intent.putExtra(HomeActivity.DEFAULT_ACTION,
                                HomeActivity.DRAWER_POSITION_NEW_BOOKING);
                        intent.putExtra(HomeActivity.DONT_OPEN_DRAWER, true);
                        context.startActivity(intent);

                    }
                });
            }

        }

        return convertView;
    }

}
