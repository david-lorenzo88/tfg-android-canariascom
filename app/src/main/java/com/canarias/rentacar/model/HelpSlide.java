package com.canarias.rentacar.model;

/**
 * Created by David on 11/11/2014.
 */
public class HelpSlide {

    private String title;
    private int imageDrawableId;
    private String description;
    private String titleAction;
    private String subtitle;
    private String identifier;

    public HelpSlide(){}

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getTitleAction() {
        return titleAction;
    }

    public void setTitleAction(String titleAction) {
        this.titleAction = titleAction;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageDrawableId() {
        return imageDrawableId;
    }

    public void setImageDrawableId(int imageDrawableId) {
        this.imageDrawableId = imageDrawableId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
