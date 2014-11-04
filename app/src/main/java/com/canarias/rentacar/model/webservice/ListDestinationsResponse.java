package com.canarias.rentacar.model.webservice;

import com.canarias.rentacar.model.Office;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

/**
 * Created by David on 03/09/2014.
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
