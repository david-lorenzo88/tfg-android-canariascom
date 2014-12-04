package com.canarias.rentacar.model.webservice;

import com.canarias.rentacar.model.Extra;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

/**
 * Created by David on 03/11/2014.
 * Clase para deserializar la respuesta de disponibilidad de extras.
 * Se anotan los atributos para parsearlos desde el XML
 */
@Root
public class GetExtrasResponse extends Response {

    @ElementList
    private ArrayList<Extra> extras;

    public ArrayList<Extra> getExtras() {
        return extras;
    }

    public void setExtras(ArrayList<Extra> extras) {
        this.extras = extras;
    }
}
