package com.canarias.rentacar.model.webservice;

import com.canarias.rentacar.model.Customer;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

/**
 * Created by David on 03/09/2014.
 */
@Root
public class GetReservationResponse extends Response {

    @Element(name = "code")
    private String code;

    @Element(name = "status")
    private String status;

    @Element(name = "car")
    private String car;

    @Element(name = "carGroup")
    private String carGroup;

    @Element(name = "startDate")
    private String startDate;

    @Element(name = "endDate")
    private String endDate;

    @Element(name = "deliveryServicePoint")
    private String deliveryServicePoint;

    @Element(name = "returnServicePoint")
    private String returnServicePoint;

    @Element(name = "total")
    private String total;

    @Element(name = "comments", required = false)
    private String comments;

    @Element(name = "flightNum", required = false)
    private String flightNum;

    @Element(name = "customer")
    private Customer customer;

    @ElementList
    private ArrayList<ReservationLine> lines;

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getFlightNum() {
        return flightNum;
    }

    public void setFlightNum(String flightNum) {
        this.flightNum = flightNum;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

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

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public String getCarGroup() {
        return carGroup;
    }

    public void setCarGroup(String carGroup) {
        this.carGroup = carGroup;
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

    public String getDeliveryServicePoint() {
        return deliveryServicePoint;
    }

    public void setDeliveryServicePoint(String deliveryServicePoint) {
        this.deliveryServicePoint = deliveryServicePoint;
    }

    public String getReturnServicePoint() {
        return returnServicePoint;
    }

    public void setReturnServicePoint(String returnServicePoint) {
        this.returnServicePoint = returnServicePoint;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public ArrayList<ReservationLine> getLines() {
        return lines;
    }

    public void setLines(ArrayList<ReservationLine> lines) {
        this.lines = lines;
    }
}
