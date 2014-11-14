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
            arguments.putBoolean(ReservationDetailFragment.ARG_LAUNCH_UPDATE,
                    getIntent().getBooleanExtra(ReservationDetailFragment.ARG_LAUNCH_UPDATE, false));
            ReservationDetailFragment fragment = new ReservationDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .add(R.id.reservation_detail_container, fragment, FRAGMENT_TAG)

                    .commit();
        }
    }




}
