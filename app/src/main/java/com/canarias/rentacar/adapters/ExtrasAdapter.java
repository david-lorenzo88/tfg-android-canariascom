package com.canarias.rentacar.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.canarias.rentacar.R;
import com.canarias.rentacar.model.Extra;

import java.util.HashMap;
import java.util.List;

/**
 * Created by David on 26/10/2014.
 * Adapter que gestiona las listas de extras
 */
public class ExtrasAdapter extends ArrayAdapter<Extra> {
    private int resource;
    private LayoutInflater inflater;
    private Context context;

    //HashMap que almacena la cantidad de cada extra seleccionada
    //en el momento
    private HashMap<Integer, Integer> extrasQuantity;


    public ExtrasAdapter(Context context, int resourceId,
                         List<Extra> objects) {
        super(context, resourceId, objects);

        this.extrasQuantity = new HashMap<Integer, Integer>();

        //Inicializamos el HashMap con la cantidad de cada extra
        for (Extra e : objects) {

            if (!extrasQuantity.containsKey(e.getCode())) {
                extrasQuantity.put(e.getCode(), e.getQuantity());
            }

        }
        resource = resourceId;
        inflater = LayoutInflater.from(context);
        this.context = context;




    }

    /**
     * Getter
     * @return el HashMap de la cantidad de extras seleccionada
     */
    public HashMap<Integer, Integer> getExtrasQuantity() {
        return extrasQuantity;
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

        final Extra extra = getItem(position);

        ExtraViewCache viewCache;

        if (convertView == null) {
            convertView = inflater.inflate(resource, null);
            viewCache = new ExtraViewCache(convertView);
            convertView.setTag(viewCache);

            //Inicializamos la cantidad de extras seleccionados si aún no se ha hecho
            if (!extrasQuantity.containsKey(extra.getCode())) {
                extrasQuantity.put(extra.getCode(), extra.getQuantity());
            }

            //Establecemos un listener en el NumberPicker para que actualice la cantidad
            //de extras seleccionados cada vez que se produzca un evento de cambio de valor
            NumberPicker numPicker = viewCache.getExtraNumber();
            numPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    extrasQuantity.put(Integer.parseInt(picker.getTag().toString()), newVal);

                }
            });
        } else {

            viewCache = (ExtraViewCache) convertView.getTag();
        }


        ImageView extraIcon = viewCache.getExtraIcon();
        if (Extra.extraIcons.containsKey(String.valueOf(extra.getModelCode()))) {
            //Establecemos el icono personalizado para este extra
            extraIcon.setImageDrawable(context.getResources()
                    .getDrawable(Extra.extraIcons.get(String.valueOf(extra.getModelCode()))));
        } else {
            //Establecemos el icono por defecto
            extraIcon.setImageDrawable(context.getResources()
                    .getDrawable(R.drawable.ic_action_new));
        }

        TextView extraName = viewCache.getExtraName();
        extraName.setText(extra.getName());

        //Establecemos el tipo de precio para el extra (Por día, Por alquiler)
        TextView extraPrice = viewCache.getExtraPrice();
        if (Extra.extraPriceType.containsKey(String.valueOf(extra.getModelCode()))) {

            extraPrice.setText(
                    String.format("%.02f", extra.getPrice()) + "€ / " + context.getString(
                            Extra.extraPriceType.get(String.valueOf(extra.getModelCode())).equals(Extra.PriceType.DAILY) ? R.string.priceTypeDaily : R.string.priceTypeTotal));
        } else {

            extraPrice.setText(
                    String.format("%.02f", extra.getPrice()) + "€ / " + context.getString(R.string.priceTypeTotal));
        }
        //Inicializamos el NumberPicker
        NumberPicker extraNumber = viewCache.getExtraNumber();
        extraNumber.setMaxValue(9);
        extraNumber.setMinValue(0);
        extraNumber.setWrapSelectorWheel(false);
        extraNumber.setTag(extra.getCode());
        if (extrasQuantity.containsKey(extra.getCode())) {
            extraNumber.setValue(extrasQuantity.get(extra.getCode()));
        } else {
            extraNumber.setValue(0);
        }

        return convertView;
    }



    /**
     * Wrapper que se asocia a la vista de cada item de la lista
     * para almacenar las vistas que se van a modificar
     * en el método getView()
     */
    private class ExtraViewCache {

        private ImageView extraIcon;
        private TextView extraName;
        private TextView extraPrice;
        private NumberPicker extraNumber;
        private View baseView;

        public ExtraViewCache(View baseView) {

            this.baseView = baseView;
        }



        public ImageView getExtraIcon() {
            if (extraIcon == null) {
                extraIcon = (ImageView) baseView.findViewById(R.id.extraIcon);
            }
            return extraIcon;
        }

        public TextView getExtraName() {
            if (extraName == null) {
                extraName = (TextView) baseView.findViewById(R.id.extraName);
            }
            return extraName;
        }

        public TextView getExtraPrice() {
            if (extraPrice == null) {
                extraPrice = (TextView) baseView.findViewById(R.id.extraPrice);
            }
            return extraPrice;
        }

        public NumberPicker getExtraNumber() {
            if (extraNumber == null) {
                extraNumber = (NumberPicker) baseView.findViewById(R.id.extraSelector);
            }
            return extraNumber;
        }

    }
}
