package com.canarias.rentacar;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.canarias.rentacar.async.DownloadCarsAsyncTask;
import com.canarias.rentacar.async.SyncDataAsyncTask;
import com.canarias.rentacar.config.Config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Activity principal, que contiene el grid con los accesos a las partes de la aplicación.
 * Además implementa el patrón NavigationDrawer para mostrar el menú lateral colapsable
 */
public class HomeActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    //Posiciones de los elementos del Menu
    private final static int DRAWER_POSITION_HOME = 0;
    public final static int DRAWER_POSITION_NEW_BOOKING = 1;
    private final static int DRAWER_POSITION_MY_BOOKINGS = 2;
    private final static int DRAWER_POSITION_CARS = 3;
    private final static int DRAWER_POSITION_OFFICES = 4;
    private final static int DRAWER_POSITION_HELP = 5;
    private final static int DRAWER_POSITION_SETTINGS = 6;
    public static final String DEFAULT_ACTION = "default_action";
    public static final String DONT_OPEN_DRAWER = "dont_open_drawer";


    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    //Preferencias del usuario
    private SharedPreferences prefs = null;

    /**
     * Crea la activity
     * @param savedInstanceState estado previo
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Obtiene el NavigationDrawer, el menu
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        //Flag para indicar si queremos que se abra el drawer o no, si el usuario no ha aprendido
        //aún el funcionamiento
        if(getIntent().getExtras() != null && getIntent().getExtras().containsKey(DONT_OPEN_DRAWER)) {
            mNavigationDrawerFragment.setmDontOpenDrawer(
                    getIntent().getExtras().getBoolean(DONT_OPEN_DRAWER, false));
        }

        // Configura el NavigationDrawer
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        //Obtiene las preferencias del usuario
        prefs = getSharedPreferences("com.canarias.rentacar", MODE_PRIVATE);


        if(savedInstanceState == null){
            //Iniciamos por primera vez, mostramos el HomeFragment
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new HomeFragment())
                    .commit();
        }
        //Si hemos pasado una acción por defecto a seleccionar, realizamos la transición
        //para mostrar el fragmento en cuestión
        if(getIntent().getExtras() != null && getIntent().getExtras().containsKey(DEFAULT_ACTION)){
            int action = getIntent().getExtras().getInt(DEFAULT_ACTION);

            onNavigationDrawerItemSelected(action);

            getIntent().removeExtra(DEFAULT_ACTION);

        }

    }

    /**
     * Ejecutado cuando se pulsa sobre un item en el Menu
     * @param position
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {

        FragmentManager fragmentManager = getFragmentManager();

        switch (position) {
            case DRAWER_POSITION_MY_BOOKINGS:
                //Mis Reservas
                Intent reservationListIntent = new Intent(this, ReservationListActivity.class);
                startActivity(reservationListIntent);
                break;

            case DRAWER_POSITION_NEW_BOOKING:
                //Reservar un coche
                if(getIntent().getExtras() != null &&
                        getIntent().getExtras().containsKey(SearchFragment.TAG_PICKUP_ZONE) &&
                        getIntent().getExtras().containsKey(SearchFragment.TAG_DROPOFF_ZONE)){
                    fragmentManager.beginTransaction()

                            .replace(R.id.container,
                                    SearchFragment.newInstance(DRAWER_POSITION_NEW_BOOKING,
                                            getIntent().getExtras().getString(SearchFragment.TAG_PICKUP_ZONE),
                                    getIntent().getExtras().getString(SearchFragment.TAG_DROPOFF_ZONE)))
                            .addToBackStack("New_Booking")
                            .commit();
                    getIntent().removeExtra(SearchFragment.TAG_PICKUP_ZONE);
                    getIntent().removeExtra(SearchFragment.TAG_DROPOFF_ZONE);
                } else if(getIntent().getExtras() != null &&
                        getIntent().getExtras().containsKey(SearchFragment.TAG_SELECTED_MODEL)){
                    fragmentManager.beginTransaction()

                            .replace(R.id.container,
                                    SearchFragment.newInstance(DRAWER_POSITION_NEW_BOOKING,
                                            getIntent().getExtras()
                                                    .getString(SearchFragment.TAG_SELECTED_MODEL)))
                            .addToBackStack("New_Booking")
                            .commit();
                    getIntent().removeExtra(SearchFragment.TAG_SELECTED_MODEL);
                } else {
                    fragmentManager.beginTransaction()

                            .replace(R.id.container,
                                    SearchFragment.newInstance(DRAWER_POSITION_NEW_BOOKING))
                            .addToBackStack("New_Booking")
                            .commit();
                }
                mTitle = getString(R.string.title_fragment_new_booking);
                getActionBar().setTitle(mTitle);

                break;
            case DRAWER_POSITION_SETTINGS:
                //Ajustes
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case DRAWER_POSITION_CARS:
                //Buscar un coche
                Intent carsIntent = new Intent(this, CarListActivity.class);
                startActivity(carsIntent);
                break;
            case DRAWER_POSITION_OFFICES:
                //Buscar Oficinas
                Intent officesIntent = new Intent(this, OfficeListActivity.class);
                startActivity(officesIntent);
                break;
            case DRAWER_POSITION_HELP:
                //Ayuda
                Intent helpIntent = new Intent(this, HelpActivity.class);
                startActivity(helpIntent);
                break;
            case DRAWER_POSITION_HOME:
                // Home

                fragmentManager.beginTransaction()
                        .replace(R.id.container, new HomeFragment())
                        .commit();
                mTitle = getString(R.string.app_name);
                getActionBar().setTitle(mTitle);
                break;
        }

    }

    /**
     * Ejecutado cuando se cambia el fragmento
     * @param number numero de seccion
     */
    public void onSectionAttached(int number) {

        switch (number) {
            case DRAWER_POSITION_NEW_BOOKING:
                mTitle = getString(R.string.title_fragment_new_booking);
                break;
            case DRAWER_POSITION_HOME:
                mTitle = getString(R.string.app_name);
                break;


        }
    }

    /**
     * Restaura la barra de acciones superior
     */
    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    /**
     * Ejecutado al crear el menú de opciones, es el sitio para manejar y decidir que elementos
     * se muestran en el menu así como cargar el archivo de configuración del menu
     * @param menu el menu
     * @return true para que el menú sea mostrado o false en caso contrario
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.home, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        /*if (id == R.id.action_settings) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    /**
     * Ejecutado al activar la activity.
     * Usado para comprobar la ultima sincronización de vehículos y oficinas
     * y ejecutarla en caso necesario.
     */
    @Override
    protected void onResume() {
        super.onResume();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);

        Date daysAgo = new Date();
        Calendar calDaysAgo = Calendar.getInstance();
        calDaysAgo.setTime(daysAgo);
        calDaysAgo.add(Calendar.DATE, (Config.DAYS_TO_SYNC_CONTENT + 1) * -1);

        try {
            //Cargamos la ultima fecha de sincronización
            Date lastSync = sdf.parse(prefs.getString("last_sync",
                    sdf.format(calDaysAgo.getTime())));
            Calendar lastSyncCal = Calendar.getInstance();
            lastSyncCal.setTime(lastSync);
            lastSyncCal.add(Calendar.DATE, Config.DAYS_TO_SYNC_CONTENT);

            if (cal.compareTo(lastSyncCal) >= 0) {

                //Sincronizamos los datos
                Toast.makeText(this, getString(R.string.last_sync)+": "+sdf.format(lastSync),
                        Toast.LENGTH_LONG).show();

                SyncDataAsyncTask syncTask = new SyncDataAsyncTask(this);
                syncTask.execute();
                //Establecemos la nueva fecha de última sincronización
                prefs.edit().putString("last_sync", sdf.format(today)).commit();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }



}
