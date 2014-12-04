package com.canarias.rentacar.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.canarias.rentacar.db.DBHelper;
import com.canarias.rentacar.model.Zone;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 11/10/2014.
 * DataSource para gestionar la persistencia de Zonas
 */
public class ZoneDataSource {

    // Database fields
    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String[] allColumns = {DBHelper.COLUMN_ZONECODE,
            DBHelper.COLUMN_ZONENAME};

    public ZoneDataSource(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * Inserta una zona
     * @param zone la zona
     * @return el resultado de la inserción
     */
    public long insert(Zone zone) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_ZONENAME, zone.getName());
        values.put(DBHelper.COLUMN_ZONECODE, zone.getCode());

        Zone current = getZone(zone.getCode());

        if (current == null) {

            return database.insert(DBHelper.TABLE_ZONE, null,
                    values);
        } else {
            values.remove(DBHelper.COLUMN_ZONECODE);
            return database.update(DBHelper.TABLE_ZONE, values, DBHelper.COLUMN_ZONECODE + " = " + zone.getCode(), null);

        }


    }

    /**
     * Elimina una zona
     * @param zone la zona
     * @return el resultado del borrado
     */
    public int delete(Zone zone) {
        int code = zone.getCode();


        return database.delete(DBHelper.TABLE_ZONE, DBHelper.COLUMN_ZONECODE
                + " = " + code, null);
    }

    /**
     * Obtiene las zonas disponibles
     * @return el listado de zonas
     */
    public List<Zone> getZones() {
        List<Zone> zones = new ArrayList<Zone>();

        Cursor cursor = database.query(DBHelper.TABLE_ZONE,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Zone zone = cursorToZone(cursor);
            zones.add(zone);
            cursor.moveToNext();
        }

        cursor.close();
        return zones;
    }

    /**
     * Obtiene una zona a partir de su codigo
     * @param code el codigo de la zona
     * @return la zona
     */
    public Zone getZone(int code) {

        Zone zone = null;

        Cursor cursor = database.query(DBHelper.TABLE_ZONE,
                allColumns, DBHelper.COLUMN_ZONECODE + " = " + code, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            zone = cursorToZone(cursor);

            cursor.moveToNext();
        }

        cursor.close();
        return zone;
    }

    /**
     * Convierte un cursor en una zona
     * @param cursor el cursor
     * @return la zona
     */
    private Zone cursorToZone(Cursor cursor) {
        Zone zone = new Zone();
        zone.setCode(cursor.getInt(0));
        zone.setName(cursor.getString(1));
        return zone;
    }


}

