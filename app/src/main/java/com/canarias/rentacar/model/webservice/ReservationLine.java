package com.canarias.rentacar.model.webservice;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by David on 03/09/2014.
 */
@Root(name = "line")
public class ReservationLine {

    @Element(name = "itemCode")
    private int itemCode;

    @Element(name = "status")
    private String status;

    @Element(name = "item")
    private String item;

    @Element(name = "amount")
    private String amount;

    @Element(name = "tax")
    private String tax;

    public int getItemCode() {
        return itemCode;
    }

    public void setItemCode(int itemCode) {
        this.itemCode = itemCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }
}
