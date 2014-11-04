package com.canarias.rentacar.model.webservice;

import com.canarias.rentacar.model.Car;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by David on 03/09/2014.
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
