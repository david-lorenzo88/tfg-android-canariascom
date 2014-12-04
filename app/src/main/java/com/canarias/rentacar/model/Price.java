package com.canarias.rentacar.model;
/**
 * Modelo que define un precio.
 */
public class Price {
    private float amount;
    private String unit;


    public Price(float amount, String unit) {
        this.amount = amount;
        this.unit = unit;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }


    public String toString() {
        return amount + " " + unit;
    }

}
