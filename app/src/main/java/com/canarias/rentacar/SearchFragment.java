package com.canarias.rentacar;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.canarias.rentacar.config.Config;
import com.canarias.rentacar.dialogs.CalendarDatePickerDialog;
import com.canarias.rentacar.dialogs.TimePickerDialog;
import com.canarias.rentacar.dialogs.ZonePickerDialog;
import com.canarias.rentacar.model.Office;
import com.canarias.rentacar.model.Zone;
import com.canarias.rentacar.widgets.StatusRelativeLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SearchFragment extends Fragment implements CalendarDatePickerDialog.OnDateSetListener,
        ZonePickerDialog.OnZoneChangedListener, TimePickerDialog.OnTimeChangedListener {
    /*parameters names*/


    public static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";

    public static final String TAG_PICKUP_DATE = "pickup_date";
    public static final String TAG_PICKUP_ZONE = "pickup_zone";
    public static final String TAG_PICKUP_TIME = "pickup_time";
    public static final String TAG_DROPOFF_DATE = "dropoff_date";
    public static final String TAG_DROPOFF_ZONE = "dropoff_zone";
    public static final String TAG_DROPOFF_TIME = "dropoff_time";
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private TextView pickupZoneLabel;
    private TextView dropoffZoneLabel;
    private TextView pickupDateLabel;
    private TextView dropoffDateLabel;
    private TextView pickupTimeLabel;
    private TextView dropoffTimeLabel;
    private StatusRelativeLayout pickupZoneLayout;
    private StatusRelativeLayout dropoffZoneLayout;
    private StatusRelativeLayout pickupDateLayout;
    private StatusRelativeLayout dropoffDateLayout;
    private StatusRelativeLayout pickupTimeLayout;
    private StatusRelativeLayout dropoffTimeLayout;
    private Button btnSearchCars;
    private Zone pickupZone;
    private Zone dropoffZone;
    private Office pickupOffice;
    private Office dropoffOffice;
    private String pickupDate;
    private String dropoffDate;
    private String pickupTime;
    private String dropoffTime;


    public SearchFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SearchFragment newInstance(int sectionNumber) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        initActivity(rootView);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((HomeActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    private void initActivity(final View rootView) {

        pickupZoneLabel = (TextView) rootView.findViewById(R.id.pickupZoneDefaultLabel);
        dropoffZoneLabel = (TextView) rootView.findViewById(R.id.dropoffZoneDefaultLabel);
        pickupDateLabel = (TextView) rootView.findViewById(R.id.pickupDateDefaultLabel);
        dropoffDateLabel = (TextView) rootView.findViewById(R.id.dropoffDateDefaultLabel);
        pickupTimeLabel = (TextView) rootView.findViewById(R.id.pickupTimeDefaultLabel);
        dropoffTimeLabel = (TextView) rootView.findViewById(R.id.dropoffTimeDefaultLabel);


        //Default values
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 1);

        pickupDate = sdf.format(cal.getTime());
        cal.add(Calendar.DATE, 5);
        dropoffDate = sdf.format(cal.getTime());

        pickupTime = "12:00";
        dropoffTime = "12:00";


        pickupDateLabel.setText(pickupDate);
        dropoffDateLabel.setText(dropoffDate);
        pickupTimeLabel.setText(pickupTime);
        dropoffTimeLabel.setText(dropoffTime);

        pickupZoneLayout = (StatusRelativeLayout) rootView.findViewById(R.id.pickupZoneLayout);

        pickupZoneLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final ZonePickerDialog dialog = new ZonePickerDialog(rootView.getContext(), TAG_PICKUP_ZONE);

                dialog.setTitle(R.string.pickupPointTitle);

                dialog.setCallback(SearchFragment.this);

                dialog.show();
            }
        });
        pickupZoneLayout.setStatus(StatusRelativeLayout.STATUS_PENDING);

        dropoffZoneLayout = (StatusRelativeLayout) rootView.findViewById(R.id.dropoffZoneLayout);

        dropoffZoneLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                final ZonePickerDialog dialog = new ZonePickerDialog(rootView.getContext(), TAG_DROPOFF_ZONE);

                dialog.setTitle(R.string.dropoffPointTitle);

                dialog.setCallback(SearchFragment.this);

                dialog.show();
            }
        });

        dropoffZoneLayout.setStatus(StatusRelativeLayout.STATUS_PENDING);

        pickupDateLayout = (StatusRelativeLayout) rootView.findViewById(R.id.pickupDateLayout);

        pickupDateLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    sdf.parse(pickupDate);
                    cal = sdf.getCalendar();
                } catch (Exception ex) {
                    Log.v("TEST", ex.getMessage());
                }

                final CalendarDatePickerDialog dialog = CalendarDatePickerDialog.
                        newInstance(SearchFragment.this,
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH),
                                TAG_PICKUP_DATE,
                                null
                        );

                dialog.show(getFragmentManager(), FRAG_TAG_DATE_PICKER);
            }
        });
        pickupDateLayout.setStatus(StatusRelativeLayout.STATUS_OK);

        dropoffDateLayout = (StatusRelativeLayout) rootView.findViewById(R.id.dropoffDateLayout);

        dropoffDateLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    sdf.parse(dropoffDate);
                    cal = sdf.getCalendar();
                } catch (Exception ex) {
                    Log.v("TEST", ex.getMessage());
                }
                //cal.setTime(new Date());
                //cal.add(Calendar.DATE, 5);

                Calendar validateCal = Calendar.getInstance();
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    sdf.parse(pickupDate);
                    validateCal = sdf.getCalendar();
                } catch (Exception ex) {
                    Log.v("TEST", ex.getMessage());
                }

                final CalendarDatePickerDialog dialog = CalendarDatePickerDialog.
                        newInstance(SearchFragment.this,
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH),
                                TAG_DROPOFF_DATE,
                                validateCal

                        );

                dialog.show(getFragmentManager(), FRAG_TAG_DATE_PICKER);
            }
        });
        dropoffDateLayout.setStatus(StatusRelativeLayout.STATUS_OK);

        pickupTimeLayout = (StatusRelativeLayout) rootView.findViewById(R.id.pickupTimeLayout);

        pickupTimeLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(rootView.getContext(), TAG_PICKUP_TIME);
                dialog.setCallback(SearchFragment.this);
                dialog.setTitle(R.string.pickupTimeDialogTitle);
                dialog.show();
            }
        });
        pickupTimeLayout.setStatus(StatusRelativeLayout.STATUS_OK);

        dropoffTimeLayout = (StatusRelativeLayout) rootView.findViewById(R.id.dropoffTimeLayout);

        dropoffTimeLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(rootView.getContext(), TAG_DROPOFF_TIME);
                dialog.setCallback(SearchFragment.this);
                dialog.setTitle(R.string.dropoffTimeDialogTitle);
                dialog.show();
            }
        });
        dropoffTimeLayout.setStatus(StatusRelativeLayout.STATUS_OK);

        btnSearchCars = (Button) rootView.findViewById(R.id.btnSearchCars);

        btnSearchCars.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //Validamos
                boolean valid = true;

                if (pickupZoneLayout.getStatus() != StatusRelativeLayout.STATUS_OK) {
                    pickupZoneLayout.setStatus(StatusRelativeLayout.STATUS_ERROR);
                    valid = false;
                }
                if (dropoffZoneLayout.getStatus() != StatusRelativeLayout.STATUS_OK) {
                    dropoffZoneLayout.setStatus(StatusRelativeLayout.STATUS_ERROR);
                    valid = false;
                }
                if (pickupDateLayout.getStatus() != StatusRelativeLayout.STATUS_OK) {
                    pickupDateLayout.setStatus(StatusRelativeLayout.STATUS_ERROR);
                    valid = false;
                }
                if (dropoffDateLayout.getStatus() != StatusRelativeLayout.STATUS_OK) {
                    dropoffDateLayout.setStatus(StatusRelativeLayout.STATUS_ERROR);
                    valid = false;
                }
                if (pickupTimeLayout.getStatus() != StatusRelativeLayout.STATUS_OK) {
                    pickupTimeLayout.setStatus(StatusRelativeLayout.STATUS_ERROR);
                    valid = false;
                }
                if (dropoffTimeLayout.getStatus() != StatusRelativeLayout.STATUS_OK) {
                    dropoffTimeLayout.setStatus(StatusRelativeLayout.STATUS_ERROR);
                    valid = false;
                }

                if (!valid) {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.btnSearchCarsInvalidStatus), Toast.LENGTH_SHORT).show();
                    return;
                }

                SearchResultsFragment fragment = SearchResultsFragment.
                        newInstance(getArguments().getInt(ARG_SECTION_NUMBER));

                Bundle args = new Bundle();

                //All fields are valid, replace fragment

                args.putString(Config.ARG_PICKUP_POINT, pickupOffice.getCode());
                args.putString(Config.ARG_DROPOFF_POINT, dropoffOffice.getCode());
                args.putInt(Config.ARG_PICKUP_ZONE, pickupZone.getCode());
                args.putInt(Config.ARG_DROPOFF_ZONE, dropoffZone.getCode());
                args.putString(Config.ARG_PICKUP_DATE, pickupDate);
                args.putString(Config.ARG_DROPOFF_DATE, dropoffDate);
                args.putString(Config.ARG_PICKUP_TIME, pickupTime);
                args.putString(Config.ARG_DROPOFF_TIME, dropoffTime);

                fragment.setArguments(args);

                getFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment)
                        .addToBackStack(null)
                        .commit();


            }
        });


    }


    @Override
    public void onDateSet(CalendarDatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth, String tag) {
        if (tag.equals(TAG_PICKUP_DATE)) {
            pickupDate = dayOfMonth + "/" + String.format("%02d", (monthOfYear + 1)) + "/" + year;
            pickupDateLabel.setText(pickupDate);

        } else if (tag.equals(TAG_DROPOFF_DATE)) {
            dropoffDate = dayOfMonth + "/" + String.format("%02d", (monthOfYear + 1)) + "/" + year;
            dropoffDateLabel.setText(dropoffDate);
        }

        validateDateLayoutStatus();
    }

    private void validateDateLayoutStatus() {
        Calendar pickupCal = Calendar.getInstance();
        Calendar dropoffCal = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.parse(dropoffDate);
            dropoffCal = sdf.getCalendar();
            sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.parse(pickupDate);
            pickupCal = sdf.getCalendar();

            dropoffCal.set(Calendar.HOUR, 0);
            dropoffCal.set(Calendar.HOUR_OF_DAY, 0);
            dropoffCal.set(Calendar.MINUTE, 0);
            dropoffCal.set(Calendar.SECOND, 0);
            dropoffCal.set(Calendar.MILLISECOND, 0);

            pickupCal.set(Calendar.HOUR, 0);
            pickupCal.set(Calendar.HOUR_OF_DAY, 0);
            pickupCal.set(Calendar.MINUTE, 0);
            pickupCal.set(Calendar.SECOND, 0);
            pickupCal.set(Calendar.MILLISECOND, 0);

            Log.v("TEST", "PickupCal: " + pickupCal.getTime());
            Log.v("TEST", "DropoffCal: " + dropoffCal.getTime());
            if (pickupCal.getTime().compareTo(dropoffCal.getTime()) >= 0) {
                dropoffDateLayout.setStatus(StatusRelativeLayout.STATUS_ERROR);
            } else {
                dropoffDateLayout.setStatus(StatusRelativeLayout.STATUS_OK);
            }
        } catch (Exception ex) {
            Log.v("TEST", ex.getMessage());
        }
    }

    @Override
    public void onZoneChanged(String tag, Zone zone, Office office) {

        if (tag.equals(TAG_PICKUP_ZONE)) {
            pickupZoneLabel.setText(office.getName() + " (" + zone.getName() + ")");
            pickupZone = zone;
            pickupOffice = office;
            pickupZoneLayout.setStatus(StatusRelativeLayout.STATUS_OK);
        } else if (tag.equals(TAG_DROPOFF_ZONE)) {
            dropoffZoneLabel.setText(office.getName() + " (" + zone.getName() + ")");
            dropoffZone = zone;
            dropoffOffice = office;
            dropoffZoneLayout.setStatus(StatusRelativeLayout.STATUS_OK);
        }
    }

    @Override
    public void onTimeChanged(String tag, String time) {
        if (tag.equals(TAG_PICKUP_TIME)) {
            pickupTime = time;
            pickupTimeLabel.setText(time);
            pickupTimeLayout.setStatus(StatusRelativeLayout.STATUS_OK);
        } else if (tag.equals(TAG_DROPOFF_TIME)) {
            dropoffTime = time;
            dropoffTimeLabel.setText(time);
            dropoffTimeLayout.setStatus(StatusRelativeLayout.STATUS_OK);
        }
    }
}
