package com.canarias.rentacar.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.canarias.rentacar.R;
import com.canarias.rentacar.model.Extra;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by David on 26/10/2014.
 */
public class ExtrasAdapter extends ArrayAdapter<Extra> {
    private int resource;
    private LayoutInflater inflater;
    private Context context;

    private HashMap<Integer, Integer> extrasQuantity;


    public ExtrasAdapter(Context context, int resourceId,
                         List<Extra> objects) {
        super(context, resourceId, objects);

        this.extrasQuantity = new HashMap<Integer, Integer>();

        for (Extra e : objects) {
            Log.v("TEST_EXTRA", e.getCode() + " " + e.getName());

            if (!extrasQuantity.containsKey(e.getCode())) {
                extrasQuantity.put(e.getCode(), e.getQuantity());
            }

        }
        resource = resourceId;
        inflater = LayoutInflater.from(context);
        this.context = context;




    }

    public HashMap<Integer, Integer> getExtrasQuantity() {
        return extrasQuantity;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Extra extra = getItem(position);

        Log.v("TEST2", extra.getName());
        Log.v("TEST2", String.valueOf(extra.getPrice()));
        Log.v("TEST2", String.valueOf(extra.getCode()));
        Log.v("TEST2", String.valueOf(extra.getModelCode()));

        ExtraViewCache viewCache;

        if (convertView == null) {
            convertView = (LinearLayout) inflater.inflate(resource, null);
            viewCache = new ExtraViewCache(convertView);
            convertView.setTag(viewCache);

            //Init Listeners and HashMaps
            if (!extrasQuantity.containsKey(extra.getCode())) {
                extrasQuantity.put(extra.getCode(), extra.getQuantity());
            }


            NumberPicker numPicker = viewCache.getExtraNumber(resource);
            numPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    extrasQuantity.put(Integer.parseInt(picker.getTag().toString()), newVal);
                    printMap();
                }
            });
        } else {
            convertView = (LinearLayout) convertView;
            viewCache = (ExtraViewCache) convertView.getTag();
        }

        ImageView extraIcon = viewCache.getExtraIcon(resource);
        if (Extra.extraIcons.containsKey(String.valueOf(extra.getModelCode()))) {
            //Set icon from mapping
            extraIcon.setImageDrawable(context.getResources()
                    .getDrawable(Extra.extraIcons.get(String.valueOf(extra.getModelCode()))));
        } else {
            //Set default icon
            extraIcon.setImageDrawable(context.getResources()
                    .getDrawable(R.drawable.ic_action_new));
        }

        TextView extraName = viewCache.getExtraName(resource);
        extraName.setText(extra.getName());

        TextView extraPrice = viewCache.getExtraPrice(resource);
        if (Extra.extraPriceType.containsKey(String.valueOf(extra.getModelCode()))) {
            //Set text
            extraPrice.setText(
                    String.format("%.02f", extra.getPrice()) + "€ / " + context.getString(
                            Extra.extraPriceType.get(String.valueOf(extra.getModelCode())).equals(Extra.PriceType.DAILY) ? R.string.priceTypeDaily : R.string.priceTypeTotal));
        } else {
            //Set default text
            extraPrice.setText(
                    String.format("%.02f", extra.getPrice()) + "€ / " + context.getString(R.string.priceTypeTotal));
        }
        NumberPicker extraNumber = viewCache.getExtraNumber(resource);
        extraNumber.setMaxValue(9);
        extraNumber.setMinValue(0);
        extraNumber.setWrapSelectorWheel(false);
        extraNumber.setTag(extra.getCode());
        if (extra != null && extrasQuantity.containsKey(extra.getCode())) {
            Log.v("KEYS", String.valueOf(extra.getCode()));
            extraNumber.setValue(extrasQuantity.get(extra.getCode()));
        } else {
            extraNumber.setValue(0);
        }

        return convertView;
    }

    private void printMap() {
        Iterator<Integer> it = extrasQuantity.keySet().iterator();
        while (it.hasNext()) {
            Integer c = it.next();
            Log.v("KEYS", c.toString() + " - " + extrasQuantity.get(c).toString());
        }
    }


    private class ExtraViewCache {

        private ImageView extraIcon;
        private TextView extraName;
        private TextView extraPrice;
        private NumberPicker extraNumber;
        private View baseView;

        public ExtraViewCache(View baseView) {

            this.baseView = baseView;
        }

        public View getViewBase() {
            return baseView;
        }

        public ImageView getExtraIcon(int resource) {
            if (extraIcon == null) {
                extraIcon = (ImageView) baseView.findViewById(R.id.extraIcon);
            }
            return extraIcon;
        }

        public TextView getExtraName(int resource) {
            if (extraName == null) {
                extraName = (TextView) baseView.findViewById(R.id.extraName);
            }
            return extraName;
        }

        public TextView getExtraPrice(int resource) {
            if (extraPrice == null) {
                extraPrice = (TextView) baseView.findViewById(R.id.extraPrice);
            }
            return extraPrice;
        }

        public NumberPicker getExtraNumber(int resource) {
            if (extraNumber == null) {
                extraNumber = (NumberPicker) baseView.findViewById(R.id.extraSelector);
            }
            return extraNumber;
        }

    }
}
