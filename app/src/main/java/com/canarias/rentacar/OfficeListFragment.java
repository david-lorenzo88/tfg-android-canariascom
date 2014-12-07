package com.canarias.rentacar;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.canarias.rentacar.adapters.OfficeListAdapter;
import com.canarias.rentacar.db.dao.OfficeDataSource;

import com.canarias.rentacar.db.dao.ZoneDataSource;
import com.canarias.rentacar.model.Office;
import com.canarias.rentacar.model.Zone;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A list fragment representing a list of Offices. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link OfficeDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class OfficeListFragment extends Fragment {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    public static final int ZONE_CODE_ALL = -1;
    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
        }
    };
    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;
    //Lista de oficinas
    private List<Office> offices;
    //Lista de oficinas filtrada
    private List<Office> filteredOffices;
    private ListView listView;
    //Lista de zonas para el filtro
    private List<Zone> zones;
    private Spinner filterSpinner;
    private OfficeListAdapter adapter;
    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OfficeListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public final OfficeListAdapter getAdapter(){
        return adapter;
    }

    /**
     * Llamado cuando el sistema operativo crea la vista del fragmento
     * @param inflater objeto para inflar las vistas
     * @param container la vista padre a la que el fragmento será asociado
     * @param savedInstanceState estado previo del fragmento cuando se está reconstruyendo
     * @return la vista generada para el fragmento
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container,
                savedInstanceState);
        View view = inflater.inflate(R.layout.office_list, container, false);


        filterSpinner = (Spinner) getActivity().findViewById(R.id.filterSpinner);

        return view;
    }

    /**
     * llamado cuando la vista ha sido creada
     * @param view la vista
     * @param savedInstanceState estado previo del fragmento cuando se está reconstruyendo
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
        //Inicializamos la ListView
        listView = (ListView) view.findViewById(android.R.id.list);
        listView.setDivider(null);
        listView.setDividerHeight(10);
        listView.setSelector(R.color.transparent);
        listView.setCacheColorHint(R.color.transparent);
        listView.setHeaderDividersEnabled(true);
        listView.setFooterDividersEnabled(true);
        listView.addHeaderView(new View(getActivity()));
        listView.addFooterView(new View(getActivity()));
        setActivateOnItemClick(true);

        //Obtenemos la lista de oficinas y zonas
        OfficeDataSource ds = new OfficeDataSource(getActivity());
        ZoneDataSource dsZones = new ZoneDataSource(getActivity());

        try{
            ds.open();
            dsZones.open();

            offices = ds.getOffices(null);
            zones = dsZones.getZones();

            Zone zoneAll = new Zone();
            zoneAll.setCode(ZONE_CODE_ALL);
            zoneAll.setName(getString(R.string.spinnerAllText));
            zones.add(0, zoneAll);

            ds.close();
            dsZones.close();


            adapter = new OfficeListAdapter(getActivity(),
                    R.layout.office_list_item, offices);

            listView.setAdapter(adapter);
            //Manejamos el evento click en los items de la lista
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mCallbacks.onItemSelected(filteredOffices.get(position - 1).getCode());
                    adapter.setSelectedIndex(position - 1);
                }
            });

            //Adapter para el filtro de zonas
            ArrayAdapter<Zone> adapter = new ArrayAdapter<Zone>(getActivity(),
                    R.layout.zone_spinner, R.id.zone_item_name, zones);

            filterSpinner = (Spinner) view.findViewById(R.id.filterSpinner);

            filterSpinner.setAdapter(adapter); // this will set list of values to spinner

            //Manejamos el evento para filtrar la lista de oficinas por zona
            filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    filterOfficeList(((Zone) parent.getItemAtPosition(position)).getCode());
                    getAdapter().getFilter()
                            .filter(String.valueOf(
                                    ((Zone)parent.getItemAtPosition(position)).getCode()));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Filtra la lista de oficinas en base al codigo de zona
     * @param zoneCode el codigo de zona
     */
    private void filterOfficeList(int zoneCode){
        filteredOffices = new ArrayList<Office>(offices);

        if(!(zoneCode == ZONE_CODE_ALL)){
            filteredOffices = new ArrayList<Office>();

            for(Office o : offices){
                if(o.getZone().getCode() == zoneCode){
                    filteredOffices.add(o);
                }
            }

        }

    }

    /**
     * Ejecutado cuando el fragmento se asocia a la activity
     * @param activity la activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }
        if(filterSpinner != null)
            filterSpinner.setVisibility(View.VISIBLE);

        mCallbacks = (Callbacks) activity;
    }


    /**
     * Ejecutado cuando el fragmento se desasocia de la activity
     */
    @Override
    public void onDetach() {
        super.onDetach();
        if(filterSpinner != null)
            filterSpinner.setVisibility(View.GONE);
        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    /**
     * Almacenamos los datos para una posterior restauración
     * @param outState parametros para almacenar los datos
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        listView.setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    /**
     * Establece la posicion activa en la ListView
     * @param position nueva posición
     */
    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            listView.setItemChecked(mActivatedPosition, false);
        } else {
            listView.setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String id);
    }
}
