package com.canarias.rentacar;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.canarias.rentacar.config.Config;
import com.canarias.rentacar.db.dao.OfficeDataSource;
import com.canarias.rentacar.model.Office;
import com.canarias.rentacar.widgets.ObservableScrollView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;


/**
 * A fragment representing a single Office detail screen.
 * This fragment is either contained in a {@link OfficeListActivity}
 * in two-pane mode (on tablets) or a {@link OfficeDetailActivity}
 * on handsets.
 */
public class OfficeDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    private Drawable mActionBarBackgroundDrawable;
    /**
     * The dummy content this fragment is presenting.
     */
    private Office mItem;
    //Vista del mapa
    private MapView mapView;

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
    //Listener para gestionar la ocultación de la ActionBar al hacer scroll
    private ObservableScrollView.OnScrollChangedListener mOnScrollChangedListener = new ObservableScrollView.OnScrollChangedListener() {
        public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
            final int headerHeight = who.findViewById(R.id.map).getHeight() - getActivity().getActionBar().getHeight();
            final float ratio = (float) Math.min(Math.max(t, 0), headerHeight) / headerHeight;
            final int newAlpha = (int) (ratio * 255);
            mActionBarBackgroundDrawable.setAlpha(newAlpha);

            if(newAlpha > 50) {
                getActivity().getActionBar().setDisplayShowTitleEnabled(true);
                getActivity().getActionBar().setDisplayShowHomeEnabled(true);
            } else {
                getActivity().getActionBar().setDisplayShowTitleEnabled(false);
                getActivity().getActionBar().setDisplayShowHomeEnabled(false);

            }
        }
    };


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OfficeDetailFragment() {
    }

    /**
     * Crea el fragmento
     * @param savedInstanceState estado previo
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            //Cargamos la oficina desde base de datos
            OfficeDataSource ds = new OfficeDataSource(getActivity());
            try {
                ds.open();
                mItem = ds.getOffice(getArguments().getString(ARG_ITEM_ID));
                ds.close();

                getActivity().setTitle(mItem.getName());

            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    /**
     * Llamado cuando el sistema operativo crea la vista del fragmento
     * @param inflater objeto para inflar las vistas
     * @param container la vista padre a la que el fragmento será asociado
     * @param savedInstanceState estado previo del fragmento cuando se está reconstruyendo
     * @return la vista generada para el fragmento
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_office_detail, container, false);


        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.txtOfficeName)).setText(mItem.getName());

            ((TextView) rootView.findViewById(R.id.txtOfficeAddress)).setText(mItem.getAddress());

            ((TextView) rootView.findViewById(R.id.txtOfficePhone)).setText(mItem.getPhone());

            ((TextView) rootView.findViewById(R.id.txtOfficeFax)).setText(mItem.getFax());

            ((TextView) rootView.findViewById(R.id.txtOfficeZone)).setText(mItem.getZone().getName());

            WebView wvDeliveryCond = (WebView) rootView.findViewById(R.id.webViewOfficeDeliveryConditions);

            wvDeliveryCond.loadDataWithBaseURL("fake://not/needed", wrapHtml(mItem.getDeliveryConditions()), "text/html", "UTF-8", "");


            WebView wvReturnCond = (WebView) rootView.findViewById(R.id.webViewOfficeReturnConditions);

            wvReturnCond.loadDataWithBaseURL("fake://not/needed", wrapHtml(mItem.getReturnConditions()), "text/html", "UTF-8", "");

            mapView = (MapView) rootView.findViewById(R.id.map);
            mapView.onCreate(savedInstanceState);

            //Si tenemos las coordenadas de la oficina, mostramos su posición en el mapa
            if (mItem.getLatitude() != 0 && mItem.getLongitude() != 0) {


                GoogleMap map = mapView.getMap();
                if (map != null) {
                    map.setMyLocationEnabled(true);

                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.marker);

                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(mItem.getLatitude(), mItem.getLongitude()))
                            .title(mItem.getName())
                            .icon(icon));

                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mItem.getLatitude(),
                            mItem.getLongitude()), 9.0f));
                }
            }
            //Evento para abrir el mapa en una nueva Activity
            rootView.findViewById(R.id.mapOpen).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentMap fragment = FragmentMap.newInstance(mItem.getCode(), -1);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.office_detail_container, fragment)
                            .commit();
                }
            });
            //Configuración específica para smartphones
            boolean isTablet = getActivity().getResources().getBoolean(R.bool.isTablet);
            if (!isTablet) {

                mActionBarBackgroundDrawable = getResources().getDrawable(R.drawable.actionbar_background);
                mActionBarBackgroundDrawable.setAlpha(0);

                getActivity().getActionBar().setBackgroundDrawable(mActionBarBackgroundDrawable);
                getActivity().getActionBar().setDisplayShowTitleEnabled(false);
                getActivity().getActionBar().setDisplayShowHomeEnabled(false);

                ((ObservableScrollView) rootView.findViewById(R.id.office_detail_container_inner))
                        .setOnScrollChangedListener(mOnScrollChangedListener);

                //Fix for pre-JELLY_BEAN_MR1 devices
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    mActionBarBackgroundDrawable.setCallback(mDrawableCallback);
                }
            }
            //Buscar precio en esta oficina, lanza la HomeActivity y realiza la transición al
            // SearchFragment con la oficina precargada
            LinearLayout btnBookThisOffice = (LinearLayout) rootView.findViewById(R.id.btnBookThisOffice);
            btnBookThisOffice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    intent.putExtra(SearchFragment.TAG_PICKUP_ZONE, mItem.getCode());
                    intent.putExtra(SearchFragment.TAG_DROPOFF_ZONE, mItem.getCode());
                    intent.putExtra(HomeActivity.DEFAULT_ACTION,
                            HomeActivity.DRAWER_POSITION_NEW_BOOKING);
                    intent.putExtra(HomeActivity.DONT_OPEN_DRAWER, true);
                    startActivity(intent);
                }
            });

        }

        return rootView;
    }

    /**
     * Añade un esqueleto HTML a un texto
     * @param html el texto o html
     * @return el html completo
     */
    private String wrapHtml(String html){
        return"<html><head><title></title></head><body>"+html+"</body></html>";
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
