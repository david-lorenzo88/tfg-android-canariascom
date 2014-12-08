package com.canarias.rentacar.model;


import com.canarias.rentacar.config.Config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Modelo que define un coche. Se anotan los atributos para
 * parsearlos desde el XML
 */
@Root
public class Car {
    private static HashMap<String, String> modelTranslations;

    static {
        modelTranslations = new HashMap<String, String>();
        modelTranslations.put("es", "Modelo");
        modelTranslations.put("en", "Model");
        modelTranslations.put("de", "Modell");
        modelTranslations.put("fr", "Model");
        modelTranslations.put("it", "Modello");
        modelTranslations.put("ru", "&#1052;&#1086;&#1076;&#1077;&#1083;&#1100;");

    }

    @Element(name = "code")
    private String code;
    @Element(name = "otherCodes")
    private String otherCodes;
    private String model;
    @Attribute(name = "Cat")
    private String category;
    @Attribute(name = "Sipp")
    private String sippCode;
    @Attribute(name = "Url")
    private String Url;
    @Element(name = "image")
    private String imageUrl;
    @Attribute(name = "Group")
    private String group;
    @ElementList(inline = true, entry = "attribute")
    private ArrayList<CarAttribute> attributes;

    public Car() {
        this.attributes = new ArrayList<CarAttribute>();
    }

    public Car(String code, String model) {
        this.code = code;
        this.model = model;
        this.attributes = new ArrayList<CarAttribute>();
    }

    public Car(String code, String model, String category, String group) {
        this.code = code;
        this.model = model;
        this.category = category;
        this.group = group;
        this.attributes = new ArrayList<CarAttribute>();
    }

    public String getOtherCodes() {
        return otherCodes;
    }

    public void setOtherCodes(String otherCodes) {
        this.otherCodes = otherCodes;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getModel() {
        if (model == null && attributes != null && attributes.size() > 0) {
            CarAttribute att = getAttribute(modelTranslations.get(
                    Config.getLanguageCode(Locale.getDefault().getLanguage())));
            if (att != null) {
                model = att.getValue();
            }
        }
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSippCode() {
        return sippCode;
    }

    public void setSippCode(String sippCode) {
        this.sippCode = sippCode;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public List<CarAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(ArrayList<CarAttribute> attributes) {
        this.attributes = attributes;
    }

    public CarAttribute getAttribute(String name) {
        Iterator<CarAttribute> it = attributes.iterator();
        while (it.hasNext()) {
            CarAttribute at = it.next();

            if (at.getName().equals(name))
                return at;
        }
        return null;
    }

    public void setAttribute(String name, String value) {
        Iterator<CarAttribute> it = attributes.iterator();
        boolean found = false;
        while (!found && it.hasNext()) {
            CarAttribute at = it.next();
            if (at.getName().equals(name)) {
                at.setValue(value);
                found = true;
            }
        }
        if (!found) {
            CarAttribute at = new CarAttribute(name, value);
            attributes.add(at);
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Car: \n");
        if (model != null)
            sb.append("\t- Model: " + getModel() + "\n");
        sb.append("\t- Code: " + getCode() + "\n");
        Iterator<CarAttribute> it = attributes.iterator();
        while (it.hasNext()) {
            CarAttribute at = it.next();
            sb.append("\t- " + at.getName() + ": " + at.getValue() + "\n");
        }


        return sb.toString();
    }


}
