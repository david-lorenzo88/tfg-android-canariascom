package com.canarias.rentacar;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.canarias.rentacar.db.dao.OfficeDataSource;
import com.canarias.rentacar.model.Office;

import java.sql.SQLException;


/**
 * A fragment representing a single Office detail screen.
 * This fragment is either contained in a {@link OfficeListActivity}
 * in two-pane mode (on tablets) or a {@link OfficeDetailActivity}
 * on handsets.
 */
public class OfficeDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Office mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OfficeDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            OfficeDataSource ds = new OfficeDataSource(getActivity());
            try{
                ds.open();
                mItem = ds.getOffice(getArguments().getString(ARG_ITEM_ID));
                ds.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_office_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.office_detail)).setText(mItem.getName());
        }

        return rootView;
    }
}
