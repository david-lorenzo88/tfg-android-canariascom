package com.canarias.rentacar.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementUnion;
import org.simpleframework.xml.Root;

@Root
public class Office {
    @Element(name = "code")
    private String code;

    @ElementUnion({
            @Element(name = "name"),
            @Element(name = "description")
    })
    private String name;
    @Element(name = "latitude", required = false)
    private float latitude;
    @Element(name = "longitude", required = false)
    private float longitude;
    @Element(name = "type", required = false)
    private String type;
    @Element(name = "address", required = false)
    private String address;
    @Element(name = "phone", required = false)
    private String phone;
    @Element(name = "fax", required = false)
    private String fax;
    @Element(name = "deliveryConditions", required = false)
    private String deliveryConditions;
    @Element(name = "returnConditions", required = false)
    private String returnConditions;

    @Element(name = "location", type = Zone.class, required = false)
    private Zone zone;

    public Office() {
    }

    public Office(String name) {
        this.name = name;
    }

    public Office(String code, String name) {
        this.code = code;
        this.name = name;

    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float lat) {
        this.latitude = lat;
    }

    public String getDeliveryConditions() {
        return deliveryConditions;
    }

    public void setDeliveryConditions(String deliveryConditions) {
        this.deliveryConditions = deliveryConditions;
    }

    public String getReturnConditions() {
        return returnConditions;
    }

    public void setReturnConditions(String returnConditions) {
        this.returnConditions = returnConditions;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float lng) {
        this.longitude = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String toString() {
        return name;
    }

}
