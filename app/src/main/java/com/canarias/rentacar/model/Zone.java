package com.canarias.rentacar.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.util.HashMap;

@Root
public class Zone {

    @Attribute(name = "code")
    private int code;

    @Text
    private String name;


    private HashMap<String, Office> offices;

    public Zone() {
    }

    public Zone(String name) {
        this.name = name;

    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Office> getOffices() {
        return offices;
    }

    public void setOffices(HashMap<String, Office> offices) {
        this.offices = offices;
    }

    public void addOffice(Office office) {
        this.offices.put(office.getCode(), office);
    }

    public Office getOffice(String code) {
        return offices.get(code);
    }

    public String toString() {
        return name;

    }
}
