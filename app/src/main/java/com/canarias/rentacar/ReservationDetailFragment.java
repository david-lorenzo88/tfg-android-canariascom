package com.canarias.rentacar;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.canarias.rentacar.async.CancelReservationAsyncTask;
import com.canarias.rentacar.async.GetBookingAsyncTask;
import com.canarias.rentacar.async.ImageDownloader;
import com.canarias.rentacar.config.Config;
import com.canarias.rentacar.db.dao.ReservationDataSource;
import com.canarias.rentacar.model.Extra;
import com.canarias.rentacar.model.Reservation;
import com.canarias.rentacar.utils.Utils;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


/**
 * Fragmento que muestra el detalle de una reserva
 */
public class ReservationDetailFragment extends Fragment {

    private final static String FRAGMENT_TAG = "reservation_detail_fragment";
    private final static String UPDATE_FRAGMENT_TAG = "reservation_update_fragment";

    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_LAUNCH_UPDATE = "launch_update";
    public static final String SHOW_TOAST = "show_toast";

    private Reservation mItem;
    private String mToastText;

    private static final String ACTION_CANCEL = "cancel";
    private static final String ACTION_UPDATE = "update";

    private boolean openCancelDialog = false;



    public ReservationDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Crea el fragmento
     * @param savedInstanceState estado previo para restaurar
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            openCancelDialog = savedInstanceState.getBoolean(Config.ARG_OPEN_CANCEL_DIALOG, false);
        }

        if (getArguments().containsKey(SHOW_TOAST)) {
            mToastText = getArguments().getString(SHOW_TOAST);

            getArguments().remove(SHOW_TOAST);
        }

        setHasOptionsMenu(true);
    }

    /**
     * Salvamos el estado del fragmento para restaurarlo despues
     * @param outState parametros para almacenar los datos
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putBoolean(Config.ARG_OPEN_CANCEL_DIALOG, openCancelDialog);

        super.onSaveInstanceState(outState);
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
        View rootView = inflater.inflate(R.layout.fragment_reservation_detail, container, false);


        if (getArguments().containsKey(ARG_ITEM_ID)) {
            //Cargamos la reserva
            ReservationDataSource ds = new ReservationDataSource(getActivity());

            try {
                ds.open();

                mItem = ds.getReservation(getArguments().getString(ARG_ITEM_ID));

                ds.close();

            } catch (SQLException ex) {

            }


        }

        if (mItem != null) {

            initLayout(rootView);

            //Si la reserva no está cancelada, descargamos desde WebService por si se
            //ha actualizado.
            if(getArguments().getBoolean(ARG_LAUNCH_UPDATE, false) &&
                    !mItem.getState().toLowerCase().contains("cancel")) {

                HashMap<String, String> params = new HashMap<String, String>();
                params.put(Config.ARG_ORDER_ID, mItem.getLocalizer());
                params.put(Config.ARG_PICKUP_POINT, mItem.getDeliveryOffice().getCode());
                params.put(Config.ARG_DROPOFF_POINT, mItem.getReturnOffice().getCode());
                params.put(Config.ARG_AVAILABILITY_IDENTIFIER, mItem.getAvailabilityIdentifier());
                GetBookingAsyncTask task = new GetBookingAsyncTask(getActivity(), params, getFragmentManager());
                task.execute();
            }
        }

        getActivity().getActionBar().setTitle(getString(R.string.title_reservation_detail));

        if(openCancelDialog)
            optionItemAction(ACTION_CANCEL);

        return rootView;
    }


    /**
     * Inicializa la interfaz
     * @param rootView vista raíz
     */
    private void initLayout(View rootView) {
        Date pickupDate, dropoffDate;
        long dateDiff = 1;

        pickupDate = mItem.getStartDate();
        dropoffDate = mItem.getEndDate();

        long diff = dropoffDate.getTime() - pickupDate.getTime();

        dateDiff = TimeUnit.MILLISECONDS.toDays(diff);


        TextView localizer = (TextView) rootView.findViewById(R.id.localizer);
        localizer.setText(mItem.getLocalizer());

        TextView status = (TextView) rootView.findViewById(R.id.reservation_status);
        String sStatus = mItem.getState().replace("&nbsp;", " ").substring(0, 10);
        try {
            sStatus = Config.getLanguageCode(Locale.getDefault().getLanguage()).equals("es") ?
                    mItem.getState().split("/")[0].replace("&nbsp;", " ").trim() :
                    mItem.getState().split("/")[1].replace("&nbsp;", " ").trim();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        status.setText(sStatus);

        ImageView iconStatus = (ImageView) rootView.findViewById(R.id.status_icon);
        if (mItem.getState().toLowerCase().contains("confirm")) {
            iconStatus.setImageDrawable(
                    getActivity().getResources().getDrawable(R.drawable.ic_done_black_48dp));
        } else if (mItem.getState().toLowerCase().contains("curso")) {
            iconStatus.setImageDrawable(
                    getActivity().getResources().getDrawable(R.drawable.ic_schedule_black_48dp));
        } else {
            iconStatus.setImageDrawable(
                    getActivity().getResources().getDrawable(R.drawable.ic_cancel_black_48dp));
        }

        TextView carModel = (TextView) rootView.findViewById(R.id.bookingDetailsCarModel);
        carModel.setText(mItem.getCar().getModel());

        Iterator<Extra> it = mItem.getExtras().iterator();
        float calculatedTotal = 0;

        View lastView = rootView.findViewById(R.id.extrasFrame);
        View lastViewRight = lastView;

        RelativeLayout extrasFrame = (RelativeLayout) rootView.findViewById(R.id.extrasFrame);

        while (it.hasNext()) {
            Extra e = it.next();

            //Calculamos el precio total del extra
            float price = e.getQuantity() * e.getPrice() * (1 + (Config.TAX / 100));


            //if (e.getPriceType().equals(Extra.PriceType.DAILY)) {
            //    price = price * dateDiff;
            //}

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
            //Generamos un id diferente para el TextView del precio
            tvPrice.setId(Integer.parseInt(e.getCode() + "12"));

            tvPrice.setText(String.format("%.02f", price) + "€");

            extrasFrame.addView(tvPrice);

            lastView = tvConcept;
            lastViewRight = tvPrice;
        }

        float fCarPrice = mItem.getPrice().getAmount() - calculatedTotal;


        TextView carPrice = (TextView) rootView.findViewById(R.id.bookingDetailsTxtCarPrice);
        carPrice.setText(String.format("%.02f", Utils.round(fCarPrice)) + "€");

        TextView totalPrice = (TextView) rootView.findViewById(R.id.bookingDetailsTotalValue);
        totalPrice.setText(String.format("%.02f", mItem.getPrice().getAmount()) + "€");

        //Datos del cliente
        TextView customerNameDateBirth = (TextView) rootView.findViewById(R.id.resDetailTitularNombreFecha);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        customerNameDateBirth.setText(mItem.getCustomer().getName() + " " +
                mItem.getCustomer().getSurname() + " - " +
                sdf.format(mItem.getCustomer().getBirthDate()));

        TextView customerEmail = (TextView) rootView.findViewById(R.id.resDetailTitularEmail);
        customerEmail.setText(mItem.getCustomer().getEmail());

        TextView lblPickupPoint = (TextView) rootView.findViewById(R.id.resDetailRecogidaZona);
        TextView lblDropoffPoint = (TextView) rootView.findViewById(R.id.resDetailDevolucionZona);
        TextView lblPickupDateTime = (TextView) rootView.findViewById(R.id.resDetailRecogidaFecha);
        TextView lblDropoffDateTime = (TextView) rootView.findViewById(R.id.resDetailDevolucionFecha);

        sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        lblPickupPoint.setText(mItem.getDeliveryOffice().getName()
                + " (" + mItem.getDeliveryOffice().getZone().getName() + ")" +
                (mItem.getFlightNumber() != null && !mItem.getFlightNumber().isEmpty() ?
                        " - ("+getString(R.string.vuelo) + ": "+mItem.getFlightNumber() + ")" :
                        ""));
        lblDropoffPoint.setText(mItem.getReturnOffice().getName()
                + " (" + mItem.getReturnOffice().getZone().getName() + ")");
        lblPickupDateTime.setText(
                sdf.format(mItem.getStartDate()));
        lblDropoffDateTime.setText(
                sdf.format(mItem.getEndDate()));

        TextView comments = (TextView) rootView.findViewById(R.id.resDetailComments);
        comments.setText(mItem.getComments());

        ImageView carImage = (ImageView) rootView.findViewById(R.id.car_image);
        ImageDownloader downloader = new ImageDownloader(9999, getActivity());
        downloader.download(mItem.getCar().getImageUrl(), carImage);
        //Si hemos pasado texto para mostrar en Toast, lo hacemos
        if (mToastText != null && !mToastText.isEmpty()) {
            Toast.makeText(getActivity(), mToastText, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Crea el menú de opciones
     * @param menu el menu
     * @param inflater el objeto para inflar la interfaz del menu
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.reservation_detail, menu);
        //Si la reserva está cancelada, o ha pasado el día de recogida
        //eliminamos los iconos de cancelar y actualizar reserva del menú
        if(mItem != null &&
                (mItem.getState().toLowerCase().contains("cancel")
                        || mItem.getStartDate().compareTo(new Date()) < 0)){
            menu.removeItem(R.id.action_cancel_reservation);
            menu.removeItem(R.id.action_update_reservation);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Callback para manejar el evento click en los items del menu
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
            NavUtils.navigateUpTo(getActivity(), new Intent(getActivity(), ReservationListActivity.class));
            return true;
        } else if (id == R.id.action_cancel_reservation) {

            optionItemAction(ACTION_CANCEL);

        } else if (id == R.id.action_update_reservation) {
            optionItemAction(ACTION_UPDATE);

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Gestiona las acciones a realizar cuando se presiona un item del menu
     * @param action acción a realizar
     */
    private void optionItemAction(String action){
        if(action.equals(ACTION_CANCEL)){
            //Cancelar reserva, mostramos diálogo de confirmación
            openCancelDialog = true;

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.cancel_reservation));
            builder.setMessage(getString(R.string.cancel_reservation_message));

            builder.setPositiveButton(getString(R.string.cancel_reservation_action_positive),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Cancel reservation
                            String reservationId = mItem.getLocalizer();
                            Log.v("TEST", "Cancelling: " + reservationId);

                            Fragment fragment = getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
                            View rootView = fragment.getView();


                            CancelReservationAsyncTask task =
                                    new CancelReservationAsyncTask(getActivity(),
                                            reservationId,
                                            (TextView) rootView.findViewById(R.id.reservation_status),
                                            (ImageView) rootView.findViewById(R.id.status_icon));
                            task.execute();
                            openCancelDialog = false;
                            dialog.dismiss();
                        }
                    });
            builder.setNegativeButton(getString(R.string.cancel_reservation_action_negative),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                            openCancelDialog = false;
                        }
                    });

            AlertDialog dialog = builder.create();

            dialog.show();

            if(Build.VERSION.SDK_INT >= 16)
                dialog.getButton(Dialog.BUTTON_POSITIVE).setBackground(
                        getResources().getDrawable(R.drawable.button_bg));
            else
                dialog.getButton(Dialog.BUTTON_POSITIVE).setBackgroundColor(
                        getResources().getColor(R.color.red_canarias));

            dialog.getButton(Dialog.BUTTON_POSITIVE).setTextColor(getResources()
                    .getColor(R.color.white));

        } else if (action.equals(ACTION_UPDATE)){

            //Actualizar reserva, realizamos la transición del fragment
            UpdateReservationFragment fragment = UpdateReservationFragment
                    .newInstance(mItem.getLocalizer());


            if(Build.VERSION.SDK_INT >= 13) {
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.animator.card_flip_right_in, R.animator.card_flip_right_out,
                                R.animator.card_flip_left_in, R.animator.card_flip_left_out)
                        .replace(R.id.reservation_detail_container, fragment, UPDATE_FRAGMENT_TAG)
                        .addToBackStack("Reservation_Detail")

                        .commit();
            } else {
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .replace(R.id.reservation_detail_container, fragment, UPDATE_FRAGMENT_TAG)
                        .addToBackStack("Reservation_Detail")

                        .commit();
            }
        }
    }


}


