package com.canarias.rentacar.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
/**
 * Modelo que define un resultado de búsqueda. Se anotan los atributos para
 * parsearlos desde el XML
 */
@Root(name = "car")
public class SearchResult {


    private Car car;
    @Element(name = "code")
    private int code;
    @Element(name = "totalPrice")
    private String totalPrice;

    private Price price;
    @Element(name = "identifier")
    private String identifier;
    @Element(name = "confirmAut")
    private int confirmAut;

    @Attribute(name = "Cat")
    private String category;

    @Attribute(name = "Sipp")
    private String sippCode;

    @Attribute(name = "Url")
    private String Url;

    @Element(name = "image")
    private String imageUrl;

    @Attribute(name = "Group")
    private String group;

    @Element(name = "description")
    private String description;

    public SearchResult() {
    }

    public SearchResult(Car car, Price price, String identifier) {
        this.car = car;
        this.price = price;
        this.identifier = identifier;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSippCode() {
        return sippCode;
    }

    public void setSippCode(String sippCode) {
        this.sippCode = sippCode;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getConfirmAut() {
        return confirmAut;
    }

    public void setConfirmAut(int confirmAut) {
        this.confirmAut = confirmAut;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Price getPrice() {
        if (price == null && totalPrice != null) {
            NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
            try {
                Number number = format.parse(totalPrice);
                return new Price(number.floatValue(), "EUR");
            } catch (ParseException ex) {
                return price;
            }
        }
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String toString() {

        StringBuilder ret = new StringBuilder();
        ret.append("Search Result: \n");
        ret.append("\t- Model: " + car.getModel() + "\n");
        ret.append("\t- Code: " + car.getCode() + "\n");
        ret.append("\t- Image: " + car.getAttribute("image") + "\n");
        ret.append("\t- Price: " + getPrice() + "\n");
        ret.append("\t- Identifier: " + getIdentifier() + "\n\n");


        return ret.toString();

    }

}
