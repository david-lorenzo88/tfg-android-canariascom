package com.canarias.rentacar.async;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.canarias.rentacar.R;
import com.canarias.rentacar.ReservationDetailActivity;
import com.canarias.rentacar.ReservationDetailFragment;
import com.canarias.rentacar.config.Config;
import com.canarias.rentacar.controller.WebServiceController;
import com.canarias.rentacar.db.dao.CarDataSource;
import com.canarias.rentacar.db.dao.CustomerDataSource;
import com.canarias.rentacar.db.dao.ExtraDataSource;
import com.canarias.rentacar.db.dao.OfficeDataSource;
import com.canarias.rentacar.db.dao.ReservationDataSource;
import com.canarias.rentacar.model.Car;
import com.canarias.rentacar.model.Customer;
import com.canarias.rentacar.model.Extra;
import com.canarias.rentacar.model.Office;
import com.canarias.rentacar.model.Price;
import com.canarias.rentacar.model.Reservation;
import com.canarias.rentacar.model.webservice.ErrorResponse;
import com.canarias.rentacar.model.webservice.MakeReservationResponse;
import com.canarias.rentacar.model.webservice.Response;
import com.canarias.rentacar.model.webservice.UpdateReservationResponse;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by David on 04/11/2014.
 * Tarea en segundo plano que realiza la actualización de una reserva con el servicio web
 */
public class UpdateBookingAsyncTask extends
        AsyncTask<Void, Void, Reservation> {

    private final static String FRAGMENT_TAG = "reservation_detail_fragment";

    //Parametros a enviar en la peticion
    HashMap<String, String> params;
    Context context;
    ErrorResponse error;
    AlertDialog loadingDialog;
    ProgressDialog progress;
    //Referencia al FragmentManager
    FragmentManager fragmentManager;

    public UpdateBookingAsyncTask(Context context,
                                HashMap<String, String> params, FragmentManager fragmentManager) {
        this.params = params;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }
    /**
     * Metodo que se ejecuta en segundo plano y realiza la actualización de la reserva
     * @param params Void
     * @return Resultado
     */
    @Override
    protected Reservation doInBackground(Void... params) {
        //Realizamos la actualizacion de la reserva con el servicio web
        WebServiceController wsc = new WebServiceController(
        );
        Response result = wsc.updateReservation(
                null,
                this.params.get(Config.ARG_CUSTOMER_NAME),
                this.params.get(Config.ARG_CUSTOMER_LASTNAME),
                this.params.get(Config.ARG_CUSTOMER_PHONE),
                this.params.get(Config.ARG_FLIGHT_NUMBER),
                this.params.get(Config.ARG_COMMENTS),
                this.params.get(Config.ARG_CUSTOMER_BIRTHDATE),
                this.params.get(Config.ARG_EXTRAS_TO_XML),
                this.params.get(Config.ARG_ORDER_ID));
        //Comprobamos si el tipo devuelto es de tipo UpdateReservationResponse
        if (result != null && result.getClass().equals(UpdateReservationResponse.class)) {

            //Almacenamos la reserva en base de datos
            UpdateReservationResponse resp = (UpdateReservationResponse) result;

            Reservation res = null;


            OfficeDataSource officeDS = new OfficeDataSource(context);
            CustomerDataSource customerDS = new CustomerDataSource(context);
            CarDataSource carDS = new CarDataSource(context);
            ReservationDataSource reservationDS = new ReservationDataSource(context);
            ExtraDataSource extraDS = new ExtraDataSource(context);

            try {

                res = new Reservation();

                officeDS.open();
                customerDS.open();
                carDS.open();
                reservationDS.open();
                extraDS.open();

                //Oficina de recogida
                Office deliveryOffice = officeDS.getOffice(this.params.get(Config.ARG_PICKUP_POINT));
                res.setDeliveryOffice(deliveryOffice);
                //Oficina de devolucion
                Office returnOffice = officeDS.getOffice(this.params.get(Config.ARG_DROPOFF_POINT));
                res.setReturnOffice(returnOffice);
                //Datos del cliente
                Customer cust = new Customer();
                cust.setEmail(this.params.get(Config.ARG_CUSTOMER_EMAIL));
                cust.setSurname(this.params.get(Config.ARG_CUSTOMER_LASTNAME));
                cust.setPhone(this.params.get(Config.ARG_CUSTOMER_PHONE));
                cust.setLanguage(Config.getLanguageCode(Locale.getDefault().getLanguage()));
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                cust.setBirthDate(sdf.parse(this.params.get(Config.ARG_CUSTOMER_BIRTHDATE)));
                cust.setName(this.params.get(Config.ARG_CUSTOMER_NAME));
                res.setCustomer(cust);
                //Coche
                Car car = carDS.getCar(this.params.get(Config.ARG_CAR_MODEL));
                res.setCar(car);
                //Otros valores
                res.setLocalizer(resp.getCode());
                res.setAvailabilityIdentifier(this.params.get(Config.ARG_AVAILABILITY_IDENTIFIER));
                res.setComments(this.params.get(Config.ARG_COMMENTS));

                sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                res.setStartDate(sdf.parse(this.params.get(Config.ARG_PICKUP_DATE) + " " + this.params.get(Config.ARG_PICKUP_TIME)));
                res.setEndDate(sdf.parse(this.params.get(Config.ARG_DROPOFF_DATE) + " " + this.params.get(Config.ARG_DROPOFF_TIME)));
                res.setState(resp.getStatus());
                res.setFlightNumber(this.params.get(Config.ARG_FLIGHT_NUMBER));
                res.setPrice(
                        new Price(
                                Float.parseFloat(resp.getTotal().replace(" eur", "").replace(".", "").replace(",", ".")),
                                "EUR"
                        )
                );

                //Extras
                //Generamos los extras parseando el string serializado
                //que se ha usado para pasar los extras desde la Activity
                List<Extra> extrasList = new ArrayList<Extra>();
                String[] extras = new String[0];
                if (this.params.get(Config.ARG_EXTRAS).length() > 0) {
                    extras = this.params.get(Config.ARG_EXTRAS).split("#");
                }

                if (extras.length > 0) {
                    for (String extra : extras) {
                        String[] parts = extra.split(";");

                        Extra oExtra = new Extra();
                        oExtra.setCode(Integer.parseInt(parts[0]));
                        oExtra.setQuantity(Integer.parseInt(parts[1]));
                        oExtra.setName(parts[2]);
                        oExtra.setPrice(Float.parseFloat(parts[3]));
                        oExtra.setModelCode(Integer.parseInt(parts[4]));
                        oExtra.setReservationCode(resp.getCode());
                        if (Extra.extraPriceType.containsKey(parts[4])) {
                            Extra.PriceType priceType = Extra.extraPriceType.get(parts[4]);
                            if (priceType.equals(Extra.PriceType.DAILY)) {
                                //Precio diario
                                oExtra.setPriceType(Extra.PriceType.DAILY);
                            } else {
                                //Precio total
                                oExtra.setPriceType(Extra.PriceType.TOTAL);
                            }
                        } else {
                            //Precio total
                            oExtra.setPriceType(Extra.PriceType.TOTAL);
                        }
                        extrasList.add(oExtra);
                    }
                }

                res.setExtras(extrasList);

                //Almacenamos los objetos en base de datos
                customerDS.update(cust, res.getLocalizer());
                reservationDS.update(res);

                extraDS.deleteExtrasFromReservation(res.getLocalizer());
                for (Extra e : extrasList) {

                    extraDS.insert(e);
                }



            } catch (SQLException ex) {

                ex.printStackTrace();
            } catch (ParseException ex) {

                ex.printStackTrace();
            } finally {
                officeDS.close();
                customerDS.close();
                carDS.close();
                reservationDS.close();
                extraDS.close();
            }
            //Devolvemos la reserva
            return res;

        } else {
            //Manejamos el error
            if (result != null && result.getClass().equals(ErrorResponse.class)) {
                error = (ErrorResponse) result;
            }
            return null;
        }
    }
    /**
     * Ejecutado al finalizar el doInBackground().
     * Actualiza la interfaz gráfica mostrando el resultado
     * @param result Resultado generado por el doInBackground()
     */
    protected void onPostExecute(Reservation result) {


        if (result != null) {
            //Reserva OK

            progress.dismiss();


            //Mostramos el detalle de la reserva
            Bundle arguments = new Bundle();
            arguments.putString(ReservationDetailFragment.ARG_ITEM_ID,
                    result.getLocalizer());
            arguments.putString(ReservationDetailFragment.SHOW_TOAST,
                    context.getString(R.string.updated_reservation));
            ReservationDetailFragment fragment = new ReservationDetailFragment();
            fragment.setArguments(arguments);
            fragmentManager.beginTransaction()
                    .add(R.id.reservation_detail_container, fragment, FRAGMENT_TAG)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)

                    .commit();

            fragmentManager.popBackStack("Reservation_Detail", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            //Error
            //Mostramos el error

            progress.dismiss();

            loadingDialog = new AlertDialog.Builder(context).create();

            loadingDialog.setTitle(context.getString(R.string.updating_booking_title));


            loadingDialog.setButton(Dialog.BUTTON_POSITIVE, context.getString(R.string.accept),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadingDialog.dismiss();
                        }
                    });

            if (error != null) {
                loadingDialog.setMessage(error.getCode() + error.getDescription());
            } else {
                loadingDialog.setMessage(context.getString(R.string.error));
            }


            loadingDialog.show();


        }
    }
    /**
     * Ejecutado antes de comenzar el doInBackground()
     * Mostramos un diálogo indicando que se está confirmando la reserva
     */
    protected void onPreExecute() {

        progress = new ProgressDialog(context);
        progress.setTitle(context.getString(R.string.updating_booking_title));
        progress.setMessage(context.getString(R.string.updating_booking));
        progress.show();


    }
}