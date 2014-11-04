package com.canarias.rentacar;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.canarias.rentacar.async.ImageDownloader;
import com.canarias.rentacar.db.dao.AttributeDataSource;
import com.canarias.rentacar.db.dao.CarDataSource;
import com.canarias.rentacar.model.Car;
import com.canarias.rentacar.model.CarAttribute;
import com.canarias.rentacar.widgets.ObservableScrollView;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * A fragment representing a single Car detail screen.
 * This fragment is either contained in a {@link CarListActivity}
 * in two-pane mode (on tablets) or a {@link CarDetailActivity}
 * on handsets.
 */
public class CarDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    private Car car;

    private TextView model;
    private ImageView photo;
    private TextView group;
    private TextView category;
    private TextView sippCode;
    private LinearLayout attributes;
    private Drawable mActionBarBackgroundDrawable;

    private ImageDownloader imageDownloader;
    private Drawable.Callback mDrawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            getActivity().getActionBar().setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
        }
    };
    private ObservableScrollView.OnScrollChangedListener mOnScrollChangedListener = new ObservableScrollView.OnScrollChangedListener() {
        public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
            final int headerHeight = who.findViewById(R.id.car_photo).getHeight() - getActivity().getActionBar().getHeight();
            final float ratio = (float) Math.min(Math.max(t, 0), headerHeight) / headerHeight;
            final int newAlpha = (int) (ratio * 255);
            mActionBarBackgroundDrawable.setAlpha(newAlpha);
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CarDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {

            CarDataSource ds = new CarDataSource(getActivity());
            AttributeDataSource attDS = new AttributeDataSource(getActivity());
            try {
                //Open Data sources
                attDS.open();
                ds.open();

                //Get car
                car = ds.getCar(getArguments().getString(ARG_ITEM_ID));

                //Get car attributes
                ArrayList<CarAttribute> atts = new ArrayList<CarAttribute>();
                atts.addAll(attDS.getCarAttributes(getArguments().getString(ARG_ITEM_ID)));
                car.setAttributes(atts);

                //Free resources
                ds.close();
                attDS.close();

                imageDownloader = new ImageDownloader(99999, getActivity());

            } catch (Exception ex) {
                Log.d("TEST", ex.getMessage());
            }


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_car_detail, container, false);

        model = (TextView) rootView.findViewById(R.id.car_model);
        photo = (ImageView) rootView.findViewById(R.id.car_photo);
        group = (TextView) rootView.findViewById(R.id.car_detail_group_value);
        category = (TextView) rootView.findViewById(R.id.car_detail_category_value);
        sippCode = (TextView) rootView.findViewById(R.id.car_detail_sipp_value);
        attributes = (LinearLayout) rootView.findViewById(R.id.car_details_attributes);

        if (car != null && imageDownloader != null) {

            model.setText(car.getModel());

            imageDownloader.download(car.getImageUrl(), photo);

            group.setText(car.getGroup());
            category.setText(car.getCategory());

            if (car.getSippCode() == null || car.getSippCode().isEmpty())
                ((LinearLayout) rootView.findViewById(R.id.car_detail_sipp_wrap)).setVisibility(View.GONE);
            else
                sippCode.setText(car.getSippCode());


            sippCode.setText(car.getSippCode());


            Iterator<CarAttribute> it = car.getAttributes().iterator();
            while (it.hasNext()) {
                CarAttribute current = it.next();
                View attWrapper = inflater.inflate(R.layout.fragment_car_detail_attribute, container, false);
                TextView attName = (TextView) attWrapper.findViewById(R.id.car_detail_attribute_name);
                TextView attValue = (TextView) attWrapper.findViewById(R.id.car_detail_attribute_value);

                attName.setText(current.getName());
                attValue.setText(current.getValue());

                attributes.addView(attWrapper);
            }

            /*attributes.setAdapter(new CarAttributeAdapter(
                    getActivity(),
                    R.layout.fragment_car_detail_attribute,
                    car.getAttributes()));*/

        }
        // Show the dummy content as text in a TextView.
        //if (mItem != null) {
        //((TextView) rootView.findViewById(R.id.car_detail)).setText(mItem.content);
        //}

        mActionBarBackgroundDrawable = getResources().getDrawable(R.drawable.actionbar_background);
        mActionBarBackgroundDrawable.setAlpha(0);

        getActivity().getActionBar().setBackgroundDrawable(mActionBarBackgroundDrawable);

        ((ObservableScrollView) rootView.findViewById(R.id.car_detail_container))
                .setOnScrollChangedListener(mOnScrollChangedListener);

        //Fix for pre-JELLY_BEAN_MR1 devices
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mActionBarBackgroundDrawable.setCallback(mDrawableCallback);
        }


        return rootView;
    }
}
