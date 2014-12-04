package com.canarias.rentacar.model;

import com.canarias.rentacar.R;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.HashMap;

/**
 * Modelo que define un extra. Se anotan los atributos para
 * parsearlos desde el XML
 */
@Root
public class Extra {

    public static HashMap<String, Integer> extraIcons;

    static {
        extraIcons = new HashMap<String, Integer>();
        extraIcons.put("2", R.drawable.ic_action_person);
        extraIcons.put("248", R.drawable.ic_action_person);
        extraIcons.put("209", R.drawable.ic_action_person);
        extraIcons.put("237", R.drawable.ic_action_person);
    }

    public static HashMap<String, PriceType> extraPriceType;

    static {
        extraPriceType = new HashMap<String, PriceType>();
        extraPriceType.put("2", PriceType.DAILY);
        extraPriceType.put("248", PriceType.TOTAL);
        extraPriceType.put("209", PriceType.DAILY);
        extraPriceType.put("237", PriceType.DAILY);
    }

    @Element(name = "code")
    private int code;
    @Element(name = "name")
    private String name;
    @Element(name = "modelCode")
    private int modelCode;
    @Element(name = "dayPrice")
    private String xmlPrice;
    private float price;
    private PriceType priceType;
    private String reservationCode; //Localizer
    private int quantity;

    public Extra() {
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getModelCode() {
        return modelCode;
    }

    public void setModelCode(int modelCode) {
        this.modelCode = modelCode;
    }

    public String getXmlPrice() {
        return xmlPrice;
    }

    public void setXmlPrice(String xmlPrice) {
        this.xmlPrice = xmlPrice;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getReservationCode() {
        return reservationCode;
    }

    public void setReservationCode(String reservationCode) {
        this.reservationCode = reservationCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {

        if (price == 0 && xmlPrice != null && !xmlPrice.isEmpty()) {
            price = Float.parseFloat(xmlPrice.replace(",", "."));
        }
        return price;

    }

    public void setPrice(float price) {
        this.price = price;
    }

    public PriceType getPriceType() {
        return priceType;
    }

    public void setPriceType(PriceType priceType) {
        this.priceType = priceType;
    }

    public enum PriceType {
        DAILY, TOTAL;


    }
}
