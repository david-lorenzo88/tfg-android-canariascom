package com.canarias.rentacar.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by David on 11/08/2014.
 */
public class DBHelper extends SQLiteOpenHelper {
    //Table Car
    public static final String TABLE_CAR = "Car";
    public static final String COLUMN_MODEL = "model";
    public static final String COLUMN_GROUP = "car_group";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_IMAGEURL = "image_url";
    public static final String COLUMN_SIPPCODE = "sipp_code";

    private static final String TABLE_CAR_CREATE = "create table "
            + TABLE_CAR + "(" + COLUMN_MODEL + " text primary key, "
            + COLUMN_GROUP + " text not null,"
            + COLUMN_CATEGORY + " text not null,"
            + COLUMN_IMAGEURL + " text not null,"
            + COLUMN_SIPPCODE + " text"
            + ");";

    //Table CarAttribute
    public static final String TABLE_ATTRIBUTE = "CarAttribute";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_VALUE = "value";
    public static final String COLUMN_FILENAME = "filename";
    public static final String COLUMN_ATTRIBUTE_CAR = "car";//Car model

    private static final String TABLE_ATTRIBUTE_CREATE = "create table "
            + TABLE_ATTRIBUTE + "(" + COLUMN_NAME + " text not null, "
            + COLUMN_VALUE + " text not null,"
            + COLUMN_FILENAME + " text not null,"
            + COLUMN_ATTRIBUTE_CAR + " text not null,"
            + "PRIMARY KEY (" + COLUMN_ATTRIBUTE_CAR + "," + COLUMN_NAME + "),"
            + " FOREIGN KEY (" + COLUMN_ATTRIBUTE_CAR + ") REFERENCES " + TABLE_CAR + " (" + COLUMN_MODEL + ")"
            + ");";


    //Table Zone
    public static final String TABLE_ZONE = "Zone";
    public static final String COLUMN_ZONENAME = "name";
    public static final String COLUMN_ZONECODE = "code";

    private static final String TABLE_ZONE_CREATE = "create table "
            + TABLE_ZONE + "(" + COLUMN_ZONECODE + " integer primary key, "
            + COLUMN_ZONENAME + " text not null"
            + ");";

    //Table Office
    public static final String TABLE_OFFICE = "Office";
    public static final String COLUMN_OFFICECODE = "code";
    public static final String COLUMN_OFFICENAME = "name";
    public static final String COLUMN_OFFICELATITUDE = "latitude";
    public static final String COLUMN_OFFICELONGITUDE = "longitude";
    public static final String COLUMN_OFFICEPHONE = "phone";
    public static final String COLUMN_OFFICEFAX = "fax";
    public static final String COLUMN_DELIVERYCONDITIONS = "delivery_conditions";
    public static final String COLUMN_RETURNCONDITIONS = "return_conditions";
    public static final String COLUMN_ZONE = "zone";//Zone code

    private static final String TABLE_OFFICE_CREATE = "create table "
            + TABLE_OFFICE + "(" + COLUMN_OFFICECODE + " integer primary key, "
            + COLUMN_OFFICENAME + " text not null,"
            + COLUMN_OFFICELATITUDE + " real,"
            + COLUMN_OFFICELONGITUDE + " real,"
            + COLUMN_OFFICEPHONE + " integer,"
            + COLUMN_OFFICEFAX + " integer,"
            + COLUMN_DELIVERYCONDITIONS + " text,"
            + COLUMN_RETURNCONDITIONS + " text,"
            + COLUMN_ZONE + " integer not null,"
            + " FOREIGN KEY (" + COLUMN_ZONE + ") REFERENCES " + TABLE_ZONE + " (" + COLUMN_ZONECODE + ")"
            + ");";

    //Table Customer
    public static final String TABLE_CUSTOMER = "Customer";
    public static final String COLUMN_CUSTOMERNAME = "name";
    public static final String COLUMN_CUSTOMERSURNAME = "surname";
    public static final String COLUMN_CUSTOMEREMAIL = "email";
    public static final String COLUMN_CUSTOMERPHONE = "phone";
    public static final String COLUMN_CUSTOMERBIRTHDATE = "birth_date";
    public static final String COLUMN_CUSTOMERLANGUAGE = "language";
    public static final String COLUMN_CUSTOMERRESERVATIONCODE = "reservation_code";

    private static final String TABLE_CUSTOMER_CREATE = "create table "
            + TABLE_CUSTOMER + "(" + COLUMN_CUSTOMEREMAIL + " text not null, "
            + COLUMN_CUSTOMERNAME + " text not null,"
            + COLUMN_CUSTOMERSURNAME + " text not null,"
            + COLUMN_CUSTOMERPHONE + " text not null,"
            + COLUMN_CUSTOMERBIRTHDATE + " text not null,"
            + COLUMN_CUSTOMERLANGUAGE + " text not null,"
            + COLUMN_CUSTOMERRESERVATIONCODE + " text not null,"
            + "PRIMARY KEY (" + COLUMN_CUSTOMEREMAIL + "," + COLUMN_CUSTOMERRESERVATIONCODE + ")"
            + ");";

    //Table Reservation
    public static final String TABLE_RESERVATION = "Reservation";
    public static final String COLUMN_LOCALIZER = "localizer";
    public static final String COLUMN_AVAILABILITYIDENTIFIER = "availability_identifier";
    public static final String COLUMN_STARTDATE = "start_date";
    public static final String COLUMN_ENDDATE = "end_date";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_COMMENTS = "comments";
    public static final String COLUMN_FLIGHTNUMBER = "flight_number";
    public static final String COLUMN_CUSTOMER = "customer"; //Customer Email
    public static final String COLUMN_CAR = "car"; //Car Model
    public static final String COLUMN_DELIVERYOFFICE = "delivery_office"; //Office Code
    public static final String COLUMN_RETURNOFFICE = "return_office"; //Office Code
    public static final String COLUMN_STATE = "state";

    private static final String TABLE_RESERVATION_CREATE = "create table "
            + TABLE_RESERVATION + "(" + COLUMN_LOCALIZER + " text primary key, "
            + COLUMN_AVAILABILITYIDENTIFIER + " text not null,"
            + COLUMN_STARTDATE + " text not null,"
            + COLUMN_ENDDATE + " text not null,"
            + COLUMN_PRICE + " real not null,"
            + COLUMN_COMMENTS + " text,"
            + COLUMN_FLIGHTNUMBER + " text,"
            + COLUMN_CUSTOMER + " text not null,"
            + COLUMN_CAR + " text not null,"
            + COLUMN_DELIVERYOFFICE + " integer not null,"
            + COLUMN_RETURNOFFICE + " integer not null,"
            + COLUMN_STATE + " text not null,"
            + " FOREIGN KEY (" + COLUMN_CUSTOMER + ") REFERENCES " + TABLE_CUSTOMER + " (" + COLUMN_CUSTOMEREMAIL + "),"
            + " FOREIGN KEY (" + COLUMN_CAR + ") REFERENCES " + TABLE_CAR + " (" + COLUMN_MODEL + "),"
            + " FOREIGN KEY (" + COLUMN_DELIVERYOFFICE + ") REFERENCES " + TABLE_OFFICE + " (" + COLUMN_OFFICECODE + "),"
            + " FOREIGN KEY (" + COLUMN_RETURNOFFICE + ") REFERENCES " + TABLE_OFFICE + " (" + COLUMN_OFFICECODE + ")"
            + ");";


    //Table Extra
    public static final String TABLE_EXTRA = "Extra";
    public static final String COLUMN_EXTRACODE = "code";
    public static final String COLUMN_EXTRAMODELCODE = "model_code";
    public static final String COLUMN_EXTRANAME = "name";
    public static final String COLUMN_EXTRAQTY = "quantity";
    public static final String COLUMN_EXTRAPRICE = "price";
    public static final String COLUMN_EXTRAPRICETYPE = "price_type"; //DAILY or TOTAL
    public static final String COLUMN_RESERVATION = "reservation";//Reservation Localizer

    private static final String TABLE_EXTRA_CREATE = "create table "
            + TABLE_EXTRA + "(" + COLUMN_EXTRACODE + " integer not null, "
            + COLUMN_EXTRANAME + " text not null,"
            + COLUMN_EXTRAPRICE + " real not null,"
            + COLUMN_EXTRAQTY + " integer not null,"
            + COLUMN_EXTRAPRICETYPE + " text not null,"
            + COLUMN_RESERVATION + " text not null,"
            + COLUMN_EXTRAMODELCODE + " integer not null,"
            + "PRIMARY KEY (" + COLUMN_EXTRACODE + "," + COLUMN_RESERVATION + "),"
            + " FOREIGN KEY (" + COLUMN_RESERVATION + ") REFERENCES " + TABLE_RESERVATION + " (" + COLUMN_LOCALIZER + ")"
            + ");";
    // Database creation sql statement
    private static final String DATABASE_CREATE = TABLE_CAR_CREATE
            + TABLE_ATTRIBUTE_CREATE + TABLE_ZONE_CREATE
            + TABLE_OFFICE_CREATE + TABLE_CUSTOMER_CREATE
            + TABLE_RESERVATION_CREATE + TABLE_EXTRA_CREATE;
    private static final String DATABASE_NAME = "rentacarcanarias.db";
    private static final int DATABASE_VERSION = 6;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(TABLE_CAR_CREATE);
        db.execSQL(TABLE_ATTRIBUTE_CREATE);
        db.execSQL(TABLE_ZONE_CREATE);
        db.execSQL(TABLE_OFFICE_CREATE);
        db.execSQL(TABLE_CUSTOMER_CREATE);
        db.execSQL(TABLE_RESERVATION_CREATE);
        db.execSQL(TABLE_EXTRA_CREATE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data"
        );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTRIBUTE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ZONE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OFFICE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXTRA);
        onCreate(db);
    }
}
