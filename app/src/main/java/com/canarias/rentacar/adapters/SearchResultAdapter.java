package com.canarias.rentacar.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.canarias.rentacar.HomeActivity;
import com.canarias.rentacar.R;
import com.canarias.rentacar.SelectExtrasFragment;
import com.canarias.rentacar.async.ImageDownloader;
import com.canarias.rentacar.model.CarAttribute;
import com.canarias.rentacar.model.SearchResult;

import java.util.Iterator;
import java.util.List;

/**
 * Created by David on 30/10/2014.
 * Adapter que gestiona la lista de resultados de búsqueda
 */
public class SearchResultAdapter extends ArrayAdapter<SearchResult> {

    //Módulo que descarga las imágenes
    private final ImageDownloader imageDownloader;
    //Argumentos actuales de la Activity para pasar al siguiente paso
    //de la reserva
    Bundle currentArgs;
    private int resource;
    private LayoutInflater inflater;
    private Context context;

    //String con los extras que se han obtenido en la respuesta de disponibilidad
    //serializados para pasar al siguiente paso de la reserva
    private String extrasString;

    public SearchResultAdapter(Context context, int resourceId,
                               List<SearchResult> objects, String extrasString, Bundle currentArgs) {
        super(context, resourceId, objects);
        resource = resourceId;
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.extrasString = extrasString;
        this.currentArgs = currentArgs;
        imageDownloader = new ImageDownloader(9999, getContext());
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

        final SearchResult sr = getItem(position);

        SearchResultViewCache viewCache;

        if (convertView == null) {
            convertView = inflater.inflate(resource, null);
            viewCache = new SearchResultViewCache(convertView);
            convertView.setTag(viewCache);
        } else {
            viewCache = (SearchResultViewCache) convertView.getTag();
        }

        TextView txtModel = viewCache.getCarModel();
        txtModel.setText(sr.getDescription());

        TextView txtPrice = viewCache.getPrice();
        txtPrice.setText(sr.getTotalPrice() + "€");

        //Mostramos los iconos de las características del coche
        LinearLayout attContainer = viewCache.getAttributesContainer();
        attContainer.removeAllViews();
        if (sr.getCar() != null && sr.getCar().getAttributes() != null) {
            Iterator<CarAttribute> atts = sr.getCar().getAttributes().iterator();

            while (atts.hasNext()) {
                CarAttribute att = atts.next();

                if (CarAttribute.attributeIcons.containsKey(att.getFilename())) {

                    ImageView image = new ImageView(getContext());
                    image.setImageDrawable(
                            getContext().getResources()
                                    .getDrawable(CarAttribute.attributeIcons.get(att.getFilename())));

                    LinearLayout.LayoutParams vp =
                            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                    vp.setMargins(6, 0, 6, 0);
                    image.setLayoutParams(vp);

                    attContainer.addView(image);
                }
            }
        }
        LinearLayout wrapView = viewCache.getWrapView();
        //Establecemos el evento click para mostrar el siguiente paso
        //de la reserva al seleccionar un coche de la lista.
        wrapView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                SelectExtrasFragment fragment = SelectExtrasFragment.newInstance(1,
                        sr.getIdentifier(),
                        sr.getImageUrl(),
                        Float.parseFloat(sr.getTotalPrice().replace(" Euro", "").replace(".", "").replace(",", ".")),
                        sr.getDescription(),
                        extrasString
                );
                Bundle args = fragment.getArguments();
                args.putAll(currentArgs);
                fragment.setArguments(args);

                ((HomeActivity) context).getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, fragment)
                        .addToBackStack(null)
                        .commit();

            }

        });

        ImageView imageCar = viewCache.getImageCar();

        // Convertimos los espacios a %20 en la url de la imagen
        // para evitar errores en la descarga
        imageDownloader.download(sr.getImageUrl()
                .replaceAll(" ", "%20"), imageCar);

        return convertView;

    }
    /**
     * Wrapper que se asocia a la vista de cada item de la lista
     * para almacenar las vistas que se van a modificar
     * en el método getView()
     */
    private class SearchResultViewCache {
        private LinearLayout wrapView;
        private View baseView;
        private TextView carModel;
        private TextView price;
        private ImageView imageCar;
        private LinearLayout attributesContainer;

        public SearchResultViewCache(View baseView) {

            this.baseView = baseView;
        }



        public LinearLayout getWrapView() {
            if (wrapView == null) {
                wrapView = (LinearLayout) baseView.findViewById((R.id.searchResultWrapperLayout));
            }
            return wrapView;
        }

        public TextView getCarModel() {
            if (carModel == null) {
                carModel = (TextView) baseView.findViewById(R.id.txtCarModel);
            }
            return carModel;
        }

        public TextView getPrice() {
            if (price == null) {
                price = (TextView) baseView.findViewById(R.id.txtCarPrice);
            }
            return price;
        }

        public ImageView getImageCar() {
            if (imageCar == null) {
                imageCar = (ImageView) baseView.findViewById(R.id.imageCar);
            }
            return imageCar;
        }

        public LinearLayout getAttributesContainer() {
            if (attributesContainer == null) {
                attributesContainer = (LinearLayout) baseView
                        .findViewById(R.id.searchResultAttributesContainer);
            }
            return attributesContainer;
        }
    }

}
