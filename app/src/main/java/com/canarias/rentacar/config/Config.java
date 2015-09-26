package com.canarias.rentacar.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Clase Final que define algunos parámetros de configuración
 * usados en el resto de la aplicación, así como las claves usadas
 * para transferir los datos entre fragments y activities.
 */
public final class Config {

    //Claves
    public static final String ARG_AVAILABILITY_IDENTIFIER = "availability_identifier";
    public static final String ARG_PICKUP_ZONE = "pickup_zone";
    public static final String ARG_PICKUP_POINT = "pickup_point";
    public static final String ARG_DROPOFF_ZONE = "dropoff_zone";
    public static final String ARG_DROPOFF_POINT = "dropoff_point";
    public static final String ARG_PICKUP_DATE = "pickup_date";
    public static final String ARG_PICKUP_TIME = "pickup_time";
    public static final String ARG_DROPOFF_DATE = "dropoff_date";
    public static final String ARG_DROPOFF_TIME = "dropoff_time";
    public static final String ARG_CAR_MODEL = "car_model";
    public static final String ARG_CAR_PRICE = "car_price";
    public static final String ARG_CAR_IMAGE = "car_image";
    public static final String ARG_EXTRAS = "extras";
    public static final String ARG_CUSTOMER_NAME = "customer_name";
    public static final String ARG_CUSTOMER_LASTNAME = "customer_lastname";
    public static final String ARG_CUSTOMER_EMAIL = "customer_email";
    public static final String ARG_CUSTOMER_PHONE = "customer_phone";
    public static final String ARG_CUSTOMER_BIRTHDATE = "customer_birthdate";
    public static final String ARG_COMMENTS = "comments";
    public static final String ARG_FLIGHT_NUMBER = "flight_number";
    public static final String ARG_EXTRAS_TO_XML = "extras_xml";
    public static final String ARG_ORDER_ID = "order_id";
    public static final String[] FLIGHT_NUMBER_MANDATORY_OFFICE_CODES = new String[]{"92", "93"};
    public static final String ARG_PICKUP_POINT_LAYOUT_STATE = "pickup_point_layout_state";
    public static final String ARG_DROPOFF_POINT_LAYOUT_STATE = "dropoff_point_layout_state";
    public static final String ARG_PICKUP_DATE_LAYOUT_STATE = "pikcup_date_layout_state";
    public static final String ARG_DROPOFF_DATE_LAYOUT_STATE = "dropoff_date_layout_state";
    public static final String ARG_PICKUP_TIME_LAYOUT_STATE = "pickup_time_layout_state";
    public static final String ARG_DROPOFF_TIME_LAYOUT_STATE = "dropoff_time_layout_state";
    public static final java.lang.String ARG_OPEN_CANCEL_DIALOG = "open_cancel_dialog";
    public static final String ARG_SELECTED_CAR = "selected_car";

    //URL del Servicio Web
    public static String WEBSERVICE_PATH = "https://rentacar.canarias.com/CarHireXML_android.asp";
    //Código de Agente en el sistema de Canarias.com
    public static String AGENCY_CODE = "8EZTDYTH1G";
    //Contraseña de Agente en el sistema de Canarias.com
    public static String AGENCY_PASS = "9Cxbi2yyI1M2X2x";
    //Código de Moneda
    public static String CURRENCY = "EUR";
    //Idioma por defecto
    public static String DEFAULT_LANGUAGE = "en";
    //Porcentaje de Impuesto
    public static float TAX = 13.5F;
    //Días para volver a sincronizar el contenido de vehículos y zonas
    public static int DAYS_TO_SYNC_CONTENT = 30;
    //Latitud por defecto para situar el mapa
    public static double DEFAULT_LAT = 28.267417;
    //Longitud por defecto para situar el mapa
    public static double DEFAULT_LNG = -16.607208;
    //Tiempo límite para las conexiones HTTP
    public static int HTTP_TIMEOUT = 20000;
    //Habilitar el uso del tiempo límite en las conexiones HTTP
    public static boolean ENABLE_HTTP_TIMEOUT = true;


    /**
     * Devuelve el código de idioma soportado por el sistema de Canarias.com
     * @param lang idioma de la aplicación
     * @return el código de idioma del servicio web correspondiente al
     * codigo de idioma de la aplicacion
     */
    public final static String getLanguageCode(String lang) {
        List<String> supportedLangs =
                new ArrayList<String>(Arrays.asList("es", "en", "fr", "de", "it", "ru"));


        if (supportedLangs.contains(lang))
            return lang;

        return DEFAULT_LANGUAGE;
    }

    //Traducción de la palabra 'Sí' en los idiomas soportados
    //por el servicio web.
    public static HashMap<String, String> yesTranslations;

    static {
        yesTranslations = new HashMap<String, String>();
        yesTranslations.put("es", "Sí");
        yesTranslations.put("en", "Yes");
        yesTranslations.put("de", "Ja");
        yesTranslations.put("fr", "Oui");
        yesTranslations.put("it", "Si");
        yesTranslations.put("ru", "&#1044;&#1072;");

    }



}
