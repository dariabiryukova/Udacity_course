package com.example.bivy.sunshine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by bivy on 14/10/14.
 */
public class WeatherDBHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "weather_db";
    private static SQLiteDatabase.CursorFactory factory;
    private static final int DATABASE_VERSION = 1;

    public WeatherDBHelper(Context context) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //Create a table to hold a location
        //location consist of post code and human recognise name

        final String SQL_CREATE_WEATHERTABLE = "CREATE TABLE " + WeatherContract.WeatherEntry.TABLE_NAME
            + "(" + WeatherContract.WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+

            WeatherContract.WeatherEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL, "+
            WeatherContract.WeatherEntry.COLUMN_DATETEXT + " TEXT NOT NULL, " +
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC + " TEXT NOT NULL, "+
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL, " +

            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP + " REAL NOT NULL, "+
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP + " REAL NOT NULL, "+

            WeatherContract.WeatherEntry.COLUMN_HUMIDITY + " REAL NOT NULL, "+
            WeatherContract.WeatherEntry.COLUMN_PRESSURE + " REAL NOT NULL, "+
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, "+
            WeatherContract.WeatherEntry.COLUMN_DEGREES + " REAL NOT NULL, " +

            //Set up location column as a foreign key for location table

            " FOREIGN KEY (" + WeatherContract.WeatherEntry.COLUMN_LOC_KEY + ") REFERENCES " +
            WeatherContract.LocationEntry.TABLE_NAME + " (" + WeatherContract.LocationEntry._ID+ "), " +

            " UNIQUE ( " + WeatherContract.WeatherEntry.COLUMN_DATETEXT + ", " +
            WeatherContract.WeatherEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE );";

        final String SQL_CreateTable_Location = " CREATE TABLE " + WeatherContract.LocationEntry.TABLE_NAME +
            "( " + WeatherContract.LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            WeatherContract.LocationEntry.COLUMN_CITY_NAME + " TEXT NOT NULL, " +
            WeatherContract.LocationEntry.COLUMN_LOCATION_LAT + " REAL NOT NULL, " +
            WeatherContract.LocationEntry.COLUMN_LOCATION_LONG + " REAL NOT NULL, " +
            WeatherContract.LocationEntry.COLUMN_LOCATIONSETTINGS + " TEXT UNIQUE NOT NULL, " +

            " UNIQUE ("+ WeatherContract.LocationEntry.COLUMN_LOCATIONSETTINGS+ ") ON CONFLICT IGNORE);";

        sqLiteDatabase.execSQL(SQL_CREATE_WEATHERTABLE);
        sqLiteDatabase.execSQL(SQL_CreateTable_Location);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + WeatherContract.WeatherEntry.TABLE_NAME);
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + WeatherContract.LocationEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
