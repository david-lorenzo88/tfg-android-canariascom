package com.canarias.rentacar.model;

import java.util.Date;
import java.util.List;
/**
 * Modelo que define una reserva.
 */
public class Reservation {
    private String localizer;
    private String availabilityIdentifier;
    private String state;
    private String flightNumber;
    private Date startDate;
    private Date endDate;
    private Car car;
    private Customer customer;
    private Office deliveryOffice;
    private Office returnOffice;
    private Price price;
    private String comments;
    private List<Extra> extras;

    public Reservation() {

    }

    public List<Extra> getExtras() {
        return extras;
    }

    public void setExtras(List<Extra> extras) {
        this.extras = extras;
    }

    public String getLocalizer() {
        return localizer;
    }

    public void setLocalizer(String localizer) {
        this.localizer = localizer;
    }

    public String getAvailabilityIdentifier() {
        return availabilityIdentifier;
    }

    public void setAvailabilityIdentifier(String availabilityIdentifier) {
        this.availabilityIdentifier = availabilityIdentifier;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

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

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
