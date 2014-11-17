package com.canarias.rentacar.controller;

import android.util.Log;

import com.canarias.rentacar.config.Config;
import com.canarias.rentacar.model.SearchResult;
import com.canarias.rentacar.model.webservice.AvailabilityResponse;
import com.canarias.rentacar.model.webservice.CancelReservationResponse;
import com.canarias.rentacar.model.webservice.ErrorResponse;
import com.canarias.rentacar.model.webservice.GetAllCarsResponse;
import com.canarias.rentacar.model.webservice.GetExtrasResponse;
import com.canarias.rentacar.model.webservice.GetReservationResponse;
import com.canarias.rentacar.model.webservice.ListDestinationsResponse;
import com.canarias.rentacar.model.webservice.MakeReservationResponse;
import com.canarias.rentacar.model.webservice.Response;
import com.canarias.rentacar.model.webservice.UpdateReservationResponse;
import com.canarias.rentacar.webservice.IWebService;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by David on 03/09/2014.
 */
public class WebServiceController implements IWebService {

    public Response availability(String startDate,
                                 String startTime, String endDate, String endTime,
                                 String deliverySP, String returnSP) {

        try {

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("function", "availability");
            params.put("start_date",
                    URLEncoder.encode(startDate + " " + startTime, "UTF-8"));
            params.put("end_date",
                    URLEncoder.encode(endDate + " " + endTime, "UTF-8"));
            params.put("delivery_service_point",
                    URLEncoder.encode(deliverySP, "UTF-8"));
            params.put("return_service_point",
                    URLEncoder.encode(returnSP, "UTF-8"));

            InputStream result = httpRequest(params);

            if (result != null) {
                Serializer serializer = new Persister();
                try {

                    AvailabilityResponse resp = serializer.read(AvailabilityResponse.class, result);


                    return resp;

                } catch (Exception ex) {

                    Log.e("TEST", ex.getMessage());
                    ErrorResponse error = serializer.read(ErrorResponse.class, result);

                    return error;
                }
            }


        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }


    @Override
    public Response doReservation(String identifier, String customerName, String customerSurname, String customerPhone, String customerEmail, String flightNumber, String comments, String birthDate, String extras) {

        try {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("function", "make_reservation");
            params.put("identifier", identifier);
            params.put("customer_name", URLEncoder.encode(customerName, "utf-8"));
            params.put("customer_last_name", URLEncoder.encode(customerSurname, "utf-8"));
            params.put("customer_email", customerEmail);
            params.put("customer_phone", URLEncoder.encode(customerPhone, "utf-8"));
            params.put("customer_birth_date", birthDate);
            params.put("comment", URLEncoder.encode(comments, "utf-8"));
            params.put("flight_number", URLEncoder.encode(flightNumber, "utf-8"));
            params.put("extras", extras);

            InputStream result = httpRequest(params);

            if (result != null) {
                Serializer serializer = new Persister();
                try {

                    MakeReservationResponse resp = serializer.read(MakeReservationResponse.class, result);


                    return resp;

                } catch (Exception ex) {

                    Log.e("TEST", ex.getMessage());
                    ErrorResponse error = serializer.read(ErrorResponse.class, result);

                    return error;
                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }

    @Override
    public Response updateReservation(String identifier, String customerName, String customerSurname, String customerPhone, String flightNumber, String comments, String birthDate, String extras, String orderId) {
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("function", "update_reservation");
            params.put("order_id", orderId);
            params.put("customer_name", URLEncoder.encode(customerName, "utf-8"));
            params.put("customer_last_name", URLEncoder.encode(customerSurname, "utf-8"));

            params.put("customer_phone", URLEncoder.encode(customerPhone, "utf-8"));
            params.put("customer_birth_date", birthDate);
            params.put("comment", URLEncoder.encode(comments, "utf-8"));
            params.put("flight_number", URLEncoder.encode(flightNumber, "utf-8"));
            if(extras != null)
                params.put("extras", extras);
            if(identifier != null)
                params.put("identifier", identifier);

            InputStream result = httpRequest(params);

            if (result != null) {
                Serializer serializer = new Persister();
                try {

                    UpdateReservationResponse resp = serializer.read(UpdateReservationResponse.class, result);


                    return resp;

                } catch (Exception ex) {

                    Log.e("TEST", ex.getMessage());
                    ErrorResponse error = serializer.read(ErrorResponse.class, result);

                    return error;
                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }

    @Override
    public Response getReservation(String orderId) {
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("function", "get_reservation");
            params.put("order_id", orderId);


            InputStream result = httpRequest(params);

            if (result != null) {
                Serializer serializer = new Persister();
                try {

                    GetReservationResponse resp = serializer.read(GetReservationResponse.class, result);


                    return resp;

                } catch (Exception ex) {

                    Log.e("TEST", ex.getMessage());
                    ErrorResponse error = serializer.read(ErrorResponse.class, result);

                    return error;
                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;

    }

    @Override
    public Response getExtras(String startDate, String startTime, String endDate, String endTime, String deliverySP, String returnSP) {
        try {

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("function", "get_extras");
            params.put("start_date",
                    URLEncoder.encode(startDate + " " + startTime, "UTF-8"));
            params.put("end_date",
                    URLEncoder.encode(endDate + " " + endTime, "UTF-8"));
            params.put("delivery_service_point",
                    URLEncoder.encode(deliverySP, "UTF-8"));
            params.put("return_service_point",
                    URLEncoder.encode(returnSP, "UTF-8"));

            InputStream result = httpRequest(params);

            if (result != null) {
                Serializer serializer = new Persister();
                try {

                    GetExtrasResponse resp = serializer.read(GetExtrasResponse.class, result);


                    return resp;

                } catch (Exception ex) {

                    Log.e("TEST", ex.getMessage());
                    ErrorResponse error = serializer.read(ErrorResponse.class, result);

                    return error;
                }
            }


        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }

    @Override
    public Response cancelReservation(String orderId) {

        try {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("function", "cancel_reservation");
            params.put("order_id", orderId);


            InputStream result = httpRequest(params);

            if (result != null) {
                Serializer serializer = new Persister();
                try {

                    CancelReservationResponse resp = serializer.read(CancelReservationResponse.class, result);


                    return resp;

                } catch (Exception ex) {

                    Log.e("TEST", ex.getMessage());
                    ErrorResponse error = serializer.read(ErrorResponse.class, result);

                    return error;
                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;

    }

    public Response listDestinations() {
        List<SearchResult> srs = new ArrayList<SearchResult>();
        try {

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("function", "list_destinations");

            InputStream result = httpRequest(params);

            if (result != null) {
                Serializer serializer = new Persister();
                try {

                    ListDestinationsResponse resp = serializer.read(ListDestinationsResponse.class, result);


                    return resp;

                } catch (Exception ex) {

                    Log.e("TEST", ex.getMessage());
                    ErrorResponse error = serializer.read(ErrorResponse.class, result);

                    return error;
                }
            }


        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }

    public Response getAllCars() {
        List<SearchResult> srs = new ArrayList<SearchResult>();
        try {

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("function", "get_all_cars");

            InputStream result = httpRequest(params);

            if (result != null) {
                Serializer serializer = new Persister();
                try {

                    GetAllCarsResponse resp = serializer.read(GetAllCarsResponse.class, result);
                    return resp;

                } catch (Exception ex) {

                    Log.e("TEST", ex.getMessage());
                    ErrorResponse error = serializer.read(ErrorResponse.class, result);

                    return error;
                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }

    private InputStream httpRequest(HashMap<String, String> params) {
        String sUrl = Config.WEBSERVICE_PATH + "?" + "agency_code="
                + Config.AGENCY_CODE + "&agency_pwd=" + Config.AGENCY_PASS +
                "&language=" + Config.getLanguageCode(Locale.getDefault().getLanguage());
        if (params != null) {
            Set<String> keySet = params.keySet();
            Iterator<String> it = keySet.iterator();
            while (it.hasNext()) {
                String cur = it.next();
                sUrl += "&" + cur + "=" + params.get(cur);
            }

        }
        Log.v("WS", sUrl);
        try {
            URL url = new URL(sUrl);

            URLConnection urlConnection = url.openConnection();

            if(Config.ENABLE_HTTP_TIMEOUT) {
                urlConnection.setConnectTimeout(Config.HTTP_TIMEOUT);
                urlConnection.setReadTimeout(Config.HTTP_TIMEOUT);
            }

            InputStream in = new BufferedInputStream(
                    urlConnection.getInputStream());

            return in;


        } catch (MalformedURLException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }

        return null;
    }
}
