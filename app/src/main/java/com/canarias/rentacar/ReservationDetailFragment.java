package com.canarias.rentacar;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.canarias.rentacar.async.ImageDownloader;
import com.canarias.rentacar.config.Config;
import com.canarias.rentacar.db.dao.ReservationDataSource;
import com.canarias.rentacar.model.Extra;
import com.canarias.rentacar.model.Reservation;
import com.canarias.rentacar.utils.Utils;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReservationDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";
    public static final String SHOW_TOAST = "show_toast";
    private Reservation mItem;
    private String mToastText;

    public ReservationDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            ReservationDataSource ds = new ReservationDataSource(getActivity());

            try {
                ds.open();

                mItem = ds.getReservation(getArguments().getString(ARG_ITEM_ID));

                ds.close();

            } catch (SQLException ex) {

            }


        }

        if (getArguments().containsKey(SHOW_TOAST)) {
            mToastText = getArguments().getString(SHOW_TOAST);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reservation_detail, container, false);


        if (mItem != null) {

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


                float price = e.getQuantity() * e.getPrice();


                if (e.getPriceType().equals(Extra.PriceType.DAILY)) {
                    Log.v("TEST", "Precio por dia, " + dateDiff + " dias");
                    price = price * dateDiff;
                } else {
                    Log.v("TEST", "Precio por alquiler");
                    price = price;
                }

                /*if(Extra.extraPriceType.containsKey(e.getModelCode())){
                    int str = Extra.extraPriceType.get(e.getModelCode());
                    if(str == R.string.priceTypeDaily){
                        //Precio por dia
                        Log.v("TEST", "Precio por dia, " + dateDiff + " dias");
                        price = price * dateDiff * (1 + (Config.TAX / 100));
                    } else {
                        //Precio por alquiler
                        Log.v("TEST", "Precio por alquiler");
                        price = price * (1 + (Config.TAX / 100));
                    }
                } else {
                    Log.v("TEST", "Tipo de precio no encontrado, default por alquiler");
                    price = price * (1 + (Config.TAX / 100));
                }*/

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
                //Generate a different id for price TextView
                tvPrice.setId(Integer.parseInt(e.getCode() + "12"));

                tvPrice.setText(price + "€");

                extrasFrame.addView(tvPrice);

                lastView = tvConcept;
                lastViewRight = tvPrice;
            }

            float fCarPrice = mItem.getPrice().getAmount() - calculatedTotal;

            TextView carPrice = (TextView) rootView.findViewById(R.id.bookingDetailsTxtCarPrice);
            carPrice.setText(fCarPrice + "€");

            TextView totalPrice = (TextView) rootView.findViewById(R.id.bookingDetailsTotalValue);
            totalPrice.setText(mItem.getPrice().getAmount() + "€");

            //Customer Data
            TextView customerNameDateBirth = (TextView) rootView.findViewById(R.id.resDetailTitularNombreFecha);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            customerNameDateBirth.setText(mItem.getCustomer().getName() + " - " + sdf.format(mItem.getCustomer().getBirthDate()));

            TextView customerEmail = (TextView) rootView.findViewById(R.id.resDetailTitularEmail);
            customerEmail.setText(mItem.getCustomer().getEmail());

            TextView lblPickupPoint = (TextView) rootView.findViewById(R.id.resDetailRecogidaZona);
            TextView lblDropoffPoint = (TextView) rootView.findViewById(R.id.resDetailDevolucionZona);
            TextView lblPickupDateTime = (TextView) rootView.findViewById(R.id.resDetailRecogidaFecha);
            TextView lblDropoffDateTime = (TextView) rootView.findViewById(R.id.resDetailDevolucionFecha);

            sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");

            lblPickupPoint.setText(mItem.getDeliveryOffice().getName()
                    + " (" + mItem.getDeliveryOffice().getZone().getName() + ")");
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

            if (mToastText != null && !mToastText.isEmpty()) {
                Toast.makeText(getActivity(), mToastText, Toast.LENGTH_SHORT).show();
            }
        }

        return rootView;
    }


}


