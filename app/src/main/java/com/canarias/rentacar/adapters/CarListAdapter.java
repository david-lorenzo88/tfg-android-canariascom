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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.canarias.rentacar.R;
import com.canarias.rentacar.async.ImageDownloader;
import com.canarias.rentacar.model.Car;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 05/11/2014.
 * Adapter que gestiona las lista de vehículos que se
 * muestran en la aplicación.
 * Implementa la intefaz Filterable para permitir el filtrado de la lista
 * de items.
 */
public class CarListAdapter extends ArrayAdapter<Car> implements Filterable {

    //Módulo para descargar imágenes
    private final ImageDownloader imageDownloader;
    Context context;
    //Lista original
    List<Car> carItemList;
    //Lista filtrada
    List<Car> filteredData;
    int layoutResID;
    private int selectedIndex = -1;
    //Filtrador
    private ItemFilter mFilter = new ItemFilter();


    public CarListAdapter(Context context, int layoutResourceID,
                          List<Car> listItems) {
        super(context, layoutResourceID, listItems);
        this.context = context;
        this.carItemList = listItems;
        this.layoutResID = layoutResourceID;
        this.filteredData = listItems;
        imageDownloader = new ImageDownloader(99999, context);


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

    public Car getItem(int position) {
        return filteredData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    public Filter getFilter() {
        return mFilter;
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
        // TODO Auto-generated method stub

        CarItemHolder drawerHolder;
        View view = convertView;

        //Reciclamos la vista si ya ha sido usada para evitar errores
        //de OutOfMemory
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

        //Obtenemos el ítem de la lista filtrada
        Car dItem = this.filteredData.get(position);

        //Descargamos la imagen
        imageDownloader.download(dItem.getImageUrl()
                .replaceAll(" ", "%20"), drawerHolder.image);

        drawerHolder.model.setText(dItem.getModel());
        drawerHolder.category.setText(dItem.getCategory());
        drawerHolder.group.setText(dItem.getGroup());

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

    //Clase Helper para filtrar la lista de coches
    private class ItemFilter extends Filter {
        /**
         * Método que realiza el filtrado
         * @param constraint restricción para filtrar la lista
         * @return Resultados del filtrado
         */
        @Override
        protected Filter.FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString();
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

        /**
         * Callback Ejecutado al finalizar el filtrado para publicar los resultados
         * @param constraint restricción de filtrado
         * @param results resultados del filtrado
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<Car>) results.values;
            selectedIndex = -1;
            notifyDataSetChanged();
        }

    }

    /**
     * Wrapper que se asocia a la vista de cada item de la lista
     * para almacenar las vistas que se van a modificar
     * en el método getView()
     */
    private static class CarItemHolder {
        TextView model;
        ImageView image;
        TextView category;
        TextView group;
        LinearLayout wrap;
    }
}
