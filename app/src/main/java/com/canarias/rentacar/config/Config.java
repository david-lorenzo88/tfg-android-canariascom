package com.canarias.rentacar.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public final class Config {


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
    public static String WEBSERVICE_PATH = "http://rentacar.canarias.com/CarHireXML_android.asp";
    public static String AGENCY_CODE = "4O7GMLV8J1";
    public static String AGENCY_PASS = "tenerife2";
    public static String CURRENCY = "EUR";
    public static String DEFAULT_LANGUAGE = "en";
    public static int SUMMARY_TEXT_CAR_MODEL_MAX_SIZE = 20;
    public static float TAX = 13.5F;
    public static int DAYS_TO_SYNC_CONTENT = 1;

    public static double DEFAULT_LAT = 28.267417;
    public static double DEFAULT_LNG = -16.607208;

    public final static String getLanguageCode(String lang) {
        List<String> supportedLangs =
                new ArrayList<String>(Arrays.asList("es", "en", "fr", "de", "it", "ru"));


        if (supportedLangs.contains(lang))
            return lang;

        return DEFAULT_LANGUAGE;
    }

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
