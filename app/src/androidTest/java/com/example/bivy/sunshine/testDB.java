package com.example.bivy.sunshine;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.bivy.sunshine.data.WeatherContract;
import com.example.bivy.sunshine.data.WeatherDBHelper;

/**
 * Created by bivy on 17/10/14.
 */
public class testDB extends AndroidTestCase {

    private static String LOG_TAG = "";

    public void testCreateDB() throws Throwable {

        mContext.deleteDatabase(WeatherDBHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDBHelper(
            this.mContext).getWritableDatabase();
            assertEquals(true, db.isOpen());
            db.close();

    }

    public void testInsertReadDb() {

        // Test data we're going to insert into the DB to see if it works.
        String testLocationSetting = "99705";
        String testCityName = "North Pole";
        double testLatitude = 64.7488;
        double testLongitude = -147.353;

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        WeatherDBHelper dbHelper = new WeatherDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(WeatherContract.Location.COLUMN_LOCATIONSETTINGS, testLocationSetting);
        values.put(WeatherContract.Location.COLUMN_CITY_NAME, testCityName);
        values.put(WeatherContract.Location.COLUMN_LOCATION_LAT, testLatitude);
        values.put(WeatherContract.Location.COLUMN_LOCATION_LONG, testLongitude);

        long locationRowId;
        locationRowId = db.insert(WeatherContract.Location.TABLE_NAME, null, values);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATETEXT, "20141205");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, 75);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, 65);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, 321);

        //dbHelper.close();

        long weatherRowId;
        weatherRowId = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, weatherValues);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        assertTrue(weatherRowId != -1);
        Log.d(LOG_TAG, "New row id in weather table: " + weatherRowId);
//
        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Specify which columns you want.
        String[] columns = {
            WeatherContract.Location._ID,
            WeatherContract.Location.COLUMN_LOCATIONSETTINGS,
            WeatherContract.Location.COLUMN_CITY_NAME,
            WeatherContract.Location.COLUMN_LOCATION_LAT,
            WeatherContract.Location.COLUMN_LOCATION_LONG
        };

        String[] weatherColumns = {

        WeatherContract.WeatherEntry.COLUMN_LOC_KEY,
        WeatherContract.WeatherEntry.COLUMN_DATETEXT,
        WeatherContract.WeatherEntry.COLUMN_DEGREES,
        WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
        WeatherContract.WeatherEntry.COLUMN_PRESSURE,
        WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
        WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
        WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
        WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
        WeatherContract.WeatherEntry.COLUMN_WEATHER_ID };

        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
            WeatherContract.Location.TABLE_NAME,  // Table to Query
            columns,
            null, // Columns for the "where" clause
            null, // Values for the "where" clause
            null, // columns to group by
            null, // columns to filter by row groups
            null // sort order
        );


        // If possible, move to the first row of the query results.
        if (cursor.moveToFirst()) {
            // Get the value in each column by finding the appropriate column index.
            int locationIndex = cursor.getColumnIndex(WeatherContract.Location.COLUMN_LOCATIONSETTINGS);
            String location = cursor.getString(locationIndex);

            int nameIndex = cursor.getColumnIndex((WeatherContract.Location.COLUMN_CITY_NAME));
            String name = cursor.getString(nameIndex);

            int latIndex = cursor.getColumnIndex((WeatherContract.Location.COLUMN_LOCATION_LAT));
            double latitude = cursor.getDouble(latIndex);

            int longIndex = cursor.getColumnIndex((WeatherContract.Location.COLUMN_LOCATION_LONG));
            double longitude = cursor.getDouble(longIndex);

            // Hooray, data was returned!  Assert that it's the right data, and that the database
            // creation code is working as intended.
            // Then take a break.  We both know that wasn't easy.
            assertEquals(testCityName, name);
            assertEquals(testLocationSetting, location);
            assertEquals(testLatitude, latitude);
            assertEquals(testLongitude, longitude);

            // Fantastic.  Now that we have a location, add some weather!
        } else {
            // That's weird, it works on MY machine...
            fail("No values returned :(");
        }

        // A cursor is your primary interface to the query results.
        Cursor weatherCursor = db.query(
            WeatherContract.WeatherEntry.TABLE_NAME,  // Table to Query
            weatherColumns,
            null, // Columns for the "where" clause
            null, // Values for the "where" clause
            null, // columns to group by
            null, // columns to filter by row groups
            null // sort order
        );


        if (!weatherCursor.moveToFirst()) {
            fail("No weather data returned!");
        }

        assertEquals(weatherCursor.getInt(
            weatherCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_LOC_KEY)), locationRowId);
        assertEquals(weatherCursor.getString(
            weatherCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATETEXT)), "20141205");
        assertEquals(weatherCursor.getDouble(
            weatherCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DEGREES)), 1.1);
        assertEquals(weatherCursor.getDouble(
            weatherCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY)), 1.2);
        assertEquals(weatherCursor.getDouble(
            weatherCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE)), 1.3);
        assertEquals(weatherCursor.getInt(
            weatherCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP)), 75);
        assertEquals(weatherCursor.getInt(
            weatherCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP)), 65);
        assertEquals(weatherCursor.getString(
            weatherCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC)), "Asteroids");
        assertEquals(weatherCursor.getDouble(
            weatherCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED)), 5.5);
        assertEquals(weatherCursor.getInt(
            weatherCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID)), 321);

        weatherCursor.close();

    }

}
