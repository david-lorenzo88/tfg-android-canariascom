package com.canarias.rentacar.model;

import com.canarias.rentacar.R;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.HashMap;
/**
 * Modelo que define un atributo de un coche. Se anotan los atributos para
 * parsearlos desde el XML
 */
@Root(name = "attribute")
public class CarAttribute {
    public static HashMap<String, Integer> attributeIcons;

    static {
        attributeIcons = new HashMap<String, Integer>();
        attributeIcons.put("seats_5", R.drawable.pict_pasajeros_05);
        attributeIcons.put("air_conditioning_si", R.drawable.pict_ac);
        attributeIcons.put("doors_5", R.drawable.pict_puertas_05);
        attributeIcons.put("music_si", R.drawable.pict_radio);
        attributeIcons.put("power_steering_si", R.drawable.pict_direccion);
        attributeIcons.put("electric_windows_si", R.drawable.pict_eleva);
        attributeIcons.put("airbags_2", R.drawable.pict_airbag);
        attributeIcons.put("airbags_4", R.drawable.pict_airbag);
        attributeIcons.put("airbags_6", R.drawable.pict_airbag);

    }

    @Element(name = "name")
    private String name;
    @Element(name = "value")
    private String value;
    @Attribute(name = "filename")
    private String filename;
    private String carModel;


    public CarAttribute() {

    }

    public CarAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return getName() + ": " + getValue();
    }

}
