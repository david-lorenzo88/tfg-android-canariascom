package com.canarias.rentacar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.canarias.rentacar.async.CancelReservationAsyncTask;


/**
 * An activity representing a single Reservation detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ReservationListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link ReservationDetailFragment}.
 */
public class ReservationDetailActivity extends Activity {

    private final static String FRAGMENT_TAG = "reservation_detail_fragment";
    private final static String UPDATE_FRAGMENT_TAG = "reservation_update_fragment";

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_detail);

        mContext = this;

        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ReservationDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(ReservationDetailFragment.ARG_ITEM_ID));
            ReservationDetailFragment fragment = new ReservationDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .add(R.id.reservation_detail_container, fragment, FRAGMENT_TAG)

                    .commit();
        }
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
            NavUtils.navigateUpTo(this, new Intent(this, ReservationListActivity.class));
            return true;
        } else if (id == R.id.action_cancel_reservation) {
            //Cancel reservation, show confirmation dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.cancel_reservation));
            builder.setMessage(getString(R.string.cancel_reservation_message));

            builder.setPositiveButton(getString(R.string.cancel_reservation_action_positive),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Cancel reservation
                            String reservationId = getIntent()
                                    .getStringExtra(ReservationDetailFragment.ARG_ITEM_ID);
                            Log.v("TEST", "Cancelling: " + reservationId);

                            Fragment fragment = getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
                            View rootView = fragment.getView();


                            CancelReservationAsyncTask task =
                                    new CancelReservationAsyncTask(mContext,
                                            reservationId,
                                            (TextView) rootView.findViewById(R.id.reservation_status),
                                            (ImageView) rootView.findViewById(R.id.status_icon));
                            task.execute();
                        }
                    });
            builder.setNegativeButton(getString(R.string.cancel_reservation_action_negative),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            AlertDialog dialog = builder.create();

            dialog.show();

            dialog.getButton(Dialog.BUTTON_POSITIVE).setBackgroundColor(
                    getResources().getColor(R.color.red_canarias));
            dialog.getButton(Dialog.BUTTON_POSITIVE).setTextColor(getResources()
                    .getColor(R.color.white));

        } else if (id == R.id.action_update_reservation) {

            UpdateReservationFragment fragment = UpdateReservationFragment
                    .newInstance(getIntent().getStringExtra(ReservationDetailFragment.ARG_ITEM_ID));


            getFragmentManager().beginTransaction()
                    .replace(R.id.reservation_detail_container, fragment, UPDATE_FRAGMENT_TAG)
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.reservation_detail, menu);
        return true;
    }
}
