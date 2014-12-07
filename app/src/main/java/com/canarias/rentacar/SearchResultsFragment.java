package com.canarias.rentacar;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.canarias.rentacar.async.AvailabilityAsyncTask;
import com.canarias.rentacar.config.Config;
import com.canarias.rentacar.db.dao.OfficeDataSource;
import com.canarias.rentacar.db.dao.ZoneDataSource;
import com.canarias.rentacar.model.Office;
import com.canarias.rentacar.model.Zone;

import java.util.HashMap;

/**
 * Fragmento que muestra los resultados de busqueda de disponibilidad.
 */
public class SearchResultsFragment extends Fragment {

    public static final String ARG_EXTRAS_STRING = "extras_string";
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    AvailabilityAsyncTask availAsync;


    public SearchResultsFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SearchResultsFragment newInstance(int sectionNumber) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Crea el fragmento
     * @param savedInstanceState estado previo
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        View rootView = inflater.inflate(R.layout.fragment_search_results, container, false);

        initActivity(rootView);

        getActivity().getActionBar().setTitle(getString(R.string.title_fragment_new_booking) + " - " + getString(R.string.title_activity_search_results));

        return rootView;
    }

    /**
     * Ejecutado cuando el fragmento se asocia a la activity
     * @param activity la activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((HomeActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    /**
     * Inicializa la interfaz
     * @param rootView vista raiz
     */
    private void initActivity(final View rootView) {

        Bundle bundle = getArguments();
        TextView lblPickupPoint = (TextView) rootView.findViewById(R.id.searchResultPickupPointValue);
        TextView lblDropoffPoint = (TextView) rootView.findViewById(R.id.searchResultDropoffPointValue);
        TextView lblPickupDateTime = (TextView) rootView.findViewById(R.id.searchResultpickupDateValue);
        TextView lblDropoffDateTime = (TextView) rootView.findViewById(R.id.searchResultDropoffDateValue);

        LinearLayout changeBtn = (LinearLayout) rootView.findViewById(R.id.changeBtn);
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        ZoneDataSource zoneDS = new ZoneDataSource(getActivity());
        OfficeDataSource officeDS = new OfficeDataSource(getActivity());

        try {
            zoneDS.open();
            officeDS.open();

            Office pickupPoint = officeDS.getOffice(bundle
                    .getString(Config.ARG_PICKUP_POINT));
            Office dropoffPoint = officeDS.getOffice(bundle
                    .getString(Config.ARG_DROPOFF_POINT));

            Zone pickupZone = zoneDS.getZone(bundle.getInt(Config.ARG_PICKUP_ZONE));
            Zone dropoffZone = zoneDS.getZone(bundle.getInt(Config.ARG_DROPOFF_ZONE));

            lblPickupPoint.setText(pickupPoint.getName() + " (" + pickupZone.getName() + ")");
            lblDropoffPoint.setText(dropoffPoint.getName() + " (" + dropoffZone.getName() + ")");
            lblPickupDateTime.setText(
                    bundle.getString(Config.ARG_PICKUP_DATE) + " - "
                            + bundle.getString(Config.ARG_PICKUP_TIME));
            lblDropoffDateTime.setText(
                    bundle.getString(Config.ARG_DROPOFF_DATE) + " - "
                            + bundle.getString(Config.ARG_DROPOFF_TIME));

            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Config.ARG_PICKUP_POINT,
                    bundle.getString(Config.ARG_PICKUP_POINT));
            params.put(Config.ARG_DROPOFF_POINT,
                    bundle.getString(Config.ARG_DROPOFF_POINT));
            params.put(Config.ARG_PICKUP_DATE,
                    bundle.getString(Config.ARG_PICKUP_DATE));
            params.put(Config.ARG_PICKUP_TIME,
                    bundle.getString(Config.ARG_PICKUP_TIME));
            params.put(Config.ARG_DROPOFF_DATE,
                    bundle.getString(Config.ARG_DROPOFF_DATE));
            params.put(Config.ARG_DROPOFF_TIME,
                    bundle.getString(Config.ARG_DROPOFF_TIME));

            //Lanzamos la busqueda de disponibilidad
            if(bundle != null && bundle.containsKey(Config.ARG_SELECTED_CAR)) {
                availAsync = new AvailabilityAsyncTask(rootView, params, getActivity(),
                        getArguments(), bundle.getString(Config.ARG_SELECTED_CAR));
            } else {
                availAsync = new AvailabilityAsyncTask(rootView, params, getActivity(), getArguments());
            }
            availAsync.execute();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally{
            zoneDS.close();
            officeDS.close();
        }
    }


}
