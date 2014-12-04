package com.canarias.rentacar.model.webservice;

import com.canarias.rentacar.model.Car;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by David on 03/11/2014.
 * Clase para deserializar la respuesta de detalle de vehículo
 * Se anotan los atributos para parsearlos desde el XML
 */
@Root
public class CarDetailsResponse extends Response {
    @Element(name = "car")
    private Car car;

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}
