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
 * Created by David on 11/10/2014.
 * DataSource para gestionar la persistencia de las oficinas
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
            DBHelper.COLUMN_RETURNCONDITIONS, DBHelper.COLUMN_ZONE, DBHelper.COLUMN_OFFICEADDRESS};
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

    /**
     * Inserta una oficina
     * @param office la oficina
     * @return el resultado de la inserción
     */
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
        values.put(DBHelper.COLUMN_OFFICEADDRESS, office.getAddress());

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

    /**
     * Elimina una oficina
     * @param office la oficina
     * @return el resultado del borrado
     */
    public int delete(Office office) {
        String code = office.getCode();


        return database.delete(DBHelper.TABLE_OFFICE, DBHelper.COLUMN_OFFICECODE
                + " = ?", new String[]{code});
    }

    /**
     * Obtiene las oficinas
     * Permitimos el paso de un parametro para sacar las oficinas de una zona
     * O si quieremos sacarlas todas pasamos 'null' como parámetro.
     * @param zoneCode el codigo de zona
     * @return el listado de oficinas
     */
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

        }

        cursor.close();
        return offices;
    }

    /**
     * Obtiene una oficina a partir de su codigo
     * @param code el codigo de la oficina
     * @return la oficina
     */
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

        }

        cursor.close();
        return office;
    }

    /**
     * Convierte un cursor en una oficina
     * @param cursor el cursor
     * @return la oficina
     */
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
        office.setAddress(cursor.getString(9));
        return office;
    }
}
