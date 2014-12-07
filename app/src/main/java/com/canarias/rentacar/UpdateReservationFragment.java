package com.canarias.rentacar;


import android.app.Fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import com.canarias.rentacar.adapters.ExtrasAdapter;
import com.canarias.rentacar.async.GetExtrasAsyncTask;
import com.canarias.rentacar.async.ImageDownloader;
import com.canarias.rentacar.async.UpdateBookingAsyncTask;
import com.canarias.rentacar.config.Config;
import com.canarias.rentacar.db.dao.ReservationDataSource;
import com.canarias.rentacar.model.Extra;
import com.canarias.rentacar.model.Reservation;
import com.canarias.rentacar.utils.AnimationHelper;
import com.canarias.rentacar.utils.Utils;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * Fragmento que muestra el formulario para actualizar una reserva
 */
public class UpdateReservationFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_RES_ID = "res_id";

    //Estado del panel de resumen
    private static final int SUMMARY_STATUS_EXPANDED = 0;
    private static final int SUMMARY_STATUS_COLLAPSED = 1;
    private int mSummaryStatus = SUMMARY_STATUS_EXPANDED;

    private boolean mFlightNumMandatory = false;
    private String mResId;
    private Reservation mItem;
    private HashMap<Integer, Integer> extrasQuantity;

    private GetExtrasAsyncTask extrasTask;


    public UpdateReservationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UpdateReservationFragment.
     */
    public static UpdateReservationFragment newInstance(String resId) {
        UpdateReservationFragment fragment = new UpdateReservationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RES_ID, resId);
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Crea el fragment
     * @param savedInstanceState estado previo
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mResId = getArguments().getString(ARG_RES_ID);

            //Cargamos la reserva
            ReservationDataSource ds = new ReservationDataSource(getActivity());

            try {
                ds.open();

                mItem = ds.getReservation(getArguments().getString(ARG_RES_ID));

                extrasQuantity = new HashMap<Integer, Integer>();

                //Fill extrasQuantity
                if(mItem.getExtras() != null && mItem.getExtras().size() > 0){
                    for(Extra e : mItem.getExtras()){
                        extrasQuantity.put(e.getCode(), e.getQuantity());
                    }
                }


                if(savedInstanceState != null){
                    //Restauramos el estado previo
                    String extras = savedInstanceState.getString(Config.ARG_EXTRAS);


                    //Reemplazamos cualquier valor obtenido de base de datos
                    // con el valor restaurado
                    if(extras != null && !extras.isEmpty()){
                        String[] parts = extras.split(";");
                        for(String e : parts){
                            String[] eParts = e.split("-");
                            if(eParts.length == 2){
                                try {
                                    int code = Integer.parseInt(eParts[0]);
                                    int qty = Integer.parseInt(eParts[1]);
                                    extrasQuantity.put(code, qty);

                                }catch (NumberFormatException ex){
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                }


                ds.close();

            } catch (SQLException ex) {

            }
        }
    }

    /**
     * Llamado cuando el sistema operativo crea la vista del fragmento
     * @param inflater objeto para inflar las vistas
     * @param container la vista padre a la que el fragmento será asociado
     * @param savedInstanceState estado previo del fragmento cuando se está reconstruyendo
     * @return la vista generada para el fragmento
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_update_reservation, container, false);

        if (mItem != null) {

            //Comprobamos si el numero de vuelo debe ser obligatorio.
            for (String code : Config.FLIGHT_NUMBER_MANDATORY_OFFICE_CODES) {
                if (mItem.getDeliveryOffice().getCode().equals(code)
                        || mItem.getReturnOffice().getCode().equals(code)) {
                    mFlightNumMandatory = true;
                    break;
                }
            }


            Date pickupDate, dropoffDate;
            long dateDiff = 1;

            pickupDate = mItem.getStartDate();
            dropoffDate = mItem.getEndDate();

            long diff = dropoffDate.getTime() - pickupDate.getTime();

            dateDiff = TimeUnit.MILLISECONDS.toDays(diff);

            TextView localizer = (TextView) rootView.findViewById(R.id.localizer);
            localizer.setText(mItem.getLocalizer());

            TextView carModel = (TextView) rootView.findViewById(R.id.carModel);
            carModel.setText(mItem.getCar().getModel());


            Iterator<Extra> it = mItem.getExtras().iterator();
            float calculatedTotal = 0;

            View lastView = rootView.findViewById(R.id.extrasFrame);
            View lastViewRight = lastView;

            RelativeLayout extrasFrame = (RelativeLayout) rootView.findViewById(R.id.extrasFrame);

            while (it.hasNext()) {
                Extra e = it.next();

                //Calculamos el precio total del extra
                float price = e.getQuantity() * e.getPrice();
                if (e.getPriceType().equals(Extra.PriceType.DAILY)) {

                    price = price * dateDiff;
                }
                price = Utils.round(price);

                calculatedTotal += price;

                //Textview concepto
                RelativeLayout.LayoutParams lp =
                        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);

                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 1);
                lp.addRule(RelativeLayout.BELOW, lastView.getId());
                lp.setMargins(6, 6, 0, 6);
                TextView tvConcept = new TextView(getActivity());
                tvConcept.setId(e.getCode());
                tvConcept.setLayoutParams(lp);
                tvConcept.setText(e.getQuantity() + " x " + e.getName());

                extrasFrame.addView(tvConcept);

                lp =
                        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);

                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
                lp.addRule(RelativeLayout.BELOW, lastViewRight.getId());
                lp.setMargins(0, 6, 6, 6);
                TextView tvPrice = new TextView(getActivity());
                tvPrice.setLayoutParams(lp);
                //ID diferente para el TextView del precio
                tvPrice.setId(Integer.parseInt(e.getCode() + "12"));

                tvPrice.setText(String.format("%.02f", price) + "€");

                extrasFrame.addView(tvPrice);

                lastView = tvConcept;
                lastViewRight = tvPrice;
            }

            float fCarPrice = mItem.getPrice().getAmount() - calculatedTotal;

            TextView carPrice = (TextView) rootView.findViewById(R.id.carPrice);
            carPrice.setText(String.format("%.02f", Utils.round(fCarPrice)) + "€");

            TextView totalPrice = (TextView) rootView.findViewById(R.id.totalValue);
            totalPrice.setText(String.format("%.02f", mItem.getPrice().getAmount()) + "€");

            //Datos del cliente
            EditText customerBirthDate = (EditText) rootView.findViewById(R.id.customerBirthdate);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            customerBirthDate.setText(sdf.format(mItem.getCustomer().getBirthDate()));

            EditText customerEmail = (EditText) rootView.findViewById(R.id.customerEmail);
            customerEmail.setText(mItem.getCustomer().getEmail());

            EditText customerName = (EditText) rootView.findViewById(R.id.customerName);
            customerName.setText(mItem.getCustomer().getName());

            EditText customerSurname = (EditText) rootView.findViewById(R.id.customerSurname);
            customerSurname.setText(mItem.getCustomer().getSurname());

            EditText customerPhone = (EditText) rootView.findViewById(R.id.customerPhone);
            customerPhone.setText(mItem.getCustomer().getPhone());

            EditText comments = (EditText) rootView.findViewById(R.id.comments);
            comments.setText(mItem.getComments());

            EditText flightNum = (EditText) rootView.findViewById(R.id.flightNumber);
            flightNum.setText(mItem.getFlightNumber());

            if (!mFlightNumMandatory) {
                flightNum.setVisibility(View.GONE);
            }

            TextView lblPickupPoint = (TextView) rootView.findViewById(R.id.pickupPoint);
            TextView lblDropoffPoint = (TextView) rootView.findViewById(R.id.dropoffPoint);
            TextView lblPickupDateTime = (TextView) rootView.findViewById(R.id.pickupDate);
            TextView lblDropoffDateTime = (TextView) rootView.findViewById(R.id.dropoffDate);

            sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            lblPickupPoint.setText(mItem.getDeliveryOffice().getName()
                    + " (" + mItem.getDeliveryOffice().getZone().getName() + ")");
            lblDropoffPoint.setText(mItem.getReturnOffice().getName()
                    + " (" + mItem.getReturnOffice().getZone().getName() + ")");
            lblPickupDateTime.setText(
                    sdf.format(mItem.getStartDate()));
            lblDropoffDateTime.setText(
                    sdf.format(mItem.getEndDate()));


            ImageView carImage = (ImageView) rootView.findViewById(R.id.imageCar);
            ImageDownloader downloader = new ImageDownloader(9999, getActivity());
            downloader.download(mItem.getCar().getImageUrl(), carImage);


            final TextView summaryCollapsedText = (TextView) rootView.findViewById(R.id.summaryCollapsedText);
            summaryCollapsedText.setText(
                    getActivity().getString(R.string.showSummary)
                            + " (" + String.format("%.02f", mItem.getPrice().getAmount()) + "€)");

            final ImageView collapseBtn = (ImageView) rootView.findViewById(R.id.iconCollapse);
            collapseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSummaryStatus == SUMMARY_STATUS_EXPANDED) {
                        AnimationHelper.collapse(rootView.findViewById(R.id.bookingSummary));
                        mSummaryStatus = SUMMARY_STATUS_COLLAPSED;
                        summaryCollapsedText.setVisibility(View.VISIBLE);
                        AnimationHelper.rotate(collapseBtn, 0, 180, 500);
                    } else {
                        AnimationHelper.expand(rootView.findViewById(R.id.bookingSummary));
                        mSummaryStatus = SUMMARY_STATUS_EXPANDED;
                        summaryCollapsedText.setVisibility(View.GONE);
                        AnimationHelper.rotate(collapseBtn, 180, 0, 500);
                    }
                }
            });
            //Configuracion especifica para smartphones
            boolean isTablet = getActivity().getResources().getBoolean(R.bool.isTablet);
            if(!isTablet) {
                new CountDownTimer(700, 700) {

                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {
                        AnimationHelper.collapse(rootView.findViewById(R.id.bookingSummary));
                        mSummaryStatus = SUMMARY_STATUS_COLLAPSED;
                        summaryCollapsedText.setVisibility(View.VISIBLE);
                        AnimationHelper.rotate(collapseBtn, 0, 180, 500);
                    }
                }.start();
            }
            LinearLayout extrasLinearLayout =
                    (LinearLayout) rootView.findViewById(R.id.selectExtrasFrame);


            sdf = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

            HashMap<String, String> params = new HashMap<String, String>();
            params.put(Config.ARG_PICKUP_POINT,
                    mItem.getDeliveryOffice().getCode());
            params.put(Config.ARG_DROPOFF_POINT,
                    mItem.getReturnOffice().getCode());
            params.put(Config.ARG_PICKUP_DATE,
                    sdf.format(mItem.getStartDate()));
            params.put(Config.ARG_PICKUP_TIME,
                    sdfTime.format(mItem.getStartDate()));
            params.put(Config.ARG_DROPOFF_DATE,
                    sdf.format(mItem.getEndDate()));
            params.put(Config.ARG_DROPOFF_TIME,
                    sdfTime.format(mItem.getEndDate()));

            //Buscamos disponibilidad de los extras
            extrasTask = new GetExtrasAsyncTask(
                    getActivity(), params, extrasLinearLayout,
                    inflater, container, extrasQuantity);
            extrasTask.execute();


            //Evento para actualizar reserva
            Button btnUpdateBooking = (Button) rootView.findViewById(R.id.btnUpdateBooking);

            btnUpdateBooking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validateData(rootView)) {

                        HashMap<String, String> params = getFieldValues(rootView,
                                extrasTask.getExtrasQuantity(), mItem, extrasTask.getResult());

                        UpdateBookingAsyncTask task = new UpdateBookingAsyncTask(getActivity(),
                                params, getFragmentManager());
                        task.execute();
                    } else {
                        Toast.makeText(getActivity(),
                                getActivity().getString(R.string.btnSearchCarsInvalidStatus),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            });

        }

        getActivity().getActionBar().setTitle(getString(R.string.title_reservation_update));

        return rootView;
    }

    /**
     * Almacenamos el estado del fragmento para poder restaurarlo despues
     * @param outState parametros para almacenar el estado
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {

        if(extrasTask != null) {

            HashMap<Integer, Integer> extrasQuantity = extrasTask.getExtrasQuantity();
            Iterator<Integer> it = extrasQuantity.keySet().iterator();

            String extras = "";

            while (it.hasNext()) {
                int key = it.next();
                extras += key + "-" + extrasQuantity.get(key) + ";";
            }
            if (extras.length() > 0)
                extras = extras.substring(0, extras.length() - 1);
            outState.putString(Config.ARG_EXTRAS, extras);

        }

        super.onSaveInstanceState(outState);
    }

    /**
     * Validamos los datos de la interfaz antes de permitir la confirmación de la reserva
     * @param rootView vista raiz
     * @return true si el estado es valido o false en otro caso
     */
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

    /**
     * Obtiene los datos de las vistas y genera un HashMap de parametros
     * @param rootView vista raiz
     * @param extrasQuantity cantidad de cada extra
     * @param res reserva
     * @param extrasResult listado de extras
     * @return HashMap de parametros para confirmar la actualización de la reserva
     */
    private HashMap<String, String> getFieldValues(View rootView, HashMap<Integer, Integer> extrasQuantity,
                                                   Reservation res, List<Extra> extrasResult) {

        HashMap<String, String> map = new HashMap<String, String>();
        String extrasToXml = "", extras = "";
        Set<Integer> keys = extrasQuantity.keySet();

        //Serializamos los extras con el formato de la petición XML
        for (Integer line : keys) {

            int count = Integer.parseInt(extrasQuantity.get(line).toString());
            for (int i = 0; i < count; i++) {
                extrasToXml += line + ",";
            }
        }
        if (extrasToXml.length() > 0) {
            extrasToXml = extrasToXml.substring(0, extrasToXml.length() - 1);
        }

        map.put(Config.ARG_EXTRAS_TO_XML, extrasToXml);

        //Serializamos los extras para uso interno
        for (Integer line : keys) {

            int qty = Integer.parseInt(extrasQuantity.get(line).toString());
            if(qty > 0) {
                for (Extra e : extrasResult) {
                    if (e.getCode() == line) {
                        extras += line + ";" + qty + ";" + e.getName() + ";" + e.getPrice() + ";" + e.getModelCode() + "#";
                        break;
                    }
                }
            }

        }
        if (extras.length() > 0) {
            extras = extras.substring(0, extras.length() - 1);
        }

        map.put(Config.ARG_EXTRAS, extras);




        EditText custName = (EditText) rootView.findViewById(R.id.customerName);
        EditText custLastName = (EditText) rootView.findViewById(R.id.customerSurname);
        EditText custBirthdate = (EditText) rootView.findViewById(R.id.customerBirthdate);
        EditText custEmail = (EditText) rootView.findViewById(R.id.customerEmail);
        EditText custPhone = (EditText) rootView.findViewById(R.id.customerPhone);
        EditText flightNum = (EditText) rootView.findViewById(R.id.flightNumber);
        EditText comments = (EditText) rootView.findViewById(R.id.comments);

        map.put(Config.ARG_CUSTOMER_NAME, custName.getText().toString().trim());
        map.put(Config.ARG_CUSTOMER_LASTNAME, custLastName.getText().toString().trim());
        map.put(Config.ARG_CUSTOMER_BIRTHDATE, custBirthdate.getText().toString().trim());
        map.put(Config.ARG_CUSTOMER_EMAIL, custEmail.getText().toString().trim());
        map.put(Config.ARG_CUSTOMER_PHONE, custPhone.getText().toString().trim());
        map.put(Config.ARG_FLIGHT_NUMBER, flightNum.getText().toString().trim());
        map.put(Config.ARG_COMMENTS, comments.getText().toString().trim());


        SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

        map.put(Config.ARG_PICKUP_POINT, res.getDeliveryOffice().getCode());
        map.put(Config.ARG_DROPOFF_POINT, res.getReturnOffice().getCode());
        map.put(Config.ARG_PICKUP_DATE, sdfDate.format(res.getStartDate()));
        map.put(Config.ARG_DROPOFF_DATE, sdfDate.format(res.getEndDate()));
        map.put(Config.ARG_PICKUP_TIME, sdfTime.format(res.getStartDate()));
        map.put(Config.ARG_DROPOFF_TIME, sdfTime.format(res.getEndDate()));

        map.put(Config.ARG_CAR_MODEL, res.getCar().getModel());
        map.put(Config.ARG_AVAILABILITY_IDENTIFIER, res.getAvailabilityIdentifier());


        map.put(Config.ARG_ORDER_ID, res.getLocalizer());


        return map;
    }


}
