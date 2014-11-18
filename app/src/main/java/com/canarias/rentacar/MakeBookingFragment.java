package com.canarias.rentacar;


import android.app.Fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.canarias.rentacar.async.ImageDownloader;
import com.canarias.rentacar.async.MakeBookingAsyncTask;
import com.canarias.rentacar.config.Config;
import com.canarias.rentacar.db.dao.OfficeDataSource;
import com.canarias.rentacar.db.dao.ZoneDataSource;
import com.canarias.rentacar.model.Extra;
import com.canarias.rentacar.model.Office;
import com.canarias.rentacar.model.Zone;
import com.canarias.rentacar.utils.AnimationHelper;
import com.canarias.rentacar.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MakeBookingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MakeBookingFragment extends Fragment {


    private static final int SUMMARY_STATUS_EXPANDED = 0;
    private int mSummaryStatus = SUMMARY_STATUS_EXPANDED;
    private static final int SUMMARY_STATUS_COLLAPSED = 1;
    private boolean mFlightNumMandatory = false;

    private EditText custName;
    private EditText custLastName;
    private EditText custBirthdate;
    private EditText custEmail;
    private EditText custPhone;
    private EditText flightNum;
    private EditText comments;

    private String valuecustName;
    private String valuecustLastName;
    private String valuecustBirthdate;
    private String valuecustEmail;
    private String valuecustPhone;
    private String valueflightNum;
    private String valuecomments;

    public MakeBookingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * ª
     *
     * @return A new instance of fragment MakeBookingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MakeBookingFragment newInstance() {
        MakeBookingFragment fragment = new MakeBookingFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {

            valuecustName = savedInstanceState.getString(Config.ARG_CUSTOMER_NAME);
            if (valuecustName == null)
                valuecustName = PreferenceManager
                        .getDefaultSharedPreferences(getActivity()).getString("customer_name", "");

            valuecustLastName = savedInstanceState.getString(Config.ARG_CUSTOMER_LASTNAME);
            if (valuecustLastName == null)
                valuecustLastName = PreferenceManager
                        .getDefaultSharedPreferences(getActivity()).getString("customer_surname", "");

            valuecustEmail = savedInstanceState.getString(Config.ARG_CUSTOMER_EMAIL);
            if (valuecustEmail == null)
                valuecustEmail = PreferenceManager
                        .getDefaultSharedPreferences(getActivity()).getString("customer_email", "");

            valuecustPhone = savedInstanceState.getString(Config.ARG_CUSTOMER_PHONE);
            if (valuecustPhone == null)
                valuecustPhone = PreferenceManager
                        .getDefaultSharedPreferences(getActivity()).getString("customer_phone", "");

            valuecustBirthdate = savedInstanceState.getString(Config.ARG_CUSTOMER_BIRTHDATE);
            if (valuecustBirthdate == null)
                valuecustBirthdate = PreferenceManager
                        .getDefaultSharedPreferences(getActivity()).getString("customer_birth_date", "");
        } else {

            valuecustName = PreferenceManager
                    .getDefaultSharedPreferences(getActivity()).getString("customer_name", "");


            valuecustLastName = PreferenceManager
                    .getDefaultSharedPreferences(getActivity()).getString("customer_surname", "");


            valuecustEmail = PreferenceManager
                    .getDefaultSharedPreferences(getActivity()).getString("customer_email", "");


            valuecustPhone = PreferenceManager
                    .getDefaultSharedPreferences(getActivity()).getString("customer_phone", "");


            valuecustBirthdate = PreferenceManager
                    .getDefaultSharedPreferences(getActivity()).getString("customer_birth_date", "");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_make_booking, container, false);

        Bundle args = getArguments();

        LinearLayout changeBtn = (LinearLayout) rootView.findViewById(R.id.changeBtn);
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        TextView lblPickupPoint = (TextView) rootView.findViewById(R.id.makeBookingPickupPointValue);
        TextView lblDropoffPoint = (TextView) rootView.findViewById(R.id.makeBookingDropoffPointValue);
        TextView lblPickupDateTime = (TextView) rootView.findViewById(R.id.makeBookingpickupDateValue);
        TextView lblDropoffDateTime = (TextView) rootView.findViewById(R.id.makeBookingDropoffDateValue);

        ImageView carImage = (ImageView) rootView.findViewById(R.id.makeBookingImageCar);
        TextView carModel = (TextView) rootView.findViewById(R.id.makeBookingCarModel);
        TextView carPrice = (TextView) rootView.findViewById(R.id.makeBookingTxtCarPrice);

        ImageDownloader downloader = new ImageDownloader(9999, getActivity());
        downloader.download(args.getString(Config.ARG_CAR_IMAGE), carImage);

        carModel.setText(args.getString(Config.ARG_CAR_MODEL));

        carPrice.setText(String.format("%.02f", args.getFloat(Config.ARG_CAR_PRICE)) + "€");

        ZoneDataSource zoneDS = new ZoneDataSource(getActivity());
        OfficeDataSource officeDS = new OfficeDataSource(getActivity());

        Date pickupDate, dropoffDate;
        long dateDiff = 1;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            pickupDate = sdf.parse(args.getString(Config.ARG_PICKUP_DATE)
                    + " " + args.getString(Config.ARG_PICKUP_TIME));
            dropoffDate = sdf.parse(args.getString(Config.ARG_DROPOFF_DATE)
                    + " " + args.getString(Config.ARG_DROPOFF_TIME));
            long diff = dropoffDate.getTime() - pickupDate.getTime();

            dateDiff = TimeUnit.MILLISECONDS.toDays(diff);

        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        try {
            zoneDS.open();
            officeDS.open();

            Office pickupPoint = officeDS.getOffice(args
                    .getString(Config.ARG_PICKUP_POINT));
            Office dropoffPoint = officeDS.getOffice(args
                    .getString(Config.ARG_DROPOFF_POINT));

            Zone pickupZone = zoneDS.getZone(args.getInt(Config.ARG_PICKUP_ZONE));
            Zone dropoffZone = zoneDS.getZone(args.getInt(Config.ARG_DROPOFF_ZONE));

            lblPickupPoint.setText(pickupPoint.getName() + " (" + pickupZone.getName() + ")");
            lblDropoffPoint.setText(dropoffPoint.getName() + " (" + dropoffZone.getName() + ")");
            lblPickupDateTime.setText(
                    args.getString(Config.ARG_PICKUP_DATE) + " - "
                            + args.getString(Config.ARG_PICKUP_TIME));
            lblDropoffDateTime.setText(
                    args.getString(Config.ARG_DROPOFF_DATE) + " - "
                            + args.getString(Config.ARG_DROPOFF_TIME));


        } catch (Exception ex) {
            ex.printStackTrace();
        } finally{
            zoneDS.close();
            officeDS.close();
        }

        //Comprobamos si el numero de vuelo debe ser obligatorio.
        for (String code : Config.FLIGHT_NUMBER_MANDATORY_OFFICE_CODES) {
            if (args.getString(Config.ARG_PICKUP_POINT).equals(code) || args
                    .getString(Config.ARG_DROPOFF_POINT).equals(code)) {
                mFlightNumMandatory = true;
                break;
            }
        }

        //Deserializamos los extras
        String[] extras = new String[0];
        if (args.getString(Config.ARG_EXTRAS).length() > 0) {
            extras = args.getString(Config.ARG_EXTRAS).split("#");
        }
        Log.v("TEST", args.getString(Config.ARG_EXTRAS));
        View lastView = rootView.findViewById(R.id.extrasFrame);
        View lastViewRight = lastView;
        RelativeLayout extrasFrame = (RelativeLayout) rootView.findViewById(R.id.extrasFrame);

        float totalPrice = args.getFloat(Config.ARG_CAR_PRICE);

        if (extras.length > 0) {
            for (String extra : extras) {
                String[] parts = extra.split(";");

                RelativeLayout.LayoutParams lp =
                        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);

                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 1);
                lp.addRule(RelativeLayout.BELOW, lastView.getId());
                lp.setMargins(6, 6, 0, 6);
                TextView tvConcept = new TextView(getActivity());
                tvConcept.setId(Integer.parseInt(parts[0]));
                tvConcept.setLayoutParams(lp);
                tvConcept.setText(parts[1] + " x " + parts[2]);

                extrasFrame.addView(tvConcept);

                lp =
                        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);

                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
                lp.addRule(RelativeLayout.BELOW, lastViewRight.getId());
                lp.setMargins(0, 6, 6, 6);
                TextView tvPrice = new TextView(getActivity());
                tvPrice.setLayoutParams(lp);
                //Generate a different id for price TextView
                tvPrice.setId(Integer.parseInt(parts[0] + "12"));
                float extraPrice = Float.parseFloat(parts[1]) * Float.parseFloat(parts[3]);

                if (Extra.extraPriceType.containsKey(parts[4])) {
                    Extra.PriceType priceType = Extra.extraPriceType.get(parts[4]);
                    if (priceType.equals(Extra.PriceType.DAILY)) {
                        //Precio por dia
                        extraPrice = extraPrice * dateDiff;
                    } else {
                        //Precio por alquiler
                        extraPrice = extraPrice;
                    }
                } else {
                    extraPrice = extraPrice;
                }
                extraPrice = Utils.round(extraPrice);

                totalPrice += extraPrice;

                tvPrice.setText(String.format("%.02f", extraPrice) + "€");

                extrasFrame.addView(tvPrice);

                lastView = tvConcept;
                lastViewRight = tvPrice;
            }
        }

        totalPrice = Utils.round(totalPrice);
        TextView totalPriceTv = (TextView) rootView.findViewById(R.id.makeBookingTotalValue);
        totalPriceTv.setText(String.format("%.02f", totalPrice) + "€");


        final TextView summaryCollapsedText = (TextView) rootView.findViewById(R.id.makeBookingSummaryCollapsedText);
        summaryCollapsedText.setText(
                getActivity().getString(R.string.showSummary)
                        + " (" + String.format("%.02f", totalPrice) + "€)");

        final ImageView collapseBtn = (ImageView) rootView.findViewById(R.id.makeBookingIconCollapse);
        collapseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSummaryStatus == SUMMARY_STATUS_EXPANDED) {
                    AnimationHelper.collapse(rootView.findViewById(R.id.makeBookingSummary));
                    mSummaryStatus = SUMMARY_STATUS_COLLAPSED;
                    summaryCollapsedText.setVisibility(View.VISIBLE);
                    AnimationHelper.rotate(collapseBtn, 0, 180, 500);
                } else {
                    AnimationHelper.expand(rootView.findViewById(R.id.makeBookingSummary));
                    mSummaryStatus = SUMMARY_STATUS_EXPANDED;
                    summaryCollapsedText.setVisibility(View.GONE);
                    AnimationHelper.rotate(collapseBtn, 180, 0, 500);
                }
            }
        });
        boolean isTablet = getActivity().getResources().getBoolean(R.bool.isTablet);
        if(!isTablet) {
            new CountDownTimer(700, 700) {

                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    AnimationHelper.collapse(rootView.findViewById(R.id.makeBookingSummary));
                    mSummaryStatus = SUMMARY_STATUS_COLLAPSED;
                    summaryCollapsedText.setVisibility(View.VISIBLE);
                    AnimationHelper.rotate(collapseBtn, 0, 180, 500);
                }
            }.start();
        }
        Button confirmBtn = (Button) rootView.findViewById(R.id.btnConfirmBooking);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Confirm Booking

                if (validateData(rootView)) {

                    HashMap<String, String> params = getFieldValues();

                    MakeBookingAsyncTask task = new MakeBookingAsyncTask(getActivity(), params,
                            getFragmentManager());
                    task.execute();
                } else {
                    Toast.makeText(getActivity(),
                            getActivity().getString(R.string.btnSearchCarsInvalidStatus),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        custName = (EditText) rootView.findViewById(R.id.customerName);
        custLastName = (EditText) rootView.findViewById(R.id.customerSurname);
        custBirthdate = (EditText) rootView.findViewById(R.id.customerBirthdate);
        custEmail = (EditText) rootView.findViewById(R.id.customerEmail);
        custPhone = (EditText) rootView.findViewById(R.id.customerPhone);
        comments = (EditText) rootView.findViewById(R.id.comments);

        custName.setText(valuecustName);
        custLastName.setText(valuecustLastName);
        custEmail.setText(valuecustEmail);
        custPhone.setText(valuecustPhone);
        custBirthdate.setText(valuecustBirthdate);

        if (valuecomments != null) {
            comments.setText(valuecomments);
        }


        if (!mFlightNumMandatory) {
            flightNum = (EditText) rootView.findViewById(R.id.flightNumber);
            flightNum.setVisibility(View.GONE);
        } else {
            if (valueflightNum != null) {
                flightNum.setText(valueflightNum);
            }
        }

        getActivity().getActionBar().setTitle(getString(R.string.title_fragment_new_booking) + " - " + getString(R.string.title_fragment_make_booking));

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (custName != null)
            outState.putString(Config.ARG_CUSTOMER_NAME, custName.getText().toString());
        if (custLastName != null)
            outState.putString(Config.ARG_CUSTOMER_LASTNAME, custLastName.getText().toString());
        if (custEmail != null)
            outState.putString(Config.ARG_CUSTOMER_EMAIL, custEmail.getText().toString());
        if (custBirthdate != null)
            outState.putString(Config.ARG_CUSTOMER_BIRTHDATE, custBirthdate.getText().toString());
        if (custPhone != null)
            outState.putString(Config.ARG_CUSTOMER_PHONE, custPhone.getText().toString());
        if (comments != null)
            outState.putString(Config.ARG_COMMENTS, comments.getText().toString());
        if (flightNum != null)
            outState.putString(Config.ARG_FLIGHT_NUMBER, flightNum.getText().toString());


        super.onSaveInstanceState(outState);
    }

    private boolean validateData(View rootView) {

        EditText custName = (EditText) rootView.findViewById(R.id.customerName);
        EditText custBirthdate = (EditText) rootView.findViewById(R.id.customerBirthdate);
        EditText custEmail = (EditText) rootView.findViewById(R.id.customerEmail);
        EditText flightNum = (EditText) rootView.findViewById(R.id.flightNumber);

        boolean flag = true;

        if (mFlightNumMandatory && flightNum.getText().toString().trim().isEmpty()) {
            flag = false;
            flightNum.setBackgroundColor(getResources().getColor(R.color.status_error));
        }
        if (custName.getText().toString().trim().isEmpty()) {
            flag = false;
            custName.setBackgroundColor(getResources().getColor(R.color.status_error));
        }
        if (custEmail.getText().toString().trim().isEmpty()) {
            flag = false;
            custEmail.setBackgroundColor(getResources().getColor(R.color.status_error));
        }
        //Fecha
        String birthDate = custBirthdate.getText().toString().trim();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if (birthDate.isEmpty()) {
            flag = false;
            custBirthdate.setBackgroundColor(getResources().getColor(R.color.status_error));
        }

        try {
            sdf.parse(birthDate);
        } catch (ParseException ex) {
            flag = false;
            custBirthdate.setBackgroundColor(getResources().getColor(R.color.status_error));
        }

        return flag;

    }

    private HashMap<String, String> getFieldValues() {

        HashMap<String, String> map = new HashMap<String, String>();

        Bundle args = getArguments();

        map.put(Config.ARG_PICKUP_POINT, args.getString(Config.ARG_PICKUP_POINT));
        map.put(Config.ARG_DROPOFF_POINT, args.getString(Config.ARG_DROPOFF_POINT));
        map.put(Config.ARG_PICKUP_ZONE, String.valueOf(args.getInt(Config.ARG_PICKUP_ZONE)));
        map.put(Config.ARG_DROPOFF_ZONE, String.valueOf(args.getInt(Config.ARG_DROPOFF_ZONE)));

        map.put(Config.ARG_PICKUP_DATE, args.getString(Config.ARG_PICKUP_DATE));
        map.put(Config.ARG_DROPOFF_DATE, args.getString(Config.ARG_DROPOFF_DATE));
        map.put(Config.ARG_PICKUP_TIME, args.getString(Config.ARG_PICKUP_TIME));
        map.put(Config.ARG_DROPOFF_TIME, args.getString(Config.ARG_DROPOFF_TIME));

        String extras = "";


        String[] aExtras = new String[0];
        if (args.getString(Config.ARG_EXTRAS).length() > 0) {
            aExtras = args.getString(Config.ARG_EXTRAS).split("#");
        }
        for (String line : aExtras) {
            String[] parts = line.split(";");
            int count = Integer.parseInt(parts[1]);
            for (int i = 0; i < count; i++) {
                extras += parts[0] + ",";
            }
        }
        if (extras.length() > 0) {
            extras = extras.substring(0, extras.length() - 1);
        }

        map.put(Config.ARG_EXTRAS_TO_XML, extras);
        map.put(Config.ARG_EXTRAS, args.getString(Config.ARG_EXTRAS));
        map.put(Config.ARG_AVAILABILITY_IDENTIFIER,
                args.getString(Config.ARG_AVAILABILITY_IDENTIFIER));

        map.put(Config.ARG_CAR_MODEL, args.getString(Config.ARG_CAR_MODEL));


        /*custName = (EditText) rootView.findViewById(R.id.customerName);
        custLastName = (EditText) rootView.findViewById(R.id.customerSurname);
        custBirthdate = (EditText) rootView.findViewById(R.id.customerBirthdate);
        custEmail = (EditText) rootView.findViewById(R.id.customerEmail);
        custPhone = (EditText) rootView.findViewById(R.id.customerPhone);
        flightNum = (EditText) rootView.findViewById(R.id.flightNumber);
        comments = (EditText) rootView.findViewById(R.id.comments);*/

        map.put(Config.ARG_CUSTOMER_NAME, custName.getText().toString().trim());
        map.put(Config.ARG_CUSTOMER_LASTNAME, custLastName.getText().toString().trim());
        map.put(Config.ARG_CUSTOMER_BIRTHDATE, custBirthdate.getText().toString().trim());
        map.put(Config.ARG_CUSTOMER_EMAIL, custEmail.getText().toString().trim());
        map.put(Config.ARG_CUSTOMER_PHONE, custPhone.getText().toString().trim());
        map.put(Config.ARG_FLIGHT_NUMBER, flightNum.getText().toString().trim());
        map.put(Config.ARG_COMMENTS, comments.getText().toString().trim());


        return map;
    }


}
