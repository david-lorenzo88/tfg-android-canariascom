package com.canarias.rentacar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Fragmento que hereda de MapFragment
 * y muestra un mapa en el que se pueden añadir elementos
 */
public class FragmentMap extends MapFragment {

    //Keys para los argumentos
    private static final String ARG_MAP_POINTS = "map_points";
    private static final String ARG_FRAGMENT_CONTAINER_ID = "fragment_container_id";
    public static final String ARG_ALL_OFFICES = "all_offices";
    //Oficinas a mostrar en el mapa
    private HashMap<String, Office> offices;
    private int fragmentContainerId = -1;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MapFragment.
     */
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

    /**
     * Crea el fragmento
     * @param savedInstanceState estado para restaurar los valores previos
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mMapPoints = getArguments().getString(ARG_MAP_POINTS);
            fragmentContainerId = getArguments().getInt(ARG_FRAGMENT_CONTAINER_ID);

            //Obtenemos las oficinas desde la base de datos
            String[] offices = mMapPoints.split(";");
            OfficeDataSource ds = new OfficeDataSource(getActivity());
            this.offices = new HashMap<String, Office>();
            try{
                ds.open();

                if(mMapPoints.equals(ARG_ALL_OFFICES)){
                    //Mostramos todas las oficinas
                    List<Office> tempOffices = ds.getOffices(null);
                    for(Office o : tempOffices){
                        this.offices.put(o.getCode(), o);
                    }
                }else {
                    //Mostramos solo algunas oficinas
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
    }

    /**
     * Ejecutado para crear la vista del mapa
     * @param inflater objeto para inflar las vistas
     * @param container la vista padre a la que el fragmento será asociado
     * @param savedInstanceState estado previo del fragmento cuando se está reconstruyendo
     * @return la vista generada para el fragmento
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        setUpMap();
        return v;
    }



    /**
     * Configura el mapa. Aqui se pueden añadir marcadores, lineas, listeners o mover
     * la cámara, entre otras funcionalidades.
     */
    private void setUpMap() {
        GoogleMap map = getMap();
        map.setMyLocationEnabled(true);

        //Establecemos el adaptador para mostrar los popups
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                //Inflamos la interfaz y configuramos las vistas
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

        // Icono para los marcadores
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.marker);


        //Mostramos un marcador para cada oficina
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
        //Establecemos el evento click del popup, que abrirá el detalle de la oficina
        //seleccionada
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.v("TEST", "fragmentContainerId: "+fragmentContainerId);
                if(fragmentContainerId == -1){
                    Intent intent = new Intent(getActivity(), OfficeDetailActivity.class);
                    intent.putExtra(OfficeDetailFragment.ARG_ITEM_ID, marker.getSnippet());
                    startActivity(intent);
                } else {
                    Bundle arguments = new Bundle();
                    arguments.putString(OfficeDetailFragment.ARG_ITEM_ID,
                            marker.getSnippet());
                    OfficeDetailFragment fragment = new OfficeDetailFragment();
                    fragment.setArguments(arguments);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.office_detail_container, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        // Zoom automático a la Latitud y Longitud por defecto, con un zoom de 9
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Config.DEFAULT_LAT,
                Config.DEFAULT_LNG), 9.0f));
    }










}
