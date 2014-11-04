package com.canarias.rentacar.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.canarias.rentacar.db.DBHelper;
import com.canarias.rentacar.model.Office;
import com.canarias.rentacar.model.Zone;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 02/09/2014.
 */
public class OfficeDataSource {
    private static final String TAG = "OfficeDataSource";
    // Database fields
    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String[] allColumns = {DBHelper.COLUMN_OFFICECODE,
            DBHelper.COLUMN_OFFICEFAX, DBHelper.COLUMN_OFFICELATITUDE,
            DBHelper.COLUMN_OFFICELONGITUDE, DBHelper.COLUMN_OFFICENAME,
            DBHelper.COLUMN_OFFICEPHONE, DBHelper.COLUMN_DELIVERYCONDITIONS,
            DBHelper.COLUMN_RETURNCONDITIONS, DBHelper.COLUMN_ZONE};
    private Context context;

    public OfficeDataSource(Context context) {
        dbHelper = new DBHelper(context);
        this.context = context;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insert(Office office) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_OFFICECODE, office.getCode());
        values.put(DBHelper.COLUMN_OFFICEFAX, office.getFax());
        values.put(DBHelper.COLUMN_OFFICELATITUDE, office.getLatitude());
        values.put(DBHelper.COLUMN_OFFICELONGITUDE, office.getLongitude());
        values.put(DBHelper.COLUMN_OFFICENAME, office.getName());
        values.put(DBHelper.COLUMN_OFFICEPHONE, office.getPhone());
        values.put(DBHelper.COLUMN_DELIVERYCONDITIONS, office.getDeliveryConditions());
        values.put(DBHelper.COLUMN_RETURNCONDITIONS, office.getReturnConditions());
        values.put(DBHelper.COLUMN_ZONE, office.getZone().getCode());

        Office current = getOffice(office.getCode());
        if (current == null) {
            //Insertamos
            return database.insert(DBHelper.TABLE_OFFICE, null,
                    values);
        } else {
            //Actualizamos
            values.remove(DBHelper.COLUMN_OFFICECODE);
            return database.update(DBHelper.TABLE_OFFICE, values,
                    DBHelper.COLUMN_OFFICECODE + " = " + office.getCode().toString(), null);
        }

    }

    public int delete(Office office) {
        String code = office.getCode();


        return database.delete(DBHelper.TABLE_OFFICE, DBHelper.COLUMN_OFFICECODE
                + " = ?", new String[]{code});
    }

    //Permitimos el paso de un parametro para sacar las oficinas de una zona
    //O si quieremos sacarlas todas pasamos 'null' como parámetro.
    public List<Office> getOffices(String zoneCode) {
        List<Office> offices = new ArrayList<Office>();

        Cursor cursor = database.query(DBHelper.TABLE_OFFICE,
                allColumns, (zoneCode == null ? null : DBHelper.COLUMN_ZONE + " = ?"),
                (zoneCode == null ? null : new String[]{zoneCode}), null, null, null);

        ZoneDataSource zoneDAO = null;
        try {
            zoneDAO = new ZoneDataSource(context);
            zoneDAO.open();
        } catch (SQLException ex) {
            Log.e(TAG, ex.getMessage());
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Office office = cursorToOffice(cursor);
            //Añadimos la oficina completa
            if (zoneDAO != null)
                office.setZone(zoneDAO.getZone(office.getZone().getCode()));

            offices.add(office);
            cursor.moveToNext();
        }

        if (zoneDAO != null) {
            zoneDAO.close();
            zoneDAO = null;
        }

        cursor.close();
        return offices;
    }

    public Office getOffice(String code) {
        Office office = null;

        Cursor cursor = database.query(DBHelper.TABLE_OFFICE,
                allColumns, DBHelper.COLUMN_OFFICECODE + " = ?",
                new String[]{code}, null, null, null);

        ZoneDataSource zoneDAO = null;
        try {
            zoneDAO = new ZoneDataSource(context);
            zoneDAO.open();
        } catch (SQLException ex) {
            Log.e(TAG, ex.getMessage());
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            office = cursorToOffice(cursor);
            //Añadimos la oficina completa
            if (zoneDAO != null)
                office.setZone(zoneDAO.getZone(office.getZone().getCode()));


            cursor.moveToNext();
        }

        if (zoneDAO != null) {
            zoneDAO.close();
            zoneDAO = null;
        }

        cursor.close();
        return office;
    }

    private Office cursorToOffice(Cursor cursor) {
        Office office = new Office();
        office.setCode(cursor.getString(0));
        office.setFax(cursor.getString(1));
        office.setLatitude(cursor.getFloat(2));
        office.setLongitude(cursor.getFloat(3));
        office.setName(cursor.getString(4));
        office.setPhone(cursor.getString(5));
        office.setDeliveryConditions(cursor.getString(6));
        office.setReturnConditions(cursor.getString(7));
        Zone zone = new Zone();
        zone.setCode(cursor.getInt(8));
        office.setZone(zone);
        return office;
    }
}
