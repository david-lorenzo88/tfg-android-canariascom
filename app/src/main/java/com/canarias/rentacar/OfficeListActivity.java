package com.canarias.rentacar;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.MapFragment;


/**
 * An activity representing a list of Offices. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link OfficeDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link OfficeListFragment} and the item details
 * (if present) is a {@link OfficeDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link OfficeListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class OfficeListActivity extends Activity
        implements OfficeListFragment.Callbacks {

    private static final String TAG_MAP = "MAP";
    private static final String TAG_LIST = "LIST";
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private boolean mShowMenuMapAction = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office_list);
        // Show the Up button in the action bar.
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Log.v("MENU", "onBackStackChanged");
                Fragment mapFragment = getFragmentManager().findFragmentByTag(TAG_MAP);
                if (mapFragment != null && mapFragment.isVisible()) {
                    // add your code here
                    Log.v("MENU", "Map Fragment is visible");
                    mShowMenuMapAction = false;
                } else {
                    Log.v("MENU", "Map Fragment is NOT visible");
                    mShowMenuMapAction = true;
                }


                invalidateOptionsMenu();
            }
        });

        if(savedInstanceState != null)
            mShowMenuMapAction = savedInstanceState.getBoolean("mShowMenuMapAction");

        if (findViewById(R.id.office_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((OfficeListFragment) getFragmentManager()
                    .findFragmentById(R.id.office_list))
                    .setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.v("MENU", "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putBoolean("mShowMenuMapAction", mShowMenuMapAction);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        if(id == R.id.action_show_offices_in_map){
            mShowMenuMapAction = false;
            invalidateOptionsMenu();

            FragmentMap fragment = FragmentMap.newInstance(FragmentMap.ARG_ALL_OFFICES);
            getFragmentManager().beginTransaction()
                    .replace(R.id.office_list, fragment, TAG_MAP)
                    .addToBackStack(null)
                    .commit();

        }
        if(id == R.id.action_show_offices_in_list){
            mShowMenuMapAction = true;
            invalidateOptionsMenu();

            OfficeListFragment fragment = new OfficeListFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.office_list, fragment, TAG_LIST)
                    .addToBackStack(null)
                    .commit();

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback method from {@link OfficeListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(OfficeDetailFragment.ARG_ITEM_ID, id);
            OfficeDetailFragment fragment = new OfficeDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.office_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, OfficeDetailActivity.class);
            detailIntent.putExtra(OfficeDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v("MENU", "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.office_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.v("MENU", "onPrepareOptionsMenu");
        MenuInflater inflater = getMenuInflater();
        menu.clear();
        inflater.inflate(R.menu.office_list, menu);

        if(mShowMenuMapAction) {
            menu.removeItem(R.id.action_show_offices_in_list);
        } else {
            menu.removeItem(R.id.action_show_offices_in_map);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    public void setmShowMenuMapAction(boolean showMenuMapAction){
        mShowMenuMapAction = showMenuMapAction;
    }


}
