package com.canarias.rentacar.model.webservice;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by David on 03/09/2014.
 */
@Root
public class ErrorResponse extends Response {
    @Element
    private int code;
    @Element
    private String description;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
