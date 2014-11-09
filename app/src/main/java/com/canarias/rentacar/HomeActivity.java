package com.canarias.rentacar;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
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


public class HomeActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private final static int DRAWER_POSITION_HOME = 0;
    private final static int DRAWER_POSITION_NEW_BOOKING = 1;
    private final static int DRAWER_POSITION_MY_BOOKINGS = 2;
    private final static int DRAWER_POSITION_CARS = 3;
    private final static int DRAWER_POSITION_OFFICES = 4;
    private final static int DRAWER_POSITION_HELP = 5;
    private final static int DRAWER_POSITION_SETTINGS = 6;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        prefs = getSharedPreferences("com.canarias.rentacar", MODE_PRIVATE);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        FragmentManager fragmentManager = getFragmentManager();

        switch (position) {
            case DRAWER_POSITION_MY_BOOKINGS:
                Intent reservationListIntent = new Intent(this, ReservationListActivity.class);
                startActivity(reservationListIntent);
                break;

            case DRAWER_POSITION_NEW_BOOKING:


                fragmentManager.beginTransaction()

                        .replace(R.id.container,
                                SearchFragment.newInstance(DRAWER_POSITION_NEW_BOOKING))
                        .commit();

                break;
            case DRAWER_POSITION_SETTINGS:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case DRAWER_POSITION_CARS:
                Intent carsIntent = new Intent(this, CarListActivity.class);
                startActivity(carsIntent);
                break;
            case DRAWER_POSITION_OFFICES:
                Intent officesIntent = new Intent(this, OfficeListActivity.class);
                startActivity(officesIntent);
                break;
            default:
                // update the main content by replacing fragments

                fragmentManager.beginTransaction()
                        .replace(R.id.container, new HomeFragment())
                        .commit();
                break;
        }

    }

    public void onSectionAttached(int number) {

        switch (number) {
            case DRAWER_POSITION_NEW_BOOKING:
                mTitle = getString(R.string.title_fragment_new_booking);
                break;
            case DRAWER_POSITION_CARS:
                mTitle = getString(R.string.title_fragment_cars);
                break;
            case DRAWER_POSITION_OFFICES:
                mTitle = getString(R.string.title_fragment_offices);
                break;
            default:
                mTitle = getString(R.string.app_name);
                break;

        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


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
            Date lastSync = sdf.parse(prefs.getString("last_sync", sdf.format(calDaysAgo.getTime())));
            Calendar lastSyncCal = Calendar.getInstance();
            lastSyncCal.setTime(lastSync);
            lastSyncCal.add(Calendar.DATE, Config.DAYS_TO_SYNC_CONTENT);

            if (cal.compareTo(lastSyncCal) >= 0) {

                //Sync Data Here
                Toast.makeText(this, "Last Sync: "+sdf.format(lastSync), Toast.LENGTH_LONG).show();

                SyncDataAsyncTask syncTask = new SyncDataAsyncTask(this);
                syncTask.execute();

                prefs.edit().putString("last_sync", sdf.format(today)).commit();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((HomeActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
