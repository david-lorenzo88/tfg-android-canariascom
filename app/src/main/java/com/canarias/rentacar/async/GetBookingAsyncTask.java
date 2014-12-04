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
import com.canarias.rentacar.model.webservice.GetReservationResponse;
import com.canarias.rentacar.model.webservice.MakeReservationResponse;
import com.canarias.rentacar.model.webservice.ReservationLine;
import com.canarias.rentacar.model.webservice.Response;
import com.canarias.rentacar.model.webservice.UpdateReservationResponse;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
/**
 * Created by David on 04/11/2014.
 * Tarea en segundo plano que realiza la descarga de una reserva desde el servicio web
 */
public class GetBookingAsyncTask extends
        AsyncTask<Void, Void, Reservation> {

    private final static String FRAGMENT_TAG = "reservation_detail_fragment";

    HashMap<String, String> params;
    Context context;
    ErrorResponse error;
    AlertDialog loadingDialog;
    ProgressDialog progress;
    //Referencia al fragmentManager
    FragmentManager fragmentManager;

    public GetBookingAsyncTask(Context context,
                                  HashMap<String, String> params, FragmentManager fragmentManager) {
        this.params = params;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }
    /**
     * Metodo que se ejecuta en segundo plano y realiza la descarga de la reserva
     * @param params Void
     * @return Resultado
     */
    @Override
    protected Reservation doInBackground(Void... params) {
        //Realizamos la descarga de la reserva desde el servicio web
        WebServiceController wsc = new WebServiceController(
        );
        Response result = wsc.getReservation(this.params.get(Config.ARG_ORDER_ID));

        //Comprobamos si el tipo devuelto es de tipo GetReservationResponse
        if (result != null && result.getClass().equals(GetReservationResponse.class)) {


            //Actualizamos la reserva en la base de datos local
            GetReservationResponse resp = (GetReservationResponse) result;

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
                cust.setEmail(resp.getCustomer().getEmail());
                cust.setSurname(resp.getCustomer().getSurname());
                cust.setPhone(resp.getCustomer().getPhone());
                cust.setLanguage(Config.getLanguageCode(Locale.getDefault().getLanguage()));
                SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
                cust.setBirthDate(sdfDate.parse(resp.getCustomer().getBirthDateXml()));
                cust.setName(resp.getCustomer().getName());
                res.setCustomer(cust);
                //Coche
                Car car = carDS.getCar(resp.getCar());
                res.setCar(car);
                //Otros valores
                res.setLocalizer(resp.getCode());
                res.setAvailabilityIdentifier(this.params.get(Config.ARG_AVAILABILITY_IDENTIFIER));
                res.setComments(resp.getComments());

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                res.setStartDate(sdf.parse(resp.getStartDate()));
                res.setEndDate(sdf.parse(resp.getEndDate()));
                res.setState(resp.getStatus());
                res.setFlightNumber(resp.getFlightNum());
                res.setPrice(
                        new Price(
                                Float.parseFloat(resp.getTotal().replace(" eur", "").replace(".", "").replace(",", ".")),
                                "EUR"
                        )
                );

                //Extras
                List<Extra> newExtras = new ArrayList<Extra>();

                boolean isFirst = true;

                for(ReservationLine l:resp.getLines()){

                    if(isFirst){
                        //Eliminamos la primera linea de reserva porque corresponde al vehículo
                        isFirst = false;

                    } else {

                        Extra ex = searchExtra(l.getItemCode(), newExtras);
                        if (ex == null) {
                            //Nuevo extra
                            ex = new Extra();
                            ex.setPrice(Float.parseFloat(l.getAmount().replace(",", ".")));
                            ex.setQuantity(1);
                            ex.setReservationCode(resp.getCode());
                            ex.setModelCode(l.getModelCode());
                            ex.setCode(l.getItemCode());
                            ex.setPriceType(Extra.extraPriceType.get(String.valueOf(l.getModelCode())));
                            ex.setName(l.getItem());
                            newExtras.add(ex);
                        } else {
                            //Extra existente, sumamos 1 a la cantidad
                            ex.setQuantity(ex.getQuantity() + 1);
                        }
                    }
                }



                res.setExtras(newExtras);

                //Almacenamos los objetos en la BD
                customerDS.update(cust, res.getLocalizer());
                reservationDS.update(res);

                extraDS.deleteExtrasFromReservation(res.getLocalizer());
                for (Extra e : newExtras) {

                    extraDS.insert(e);
                }



            } catch (SQLException ex) {

                ex.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                officeDS.close();
                customerDS.close();
                carDS.close();
                reservationDS.close();
                extraDS.close();
            }

            return res;

        } else {
            if (result != null && result.getClass().equals(ErrorResponse.class)) {
                error = (ErrorResponse) result;
            }
            return null;
        }
    }

    /**
     * Busca un extra en una lista de extras
     * @param code el codigo del extra a buscar
     * @param extras lista de extras
     * @return el extra si se encuentra o null en otro caso
     */
    private Extra searchExtra(int code, List<Extra> extras) {
        for(Extra e: extras)
            if(e.getCode() == code)
                return e;
        return null;
    }
    /**
     * Ejecutado al finalizar el doInBackground().
     * Actualiza la interfaz gráfica mostrando el resultado
     * @param result Resultado generado por el doInBackground()
     */
    protected void onPostExecute(Reservation result) {
        //Cerramos el dialogo de descarga
        progress.dismiss();

        if (result != null) {
            //Reserva OK




            //Mostramos detalle de la reserva
            Bundle arguments = new Bundle();
            arguments.putString(ReservationDetailFragment.ARG_ITEM_ID,
                    result.getLocalizer());
            arguments.putString(ReservationDetailFragment.SHOW_TOAST,
                    context.getString(R.string.updated_reservation));
            ReservationDetailFragment fragment = new ReservationDetailFragment();
            fragment.setArguments(arguments);
            fragmentManager.beginTransaction()
                    .replace(R.id.reservation_detail_container, fragment, FRAGMENT_TAG)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)

                    .commit();


        } else {
            //Error
            //Mostramos un diálogo con el error
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
     * Mostramos un diálogo indicando que se está descargando la reserva
     */
    protected void onPreExecute() {

        progress = new ProgressDialog(context);
        progress.setTitle(context.getString(R.string.updating_booking_title));
        progress.setMessage(context.getString(R.string.updating_booking));
        progress.show();


    }
}