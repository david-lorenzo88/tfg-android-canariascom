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
 * Created by David on 03/11/2014.
 * Implementación de la interfaz IWebService. Es la clase que gestiona las conexiones
 * con el servicio web de Canarias.com y realiza todas las operaciones, parsea el resultado
 * xml y devuelve objetos de negocio listos para procesar.
 */
public class WebServiceController implements IWebService {

    /**
     * Consulta de disponibilidad de vehículos
     * @param startDate Fecha de inicio
     * @param startTime Hora de inicio
     * @param endDate Fecha de fin
     * @param endTime Hora de fin
     * @param deliverySP Punto de recogida (código)
     * @param returnSP Punto de devolución (código)
     * @return Respuesta de disponibilidad
     */
    public Response availability(String startDate,
                                 String startTime, String endDate, String endTime,
                                 String deliverySP, String returnSP) {

        try {

            //Generamos el HashMap de parámetros
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

            //Ejecutamos la petición HTTPS
            InputStream result = httpRequest(params);


            if (result != null) {
                //Si hay resultado, creamos un serlizador para convertir el XML en objetos
                Serializer serializer = new Persister();
                try {

                    //Deserializamos el XML en objetos de negocio
                    AvailabilityResponse resp = serializer.read(AvailabilityResponse.class, result);

                    //Devolvemos la respuesta
                    return resp;

                } catch (Exception ex) {
                    //Manejo de errores
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

    /**
     * Confirma una reserva con el servicio web
     * @param identifier Identificador de disponibilidad recibido en la respuesta de disponibilidad
     * @param customerName Nombre del cliente
     * @param customerSurname Apellidos del cliente
     * @param customerPhone Teléfono del cliente
     * @param customerEmail E-mail del cliente
     * @param flightNumber Número de vuelo (solo requerido en aeropuertos)
     * @param comments Comentarios
     * @param birthDate Fecha de nacimiento del cliente
     * @param extras Extras (códigos separados por coma)
     * @return Respuesta de reserva
     */
    @Override
    public Response doReservation(String identifier, String customerName, String customerSurname, String customerPhone, String customerEmail, String flightNumber, String comments, String birthDate, String extras) {

        try {
            //Generamos el HashMap de parámetros
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

            //Ejecutamos la petición HTTPS
            InputStream result = httpRequest(params);

            if (result != null) {
                //Si hay resultado, creamos un serlizador para convertir el XML en objetos
                Serializer serializer = new Persister();
                try {

                    //Deserializamos el XML en objetos de negocio
                    MakeReservationResponse resp = serializer.read(MakeReservationResponse.class, result);


                    return resp;

                } catch (Exception ex) {
                    //Manejo de errores
                    Log.e("TEST", ex.getMessage());
                    return serializer.read(ErrorResponse.class, result);


                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }

    /**
     * Actualiza una reserva con el servicio web
     * @param identifier Identificador de disponibilidad recibido en la respuesta de disponibilidad
     * @param customerName Nombre del cliente
     * @param customerSurname Apellidos del cliente
     * @param customerPhone Teléfono del cliente
     * @param flightNumber Número de vuelo (solo requerido en aeropuertos)
     * @param comments Comentarios
     * @param birthDate Fecha de nacimiento del cliente
     * @param extras Extras (códigos separados por coma)
     * @param orderId Localizador de la reserva
     * @return Respuesta de reserva
     */
    @Override
    public Response updateReservation(String identifier, String customerName, String customerSurname, String customerPhone, String flightNumber, String comments, String birthDate, String extras, String orderId) {
        try {
            //Generamos el HashMap de parámetros
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
                //Si hay resultado, creamos un serlizador para convertir el XML en objetos
                Serializer serializer = new Persister();
                try {
                    //Deserializamos el XML en objetos de negocio
                    UpdateReservationResponse resp = serializer.read(UpdateReservationResponse.class, result);


                    return resp;

                } catch (Exception ex) {
                    //Manejo de errores
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

    /**
     * Obtiene una reserva desde el servicio web
     * @param orderId Localizador de la reserva
     * @return Respuesta de reserva
     */
    @Override
    public Response getReservation(String orderId) {
        try {
            //Generamos el HashMap de parámetros
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("function", "get_reservation");
            params.put("order_id", orderId);


            InputStream result = httpRequest(params);

            if (result != null) {
                //Si hay resultado, creamos un serlizador para convertir el XML en objetos
                Serializer serializer = new Persister();
                try {
                    //Deserializamos el XML en objetos de negocio
                    GetReservationResponse resp = serializer.read(GetReservationResponse.class, result);


                    return resp;

                } catch (Exception ex) {
                    //Manejo de errores
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

    /**
     * Consulta la disponibilidad de los extras
     * @param startDate Fecha de inicio
     * @param startTime Hora de inicio
     * @param endDate Fecha de fin
     * @param endTime Hora de fin
     * @param deliverySP Punto de recogida (código)
     * @param returnSP Punto de devolución (código)
     * @return Respuesta de disponibilidad de extras
     */
    @Override
    public Response getExtras(String startDate, String startTime, String endDate, String endTime, String deliverySP, String returnSP) {
        try {
            //Generamos el HashMap de parámetros
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
                //Si hay resultado, creamos un serlizador para convertir el XML en objetos
                Serializer serializer = new Persister();
                try {
                    //Deserializamos el XML en objetos de negocio
                    GetExtrasResponse resp = serializer.read(GetExtrasResponse.class, result);


                    return resp;

                } catch (Exception ex) {
                    //Manejo de errores
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

    /**
     * Cancela una reserva con el servicio web
     * @param orderId Localizador de la reserva
     * @return Respuesta de cancelación
     */
    @Override
    public Response cancelReservation(String orderId) {

        try {
            //Generamos el HashMap de parámetros
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("function", "cancel_reservation");
            params.put("order_id", orderId);


            InputStream result = httpRequest(params);

            if (result != null) {
                //Si hay resultado, creamos un serlizador para convertir el XML en objetos
                Serializer serializer = new Persister();
                try {
                    //Deserializamos el XML en objetos de negocio
                    CancelReservationResponse resp = serializer.read(CancelReservationResponse.class, result);


                    return resp;

                } catch (Exception ex) {
                    //Manejo de errores
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

    /**
     * Descarga las zonas y oficinas disponibles desde el servicio web
     * @return Respuesta de oficinas
     */
    public Response listDestinations() {
        List<SearchResult> srs = new ArrayList<SearchResult>();
        try {
            //Generamos el HashMap de parámetros
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("function", "list_destinations");

            InputStream result = httpRequest(params);

            if (result != null) {
                //Si hay resultado, creamos un serlizador para convertir el XML en objetos
                Serializer serializer = new Persister();
                try {
                    //Deserializamos el XML en objetos de negocio
                    ListDestinationsResponse resp = serializer.read(ListDestinationsResponse.class, result);


                    return resp;

                } catch (Exception ex) {
                    //Manejo de errores
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

    /**
     * Descarga los vehículos disponibles desde el servicio web
     * @return La respuesta de vehículos
     */
    public Response getAllCars() {
        List<SearchResult> srs = new ArrayList<SearchResult>();
        try {
            //Generamos el HashMap de parámetros
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("function", "get_all_cars");

            InputStream result = httpRequest(params);

            if (result != null) {
                //Si hay resultado, creamos un serlizador para convertir el XML en objetos
                Serializer serializer = new Persister();
                try {
                    //Deserializamos el XML en objetos de negocio
                    GetAllCarsResponse resp = serializer.read(GetAllCarsResponse.class, result);
                    return resp;

                } catch (Exception ex) {
                    //Manejo de errores
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

    /**
     * Realiza una petición HTTP y devuelve un Stream de respuesta
     * @param params Parámetros de la petición
     * @return Stream resultante
     */
    private InputStream httpRequest(HashMap<String, String> params) {
        //Se genera la URL de consulta
        //Primero se añaden los parámetros genéricos, que van en todas las
        //peticiones
        String sUrl = Config.WEBSERVICE_PATH + "?" + "agency_code="
                + Config.AGENCY_CODE + "&agency_pwd=" + Config.AGENCY_PASS +
                "&language=" + Config.getLanguageCode(Locale.getDefault().getLanguage());
        //Si hay parámetros extra, se añaden también
        if (params != null) {
            Set<String> keySet = params.keySet();
            Iterator<String> it = keySet.iterator();
            while (it.hasNext()) {
                String cur = it.next();
                sUrl += "&" + cur + "=" + params.get(cur);
            }

        }


        try {
            //Realizamos la petición
            URL url = new URL(sUrl);

            URLConnection urlConnection = url.openConnection();
            //Si hemos activado el Timeout, lo configuramos
            if(Config.ENABLE_HTTP_TIMEOUT) {
                urlConnection.setConnectTimeout(Config.HTTP_TIMEOUT);
                urlConnection.setReadTimeout(Config.HTTP_TIMEOUT);
            }
            //Obtenemos el resultado
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
