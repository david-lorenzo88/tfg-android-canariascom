package com.canarias.rentacar.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.canarias.rentacar.db.DBHelper;
import com.canarias.rentacar.model.Car;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 11/08/2014.
 */
public class CarDataSource {

    // Database fields
    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String[] allColumns = {DBHelper.COLUMN_MODEL,
            DBHelper.COLUMN_GROUP, DBHelper.COLUMN_CATEGORY,
            DBHelper.COLUMN_IMAGEURL, DBHelper.COLUMN_SIPPCODE};

    public CarDataSource(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insert(Car car) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_MODEL, car.getModel());
        values.put(DBHelper.COLUMN_GROUP, car.getGroup());
        values.put(DBHelper.COLUMN_CATEGORY, car.getCategory());
        values.put(DBHelper.COLUMN_IMAGEURL, car.getImageUrl());
        values.put(DBHelper.COLUMN_SIPPCODE, car.getSippCode());

        Car current = getCar(car.getModel());

        if (current == null) {
            return database.insert(DBHelper.TABLE_CAR, null,
                    values);
        } else {
            values.remove(DBHelper.COLUMN_MODEL);
            return database.update(DBHelper.TABLE_CAR, values,
                    DBHelper.COLUMN_MODEL + " = ?", new String[]{car.getModel()});
        }


    }

    public int delete(Car car) {
        String id = car.getModel();

        return database.delete(DBHelper.TABLE_CAR, DBHelper.COLUMN_MODEL
                + " = ?", new String[]{id});
    }


    public List<String> getCarCategories() {
        List<String> categories = new ArrayList<String>();

        Cursor cursor = database.query(true, DBHelper.TABLE_CAR,
                new String[]{DBHelper.COLUMN_CATEGORY}, null, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            categories.add(cursor.getString(0));
            cursor.moveToNext();
        }

        cursor.close();
        return categories;
    }


    public List<Car> getAllCars() {
        List<Car> cars = new ArrayList<Car>();

        Cursor cursor = database.query(DBHelper.TABLE_CAR,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Car car = cursorToCar(cursor);
            cars.add(car);
            cursor.moveToNext();
        }

        cursor.close();
        return cars;
    }

    public Car getCar(String model) {
        Car car = null;

        Cursor cursor = database.query(DBHelper.TABLE_CAR,
                allColumns, DBHelper.COLUMN_MODEL + " = ?", new String[]{model}, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            car = cursorToCar(cursor);

            cursor.moveToNext();
        }

        cursor.close();
        return car;
    }

    public List<Car> getCarsByCategory(String category) {
        List<Car> cars = new ArrayList<Car>();

        String query = category == null ? null : DBHelper.COLUMN_CATEGORY + " = ?";
        String[] params = category == null ? null : new String[]{category};

        Cursor cursor = database.query(DBHelper.TABLE_CAR,
                allColumns, query, params, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            cars.add(cursorToCar(cursor));

            cursor.moveToNext();
        }

        cursor.close();
        return cars;
    }

    private Car cursorToCar(Cursor cursor) {
        Car car = new Car();
        car.setModel(cursor.getString(0));
        car.setGroup(cursor.getString(1));
        car.setCategory(cursor.getString(2));
        car.setImageUrl(cursor.getString(3));
        car.setSippCode(cursor.getString(4));
        return car;
    }


}
