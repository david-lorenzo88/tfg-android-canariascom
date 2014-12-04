package com.canarias.rentacar.model.webservice;

import com.canarias.rentacar.model.Office;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

/**
 * Created by David on 03/11/2014.
 * Clase para deserializar la respuesta de descarga de oficinas.
 * Se anotan los atributos para parsearlos desde el XML
 */
@Root
public class ListDestinationsResponse extends Response {

    @ElementList(inline = true, entry = "servicePoint")
    private ArrayList<Office> servicePoints;

    public ArrayList<Office> getServicePoints() {
        return servicePoints;
    }

    public void setServicePoints(ArrayList<Office> servicePoints) {
        this.servicePoints = servicePoints;
    }
}
