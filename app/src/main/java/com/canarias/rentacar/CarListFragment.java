package com.canarias.rentacar;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.canarias.rentacar.adapters.CarListAdapter;
import com.canarias.rentacar.db.dao.CarDataSource;
import com.canarias.rentacar.model.Car;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * A list fragment representing a list of Cars. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link CarDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class CarListFragment extends ListFragment {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
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
    //Lista de coches
    List<Car> cars;
    //lista de coches filtrada
    List<Car> filteredCars;
    //lista de categorias para el filtro
    List<String> categories;

    CarListAdapter adapter;

    Spinner filterSpinner;

    ListView listView;
    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CarListFragment() {
    }

    /**
     * Crea el Fragment
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    /**
     * Crea la vista para el fragment
     * @param inflater objeto para inflar las vistas
     * @param container la vista padre a la que el fragmento será asociado
     * @param savedInstanceState estado previo del fragmento cuando se está reconstruyendo
     * @return la vista generada para el fragmento
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container,
                savedInstanceState);
        View view = inflater.inflate(R.layout.car_list, container, false);
        filterSpinner = (Spinner) view.findViewById(R.id.filterSpinner);

        return view;
    }

    /**
     * Getter. Devuelve el adapter
     * @return
     */
    public final CarListAdapter getAdapter(){
        return adapter;
    }

    /**
     * Ejecutado cuando la vista ya ha sido creada
     * @param view la vista del fragment
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
        listView = getListView();
        listView.setDivider(null);
        listView.setDividerHeight(10);
        listView.setSelector(R.color.transparent);
        listView.setCacheColorHint(R.color.transparent);
        listView.setHeaderDividersEnabled(true);
        listView.setFooterDividersEnabled(true);
        listView.addHeaderView(new View(getActivity()));
        listView.addFooterView(new View(getActivity()));
        setActivateOnItemClick(true);


        CarDataSource ds = new CarDataSource(getActivity());

        //Obtenemos la lista de vehículos
        try {
            ds.open();

            cars = ds.getAllCars();

            filteredCars = new ArrayList<Car>(cars);

            categories = ds.getCarCategories();
            categories.add(0, getActivity().getString(R.string.spinnerAllText));

            ds.close();

            //Creamos el adapter para los vehículos
            adapter = new CarListAdapter(
                    getActivity(),
                    R.layout.car_list_item,
                    cars);
            setListAdapter(adapter);

            //Creamos el adapter para el filtro
            final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(),
                    R.layout.category_spinner, R.id.category_item_name, categories);
            filterSpinner.setAdapter(spinnerAdapter);

            //Evento para filtrar la lista al seleccionar una categoría en el filtro
            filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    filterCarList((String)parent.getItemAtPosition(position));

                    getAdapter().getFilter()
                            .filter((String)parent.getItemAtPosition(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


        } catch (SQLException ex) {
            Log.e("TEST", ex.getMessage());
        }

    }

    /**
     * filtra la lista de vehículos
     * @param cat categoría a filtrar
     */
    private void filterCarList(String cat){
        filteredCars = new ArrayList<Car>(cars);

        if(!cat.equals(getString(R.string.spinnerAllText))){
            filteredCars = new ArrayList<Car>();

            for(Car c : cars){
                if(c.getCategory().equals(cat)){
                    filteredCars.add(c);
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

        mCallbacks = (Callbacks) activity;
    }

    /**
     * Ejecutado cuando el fragmento se desasocia de la activity
     */
    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    /**
     * Ejecutado cuando se ha pulsado sobre un item de la lista
     * @param listView la lista
     * @param view la vista del item que se ha pulsado
     * @param position la posicion del item
     * @param id el identificador del item
     */
    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.

        mCallbacks.onItemSelected(filteredCars.get(position - 1).getModel());

        adapter.setSelectedIndex(position - 1);
    }

    /**
     * Guarda los valores necesarios para restaurar el fragmento
     * @param outState set de parametros a almacenar
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
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    /**
     * Establece la posicion activa de la lista
     * @param position posicion a activar
     */
    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
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
