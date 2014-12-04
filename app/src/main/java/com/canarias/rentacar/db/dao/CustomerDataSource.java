package com.canarias.rentacar.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.canarias.rentacar.db.DBHelper;
import com.canarias.rentacar.model.Customer;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by David on 11/10/2014.
 * DataSource para gestionar la persistencia de los datos de los clientes
 */
public class CustomerDataSource {

    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String[] allColumns = {DBHelper.COLUMN_CUSTOMEREMAIL,
            DBHelper.COLUMN_CUSTOMERBIRTHDATE, DBHelper.COLUMN_CUSTOMERLANGUAGE,
            DBHelper.COLUMN_CUSTOMERNAME, DBHelper.COLUMN_CUSTOMERPHONE,
            DBHelper.COLUMN_CUSTOMERSURNAME, DBHelper.COLUMN_CUSTOMERRESERVATIONCODE};

    public CustomerDataSource(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * Inserta un cliente
     * @param cust el cliente
     * @param reservationCode el codigo de reserva asociado al cliente
     * @return el resultado de la inserción
     */
    public long insert(Customer cust, String reservationCode) {
        ContentValues values = new ContentValues();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        values.put(DBHelper.COLUMN_CUSTOMERBIRTHDATE, sdf.format(cust.getBirthDate()));
        values.put(DBHelper.COLUMN_CUSTOMEREMAIL, cust.getEmail());
        values.put(DBHelper.COLUMN_CUSTOMERLANGUAGE, cust.getLanguage());
        values.put(DBHelper.COLUMN_CUSTOMERNAME, cust.getName());
        values.put(DBHelper.COLUMN_CUSTOMERPHONE, cust.getPhone());
        values.put(DBHelper.COLUMN_CUSTOMERSURNAME, cust.getSurname());
        values.put(DBHelper.COLUMN_CUSTOMERRESERVATIONCODE, reservationCode);

        return database.insert(DBHelper.TABLE_CUSTOMER, null,
                values);


    }

    /**
     * Actualiza los datos de un cliente
     * @param cust el cliente
     * @param reservationCode el codigo de reserva asociado al cliente
     * @return el resultado de la actualización
     */
    public int update(Customer cust, String reservationCode){
        ContentValues values = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        values.put(DBHelper.COLUMN_CUSTOMERBIRTHDATE, sdf.format(cust.getBirthDate()));
        values.put(DBHelper.COLUMN_CUSTOMERLANGUAGE, cust.getLanguage());
        values.put(DBHelper.COLUMN_CUSTOMERNAME, cust.getName());
        values.put(DBHelper.COLUMN_CUSTOMERPHONE, cust.getPhone());
        values.put(DBHelper.COLUMN_CUSTOMERSURNAME, cust.getSurname());


        return database.update(DBHelper.TABLE_CUSTOMER,
                values,
                DBHelper.COLUMN_CUSTOMEREMAIL + " = ? and " +
                        DBHelper.COLUMN_CUSTOMERRESERVATIONCODE + " = ?",
                new String[]{cust.getEmail(), reservationCode});
    }

    /**
     * Elimina un cliente
     * @param cust el cliente
     * @return el resultado de la eliminación
     */
    public int delete(Customer cust) {
        String email = cust.getEmail();


        return database.delete(DBHelper.TABLE_CUSTOMER, DBHelper.COLUMN_CUSTOMEREMAIL
                + " = ?", new String[]{email});
    }

    /**
     * Obtiene un cliente a partir de su email y el código de la reserva
     * @param email el email
     * @param reservationCode el codigo de reserva asociado al cliente
     * @return el cliente
     */
    public Customer getCustomer(String email, String reservationCode) {
        Customer cust = null;

        Cursor cursor = database.query(DBHelper.TABLE_CUSTOMER,
                allColumns, DBHelper.COLUMN_CUSTOMEREMAIL + " = ? AND " + DBHelper.COLUMN_CUSTOMERRESERVATIONCODE + " = ?",
                new String[]{email, reservationCode}, null, null, null);


        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            cust = cursorToCustomer(cursor);
            cursor.moveToNext();
        }


        cursor.close();
        return cust;
    }

    /**
     * Convierte un cursor en un cliente
     * @param cursor el cursor
     * @return el cliente
     */
    private Customer cursorToCustomer(Cursor cursor) {
        Customer cust = new Customer();
        cust.setEmail(cursor.getString(0));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(cursor.getString(1));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cust.setBirthDate(convertedDate);

        cust.setLanguage(cursor.getString((2)));
        cust.setName(cursor.getString((3)));
        cust.setPhone(cursor.getString((4)));
        cust.setSurname(cursor.getString((5)));


        return cust;
    }
}
