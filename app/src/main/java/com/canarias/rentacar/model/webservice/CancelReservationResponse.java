package com.canarias.rentacar.model.webservice;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by David on 03/11/2014.
 * Clase para deserializar la respuesta de cancelacion de reserva.
 * Se anotan los atributos para parsearlos desde el XML
 */
@Root
public class CancelReservationResponse extends Response {
    @Element(name = "code")
    private String code;

    @Element(name = "status")
    private String status;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
