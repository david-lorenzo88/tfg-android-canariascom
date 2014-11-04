package com.canarias.rentacar.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
public class SearchResultAdapter extends ArrayAdapter<SearchResult> {

    private final ImageDownloader imageDownloader;
    Bundle currentArgs;
    private int resource;
    private LayoutInflater inflater;
    private Context context;
    private Point screen;
    private String extrasString;

    public SearchResultAdapter(Context context, int resourceId,
                               List<SearchResult> objects, String extrasString, Bundle currentArgs) {
        super(context, resourceId, objects);
        resource = resourceId;
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.extrasString = extrasString;
        this.currentArgs = currentArgs;
        /*Display display = context.getWindowManager().getDefaultDisplay();
		screen = new Point();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			display.getSize(screen);
		} else {
			screen.x = display.getWidth();
			screen.y = display.getHeight();
		}*/

        imageDownloader = new ImageDownloader(9999, getContext());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v("CANARIAS", "View pos: " + position);
        final SearchResult sr = getItem(position);

        SearchResultViewCache viewCache;

        if (convertView == null) {
            convertView = (LinearLayout) inflater.inflate(resource, null);
            viewCache = new SearchResultViewCache(convertView);
            convertView.setTag(viewCache);
        } else {
            convertView = (LinearLayout) convertView;
            viewCache = (SearchResultViewCache) convertView.getTag();
        }

        TextView txtModel = viewCache.getCarModel(resource);
        txtModel.setText(sr.getDescription());

        TextView txtPrice = viewCache.getPrice(resource);
        txtPrice.setText(sr.getTotalPrice());

        LinearLayout attContainer = viewCache.getAttributesContainer(resource);
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
        LinearLayout wrapView = viewCache.getWrapView(resource);

        wrapView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub


                SelectExtrasFragment fragment = SelectExtrasFragment.newInstance(1,
                        sr.getIdentifier(),
                        sr.getImageUrl(),
                        Float.parseFloat(sr.getTotalPrice().replace(" Euro", "").replace(",", ".")),
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

        ImageView imageCar = viewCache.getImageCar(resource);
        //imageCar.setLayoutParams(new RelativeLayout.LayoutParams(
        //		(int) (screen.x * 0.30), /*btnBook.getHeight()
        //				+ txtModel.getHeight()*/(int) (screen.x * 0.30)));

        // Convertimos los espacios a %20 en la url de la imagen

        imageDownloader.download(sr.getImageUrl()
                .replaceAll(" ", "%20"), imageCar);

        return convertView;

    }

    public ImageDownloader getImageDownloader() {
        return imageDownloader;
    }

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

        public View getViewBase() {
            return baseView;
        }

        public LinearLayout getWrapView(int resource) {
            if (wrapView == null) {
                wrapView = (LinearLayout) baseView.findViewById((R.id.searchResultWrapperLayout));
            }
            return wrapView;
        }

        public TextView getCarModel(int resource) {
            if (carModel == null) {
                carModel = (TextView) baseView.findViewById(R.id.txtCarModel);
            }
            return carModel;
        }

        public TextView getPrice(int resource) {
            if (price == null) {
                price = (TextView) baseView.findViewById(R.id.txtCarPrice);
            }
            return price;
        }

        public ImageView getImageCar(int resource) {
            if (imageCar == null) {
                imageCar = (ImageView) baseView.findViewById(R.id.imageCar);
            }
            return imageCar;
        }

        public LinearLayout getAttributesContainer(int resource) {
            if (attributesContainer == null) {
                attributesContainer = (LinearLayout) baseView
                        .findViewById(R.id.searchResultAttributesContainer);
            }
            return attributesContainer;
        }
    }

}
