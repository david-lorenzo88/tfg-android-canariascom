package com.canarias.rentacar.model.webservice;

import com.canarias.rentacar.model.Extra;
import com.canarias.rentacar.model.Office;
import com.canarias.rentacar.model.SearchResult;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

/**
 * Created by David on 03/11/2014.
 * Clase para deserializar la respuesta de disponibilidad.
 * Se anotan los atributos para parsearlos desde el XML
 */
@Root
public class AvailabilityResponse extends Response {

    @Element(name = "deliveryServicePoint")
    private Office deliveryOffice;
    @Element(name = "returnServicePoint")
    private Office returnOffice;
    @Element(name = "startDate")
    private String startDate;
    @Element(name = "endDate")
    private String endDate;
    @Element(name = "flightNumberMandatory")
    private int flightNumberMandatory;
    @Element(name = "responseTime")
    private String responseTime;

    @ElementList
    private ArrayList<Extra> extras;

    @ElementList(inline = true, entry = "car", required = false)
    private ArrayList<SearchResult> cars;

    public Office getDeliveryOffice() {
        return deliveryOffice;
    }

    public void setDeliveryOffice(Office deliveryOffice) {
        this.deliveryOffice = deliveryOffice;
    }

    public Office getReturnOffice() {
        return returnOffice;
    }

    public void setReturnOffice(Office returnOffice) {
        this.returnOffice = returnOffice;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getFlightNumberMandatory() {
        return flightNumberMandatory;
    }

    public void setFlightNumberMandatory(int flightNumberMandatory) {
        this.flightNumberMandatory = flightNumberMandatory;
    }

    public ArrayList<Extra> getExtras() {
        return extras;
    }

    public void setExtras(ArrayList<Extra> extras) {
        this.extras = extras;
    }

    public ArrayList<SearchResult> getCars() {
        return cars;
    }

    public void setCars(ArrayList<SearchResult> cars) {
        this.cars = cars;
    }
}
