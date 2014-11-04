package com.canarias.rentacar.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.canarias.rentacar.db.DBHelper;
import com.canarias.rentacar.model.CarAttribute;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 11/08/2014.
 */
public class AttributeDataSource {

    // Database fields
    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String[] allColumns = {DBHelper.COLUMN_NAME,
            DBHelper.COLUMN_VALUE, DBHelper.COLUMN_FILENAME, DBHelper.COLUMN_ATTRIBUTE_CAR};

    public AttributeDataSource(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insert(CarAttribute att) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NAME, att.getName());
        values.put(DBHelper.COLUMN_VALUE, att.getValue());
        values.put(DBHelper.COLUMN_FILENAME, att.getFilename());
        values.put(DBHelper.COLUMN_ATTRIBUTE_CAR, att.getCarModel());


        CarAttribute current = getAttribute(att.getName(), att.getCarModel());

        if (current == null) {
            return database.insert(DBHelper.TABLE_ATTRIBUTE, null,
                    values);
        } else {
            values.remove(DBHelper.COLUMN_NAME);
            values.remove(DBHelper.COLUMN_ATTRIBUTE_CAR);
            return database.update(DBHelper.TABLE_ATTRIBUTE, values,
                    DBHelper.COLUMN_ATTRIBUTE_CAR + " = ? AND " +
                            DBHelper.COLUMN_NAME + " = ?",
                    new String[]{att.getCarModel(), att.getName()});
        }


    }

    public int delete(CarAttribute att) {
        String carModel = att.getCarModel();
        String name = att.getName();

        return database.delete(DBHelper.TABLE_ATTRIBUTE, DBHelper.COLUMN_NAME
                        + " = ? AND " + DBHelper.COLUMN_ATTRIBUTE_CAR + " = ?",
                new String[]{name, carModel});
    }

    public CarAttribute getAttribute(String name, String carModel) {
        CarAttribute att = null;

        Cursor cursor = database.query(DBHelper.TABLE_ATTRIBUTE,
                allColumns, DBHelper.COLUMN_ATTRIBUTE_CAR + " = ? AND " + DBHelper.COLUMN_NAME + " = ?", new String[]{carModel, name}, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            att = cursorToAttribute(cursor);
            cursor.moveToNext();
        }

        cursor.close();
        return att;
    }

    public List<CarAttribute> getCarAttributes(String carModel) {
        List<CarAttribute> atts = new ArrayList<CarAttribute>();

        Cursor cursor = database.query(DBHelper.TABLE_ATTRIBUTE,
                allColumns, DBHelper.COLUMN_ATTRIBUTE_CAR + " = ?", new String[]{carModel}, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CarAttribute att = cursorToAttribute(cursor);
            atts.add(att);
            cursor.moveToNext();
        }

        cursor.close();
        return atts;
    }

    private CarAttribute cursorToAttribute(Cursor cursor) {
        CarAttribute att = new CarAttribute();
        att.setName(cursor.getString(0));
        att.setValue(cursor.getString(1));
        att.setFilename(cursor.getString(2));
        att.setCarModel(cursor.getString(3));
        return att;
    }


}

