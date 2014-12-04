package com.canarias.rentacar.items;

/**
 * Created by David on 05/11/2014.
 * Item usado para almacenar la información de cada
 * item del NavigationDrawer, el menú colapsable de la Home.
 */
public class DrawerItem {

    String ItemName;
    int imgResID;

    public DrawerItem(String itemName, int imgResID) {
        super();
        ItemName = itemName;
        this.imgResID = imgResID;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public int getImgResID() {
        return imgResID;
    }

    public void setImgResID(int imgResID) {
        this.imgResID = imgResID;
    }

}
