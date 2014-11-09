package com.canarias.rentacar.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.canarias.rentacar.db.DBHelper;
import com.canarias.rentacar.model.Car;
import com.canarias.rentacar.model.Customer;
import com.canarias.rentacar.model.Office;
import com.canarias.rentacar.model.Price;
import com.canarias.rentacar.model.Reservation;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by David on 02/09/2014.
 */
public class ReservationDataSource {

    private static final String TAG = "ReservationDataSource";
    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String[] allColumns = {DBHelper.COLUMN_LOCALIZER,
            DBHelper.COLUMN_AVAILABILITYIDENTIFIER, DBHelper.COLUMN_STARTDATE,
            DBHelper.COLUMN_ENDDATE, DBHelper.COLUMN_PRICE,
            DBHelper.COLUMN_COMMENTS, DBHelper.COLUMN_FLIGHTNUMBER,
            DBHelper.COLUMN_CUSTOMER, DBHelper.COLUMN_CAR,
            DBHelper.COLUMN_DELIVERYOFFICE, DBHelper.COLUMN_RETURNOFFICE, DBHelper.COLUMN_STATE};
    private Context context;

    public ReservationDataSource(Context context) {
        dbHelper = new DBHelper(context);
        this.context = context;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }


    public long insert(Reservation res) {
        ContentValues values = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        values.put(DBHelper.COLUMN_LOCALIZER, res.getLocalizer());
        values.put(DBHelper.COLUMN_AVAILABILITYIDENTIFIER, res.getAvailabilityIdentifier());
        values.put(DBHelper.COLUMN_STARTDATE, sdf.format(res.getStartDate()));
        values.put(DBHelper.COLUMN_ENDDATE, sdf.format(res.getEndDate()));
        values.put(DBHelper.COLUMN_PRICE, res.getPrice().getAmount());
        values.put(DBHelper.COLUMN_COMMENTS, res.getComments());
        values.put(DBHelper.COLUMN_FLIGHTNUMBER, res.getFlightNumber());
        values.put(DBHelper.COLUMN_CUSTOMER, res.getCustomer().getEmail());
        values.put(DBHelper.COLUMN_CAR, res.getCar().getModel());
        values.put(DBHelper.COLUMN_DELIVERYOFFICE, res.getDeliveryOffice().getCode());
        values.put(DBHelper.COLUMN_RETURNOFFICE, res.getReturnOffice().getCode());
        values.put(DBHelper.COLUMN_STATE, res.getState());


        return database.insert(DBHelper.TABLE_RESERVATION, null,
                values);


    }

    public int update(Reservation res) {
        ContentValues values = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");


        values.put(DBHelper.COLUMN_AVAILABILITYIDENTIFIER, res.getAvailabilityIdentifier());
        values.put(DBHelper.COLUMN_STARTDATE, sdf.format(res.getStartDate()));
        values.put(DBHelper.COLUMN_ENDDATE, sdf.format(res.getEndDate()));
        values.put(DBHelper.COLUMN_PRICE, res.getPrice().getAmount());
        values.put(DBHelper.COLUMN_COMMENTS, res.getComments());
        values.put(DBHelper.COLUMN_FLIGHTNUMBER, res.getFlightNumber());
        values.put(DBHelper.COLUMN_CUSTOMER, res.getCustomer().getEmail());
        values.put(DBHelper.COLUMN_CAR, res.getCar().getModel());
        values.put(DBHelper.COLUMN_DELIVERYOFFICE, res.getDeliveryOffice().getCode());
        values.put(DBHelper.COLUMN_RETURNOFFICE, res.getReturnOffice().getCode());
        values.put(DBHelper.COLUMN_STATE, res.getState());


        return database.update(DBHelper.TABLE_RESERVATION, values,
                DBHelper.COLUMN_LOCALIZER + " = ?", new String[]{res.getLocalizer()});


    }

    public int delete(Reservation res) {
        String locata = res.getLocalizer();


        return database.delete(DBHelper.TABLE_RESERVATION, DBHelper.COLUMN_LOCALIZER
                + " = ?", new String[]{locata});
    }

    public List<Reservation> getReservations() {
        List<Reservation> reservations = new ArrayList<Reservation>();

        Cursor cursor = database.query(DBHelper.TABLE_RESERVATION,
                allColumns, null, null, null, null, null);

        OfficeDataSource officeDAO = null;
        try {
            officeDAO = new OfficeDataSource(context);
            officeDAO.open();
        } catch (SQLException ex) {
            Log.e(TAG, ex.getMessage());
        }
        CarDataSource carDAO = null;
        try {
            carDAO = new CarDataSource(context);
            carDAO.open();
        } catch (SQLException ex) {
            Log.e(TAG, ex.getMessage());
        }
        CustomerDataSource customerDAO = null;
        try {
            customerDAO = new CustomerDataSource(context);
            customerDAO.open();
        } catch (SQLException ex) {
            Log.e(TAG, ex.getMessage());
        }

        ExtraDataSource extrasDAO = null;
        try {
            extrasDAO = new ExtraDataSource(context);
            extrasDAO.open();
        } catch (SQLException ex) {
            Log.e(TAG, ex.getMessage());
        }


        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Reservation r = cursorToReservation(cursor);

            if (officeDAO != null) {
                r.setDeliveryOffice(officeDAO.getOffice(r.getDeliveryOffice().getCode()));
                r.setReturnOffice(officeDAO.getOffice(r.getReturnOffice().getCode()));
            }
            if (customerDAO != null)
                r.setCustomer(customerDAO.getCustomer(r.getCustomer().getEmail(), r.getLocalizer()));

            if (carDAO != null)
                r.setCar(carDAO.getCar(r.getCar().getModel()));

            if (extrasDAO != null)
                r.setExtras(extrasDAO.getReservationExtras(r.getLocalizer()));

            reservations.add(r);
            cursor.moveToNext();
        }

        if (officeDAO != null) {
            officeDAO.close();
            officeDAO = null;
        }

        if (customerDAO != null) {
            customerDAO.close();
            customerDAO = null;
        }

        if (carDAO != null) {
            carDAO.close();
            carDAO = null;
        }

        if (extrasDAO != null) {
            extrasDAO.close();
            extrasDAO = null;
        }


        cursor.close();
        return reservations;
    }

    private Reservation cursorToReservation(Cursor cursor) {
        Reservation res = new Reservation();
        res.setLocalizer(cursor.getString(0));
        res.setAvailabilityIdentifier(cursor.getString(1));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(cursor.getString(2));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        res.setStartDate(convertedDate);

        convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(cursor.getString(3));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        res.setEndDate(convertedDate);

        res.setPrice(new Price(cursor.getFloat(4), "EUR"));

        res.setComments(cursor.getString(5));
        res.setFlightNumber(cursor.getString(6));

        Customer c = new Customer();
        c.setEmail(cursor.getString(7));
        res.setCustomer(c);

        Car car = new Car();
        car.setModel(cursor.getString(8));
        res.setCar(car);

        Office deo = new Office();
        deo.setCode(cursor.getString(9));
        res.setDeliveryOffice(deo);

        Office reo = new Office();
        reo.setCode(cursor.getString(10));
        res.setReturnOffice(reo);

        res.setState(cursor.getString(11));

        return res;
    }

    public int updateReservationStatus(String resId, String newStatus) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_STATE, newStatus);
        return database.update(DBHelper.TABLE_RESERVATION,
                values, DBHelper.COLUMN_LOCALIZER + " = ?", new String[]{resId});
    }

    public Reservation getReservation(String localizer) {
        Reservation r = null;

        Cursor cursor = database.query(DBHelper.TABLE_RESERVATION,
                allColumns, DBHelper.COLUMN_LOCALIZER + " = ?", new String[]{localizer},
                null, null, null);

        OfficeDataSource officeDAO = null;
        try {
            officeDAO = new OfficeDataSource(context);
            officeDAO.open();
        } catch (SQLException ex) {
            Log.e(TAG, ex.getMessage());
        }
        CarDataSource carDAO = null;
        try {
            carDAO = new CarDataSource(context);
            carDAO.open();
        } catch (SQLException ex) {
            Log.e(TAG, ex.getMessage());
        }
        CustomerDataSource customerDAO = null;
        try {
            customerDAO = new CustomerDataSource(context);
            customerDAO.open();
        } catch (SQLException ex) {
            Log.e(TAG, ex.getMessage());
        }

        ExtraDataSource extrasDAO = null;
        try {
            extrasDAO = new ExtraDataSource(context);
            extrasDAO.open();
        } catch (SQLException ex) {
            Log.e(TAG, ex.getMessage());
        }


        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            r = cursorToReservation(cursor);
            if (officeDAO != null) {
                r.setDeliveryOffice(officeDAO.getOffice(r.getDeliveryOffice().getCode()));
                r.setReturnOffice(officeDAO.getOffice(r.getReturnOffice().getCode()));
            }
            if (customerDAO != null)
                r.setCustomer(customerDAO.getCustomer(r.getCustomer().getEmail(), r.getLocalizer()));

            if (carDAO != null)
                r.setCar(carDAO.getCar(r.getCar().getModel()));

            if (extrasDAO != null)
                r.setExtras(extrasDAO.getReservationExtras(r.getLocalizer()));
            cursor.moveToNext();
        }

        if (officeDAO != null) {
            officeDAO.close();
            officeDAO = null;
        }

        if (customerDAO != null) {
            customerDAO.close();
            customerDAO = null;
        }

        if (carDAO != null) {
            carDAO.close();
            carDAO = null;
        }

        if (extrasDAO != null) {
            extrasDAO.close();
            extrasDAO = null;
        }

        cursor.close();
        return r;

    }
}
