package com.canarias.rentacar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;


/**
 * An activity representing a list of Reservations. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ReservationDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ReservationListFragment} and the item details
 * (if present) is a {@link ReservationDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link ReservationListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class ReservationListActivity extends FragmentActivity
        implements ReservationListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    /**
     * Crea la activity
     * @param savedInstanceState estado previo
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_list);
        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (findViewById(R.id.reservation_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((ReservationListFragment) getFragmentManager()
                    .findFragmentById(R.id.reservation_list))
                    .setActivateOnItemClick(true);
        }


    }

    /**
     * Maneja el evento click de los items del menu
     * @param item el item presionado
     * @return true para para el evento o false para dejar su curso
     */
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
        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback method from {@link ReservationListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ReservationDetailFragment.ARG_ITEM_ID, id);
            //Flag para lanzar la actualización de la reserva la web service.
            arguments.putBoolean(ReservationDetailFragment.ARG_LAUNCH_UPDATE, true);
            ReservationDetailFragment fragment = new ReservationDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .replace(R.id.reservation_detail_container, fragment)

                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, ReservationDetailActivity.class);
            detailIntent.putExtra(ReservationDetailFragment.ARG_ITEM_ID, id);
            detailIntent.putExtra(ReservationDetailFragment.ARG_LAUNCH_UPDATE, true);
            startActivity(detailIntent);
        }
    }
}
