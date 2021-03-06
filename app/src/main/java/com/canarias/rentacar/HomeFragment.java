package com.canarias.rentacar;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


/**
 * Fragmento que muestra el grid de la pantalla principal
 */
public class HomeFragment extends Fragment {

    //Posiciones del menu
    private final static int DRAWER_POSITION_HOME = 0;
    private final static int DRAWER_POSITION_NEW_BOOKING = 1;
    private final static int DRAWER_POSITION_MY_BOOKINGS = 2;
    private final static int DRAWER_POSITION_CARS = 3;
    private final static int DRAWER_POSITION_OFFICES = 4;
    private final static int DRAWER_POSITION_HELP = 5;
    private final static int DRAWER_POSITION_SETTINGS = 6;

    public HomeFragment() {
        // Required empty public constructor
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
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        LinearLayout newBooking = (LinearLayout) rootView.findViewById(R.id.action_home_new_booking);
        newBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).onNavigationDrawerItemSelected(DRAWER_POSITION_NEW_BOOKING);
            }
        });

        LinearLayout bookingList = (LinearLayout) rootView.findViewById(R.id.action_home_my_bookings);
        bookingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).onNavigationDrawerItemSelected(DRAWER_POSITION_MY_BOOKINGS);
            }
        });
        LinearLayout cars = (LinearLayout) rootView.findViewById(R.id.action_home_cars);
        cars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).onNavigationDrawerItemSelected(DRAWER_POSITION_CARS);
            }
        });

        LinearLayout offices = (LinearLayout) rootView.findViewById(R.id.action_home_offices);
        offices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).onNavigationDrawerItemSelected(DRAWER_POSITION_OFFICES);
            }
        });

        LinearLayout help = (LinearLayout) rootView.findViewById(R.id.action_home_help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).onNavigationDrawerItemSelected(DRAWER_POSITION_HELP);
            }
        });
        LinearLayout settings = (LinearLayout) rootView.findViewById(R.id.action_home_settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).onNavigationDrawerItemSelected(DRAWER_POSITION_SETTINGS);
            }
        });


        return rootView;
    }

    /**
     * Ejecutado al activar el fragment.
     * Establecemos el titulo de la activity
     */
    @Override
    public void onResume() {
        super.onResume();
        getActivity().getActionBar().setTitle(getString(R.string.app_name));
    }


}
