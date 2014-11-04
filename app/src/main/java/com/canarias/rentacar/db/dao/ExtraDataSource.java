package com.canarias.rentacar.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.canarias.rentacar.db.DBHelper;
import com.canarias.rentacar.model.Extra;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 02/09/2014.
 */
public class ExtraDataSource {

    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String[] allColumns = {DBHelper.COLUMN_EXTRACODE,
            DBHelper.COLUMN_EXTRANAME, DBHelper.COLUMN_EXTRAPRICE, DBHelper.COLUMN_EXTRAQTY,
            DBHelper.COLUMN_EXTRAPRICETYPE, DBHelper.COLUMN_RESERVATION,
            DBHelper.COLUMN_EXTRAMODELCODE};

    public ExtraDataSource(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insert(Extra extra) {
        ContentValues values = new ContentValues();

        values.put(DBHelper.COLUMN_EXTRACODE, extra.getCode());
        values.put(DBHelper.COLUMN_EXTRANAME, extra.getName());
        values.put(DBHelper.COLUMN_EXTRAPRICE, extra.getPrice());
        values.put(DBHelper.COLUMN_EXTRAQTY, extra.getQuantity());
        values.put(DBHelper.COLUMN_EXTRAPRICETYPE, extra.getPriceType().toString());
        values.put(DBHelper.COLUMN_RESERVATION, extra.getReservationCode());
        values.put(DBHelper.COLUMN_EXTRAMODELCODE, extra.getModelCode());

        return database.insert(DBHelper.TABLE_EXTRA, null,
                values);
    }

    public int delete(Extra extra) {
        return database.delete(DBHelper.TABLE_EXTRA, DBHelper.COLUMN_EXTRACODE
                        + " = " + extra.getCode() + " AND " + DBHelper.COLUMN_RESERVATION + " = ?",
                new String[]{extra.getReservationCode()});
    }

    public List<Extra> getReservationExtras(String localizer) {
        List<Extra> extras = new ArrayList<Extra>();

        Cursor cursor = database.query(DBHelper.TABLE_EXTRA,
                allColumns, DBHelper.COLUMN_RESERVATION + " = ?", new String[]{localizer},
                null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Extra e = cursorToExtra(cursor);
            extras.add(e);
            cursor.moveToNext();
        }

        cursor.close();
        return extras;
    }

    private Extra cursorToExtra(Cursor cursor) {
        Extra e = new Extra();
        e.setCode(cursor.getInt(0));
        e.setName(cursor.getString(1));
        e.setPrice(cursor.getFloat(2));
        e.setQuantity(cursor.getInt(3));
        e.setPriceType(Extra.PriceType.valueOf(cursor.getString(4)));
        e.setReservationCode(cursor.getString(5));
        e.setModelCode(cursor.getInt(6));

        return e;
    }
}
