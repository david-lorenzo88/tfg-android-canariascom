package com.canarias.rentacar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.canarias.rentacar.config.Config;
import com.canarias.rentacar.db.dao.OfficeDataSource;
import com.canarias.rentacar.model.Office;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;


public class FragmentMap extends MapFragment {





    private Double latitude, longitude;

    private static final String ARG_MAP_POINTS = "map_points";
    private static final String ARG_FRAGMENT_CONTAINER_ID = "fragment_container_id";

    public static final String ARG_ALL_OFFICES = "all_offices";



    private String mMapPoints;
    private HashMap<String, Office> offices;

    private int fragmentContainerId = -1;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentMap newInstance(String mapPoints, int fragmentContainerId) {
        FragmentMap fragment = new FragmentMap();
        Bundle args = new Bundle();
        args.putString(ARG_MAP_POINTS, mapPoints);
        args.putInt(ARG_FRAGMENT_CONTAINER_ID, fragmentContainerId);

        fragment.setArguments(args);
        return fragment;
    }

    public FragmentMap() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMapPoints = getArguments().getString(ARG_MAP_POINTS);
            fragmentContainerId = getArguments().getInt(ARG_FRAGMENT_CONTAINER_ID);

            //Get Offices from DB
            String[] offices = mMapPoints.split(";");
            OfficeDataSource ds = new OfficeDataSource(getActivity());
            this.offices = new HashMap<String, Office>();
            try{
                ds.open();

                if(mMapPoints.equals(ARG_ALL_OFFICES)){
                    //Showing all offices
                    List<Office> tempOffices = ds.getOffices(null);
                    for(Office o : tempOffices){
                        this.offices.put(o.getCode(), o);
                    }
                }else {
                    //Showing certain offices
                    for (String o : offices) {
                        this.offices.put(o, ds.getOffice(o));
                    }
                }

                ds.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }


        }
        setRetainInstance(true);
        Log.v("MAP", "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        setUpMap();
        return v;
    }



    /**
     * This is where we can add markers or lines, add listeners or move the
     * camera.
     */
    private void setUpMap() {
        GoogleMap map = getMap();
        map.setMyLocationEnabled(true);

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = getActivity().getLayoutInflater().inflate(R.layout.info_window_office, null);
                final String officeCode = marker.getSnippet();
                if(officeCode != null) {
                    Office o = offices.get(officeCode);

                    if (o != null) {

                        TextView officeName = (TextView) v.findViewById(R.id.infoWindowOfficeName);
                        officeName.setText(o.getName());


                        TextView officeAddress = (TextView) v.findViewById(R.id.infoWindowOfficeAddress);
                        officeAddress.setText(o.getAddress());

                        TextView officeZone = (TextView) v.findViewById(R.id.infoWindowOfficeZone);
                        officeZone .setText(o.getZone().getName());

                        TextView officePhone = (TextView) v.findViewById(R.id.infoWindowOfficePhone);
                        officePhone.setText(o.getPhone());



                    }
                }
                return v;
            }
        });

        // For dropping a marker at a point on the Map
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.marker);


        //Loop to show markers
        if(this.offices != null) {
            for (Office o : this.offices.values()) {

                if (o.getLatitude() != 0 && o.getLongitude() != 0) {


                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(o.getLatitude(), o.getLongitude()))
                            .title(o.getName())
                            .snippet(o.getCode())
                            .icon(icon));
                } else {
                    Log.v("MAP", "Oficina faltan coordenadas! " + o.getName() + " - "
                            + o.getCode());
                }
            }
        }
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if(fragmentContainerId != -1){

                } else {
                    Intent intent = new Intent(getActivity(), OfficeDetailActivity.class);
                    intent.putExtra(ReservationDetailFragment.ARG_ITEM_ID, marker.getSnippet());
                    startActivity(intent);
                }
            }
        });

        // For zooming automatically to the Dropped PIN Location
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Config.DEFAULT_LAT,
                Config.DEFAULT_LNG), 9.0f));
    }










}
