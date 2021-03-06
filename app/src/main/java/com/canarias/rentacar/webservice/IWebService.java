/**
 *
 */
package com.canarias.rentacar.webservice;

import com.canarias.rentacar.model.webservice.Response;

/**
 * Created by David on 26/10/2014.
 * Interfaz que define las operaciones a realizar por el servicio web
 */
public interface IWebService {
    public Response availability(String startDate,
                                 String startTime, String endDate, String endTime,
                                 String deliverySP, String returnSP);


    public Response listDestinations();

    public Response getAllCars();

    public Response doReservation(String identifier,
                                  String customerName, String customerSurname, String customerPhone,
                                  String customerEmail, String flightNumber, String comments, String birthDate,
                                  String extras);

    public Response updateReservation(String identifier,
                                      String customerName, String customerSurname, String customerPhone,
                                      String flightNumber, String comments, String birthDate,
                                      String extras, String orderId);

    public Response getReservation(String orderId);

    public Response getExtras(String startDate,
                              String startTime, String endDate, String endTime,
                              String deliverySP, String returnSP);

    public Response cancelReservation(String orderId);


}
