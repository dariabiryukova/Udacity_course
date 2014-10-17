package com.example.bivy.sunshine;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

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

            int longIndex = cursor.getColumnIndex((WeatherContract.Location.COLUMN_LOCATIONSETTINGS));
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
    }

}
