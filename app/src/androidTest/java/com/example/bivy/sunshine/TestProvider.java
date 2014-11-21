package com.example.bivy.sunshine;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.bivy.sunshine.data.WeatherContract;
import com.example.bivy.sunshine.data.WeatherDBHelper;

import java.util.Map;
import java.util.Set;

import static com.example.bivy.sunshine.data.WeatherContract.*;

/**
 * Created by bivy on 17/10/14.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();
    static final String TEST_LOCATION = "99705";
    static final String TEST_DATE = "20141205";

    public void testDeleteDB() throws Throwable {
        mContext.deleteDatabase(WeatherDBHelper.DATABASE_NAME);
    }

    public void testGetType() {

        //content: //com.example.bivy.sunshine.app/weather/
        String type = mContext.getContentResolver().getType(WeatherEntry.CONTENT_URI);
        assertEquals(WeatherEntry.CONTENT_TYPE, type);

        //content: //com.example.bivy.sunshine.app/weather/94074
        String testLocation = "94074";
        type = mContext.getContentResolver().getType(
            WeatherEntry.buildWeatherLocation(testLocation));
        assertEquals(WeatherEntry.CONTENT_ITEM_TYPE, type);

        //content: //com.example.bivy.sunshine.app/weather/94074/20140612

        type = mContext.getContentResolver().getType(
            WeatherEntry.buildWeatherLocationWithDate(testLocation, TEST_DATE));
        assertEquals(WeatherEntry.CONTENT_ITEM_TYPE, type);

        //content: //com.example.bivy.sunshine.app/weather/94074/20140612
        type = mContext.getContentResolver().getType(
            WeatherEntry.buildWeatherLocationWithStartDate(testLocation, TEST_DATE));
        assertEquals(WeatherEntry.CONTENT_ITEM_TYPE, type);

        //content: //com.example.bivy.sunshine.app/location
        type = mContext.getContentResolver().getType(
            LocationEntry.CONTENT_URI);
        assertEquals(LocationEntry.CONTENT_TYPE, type);

        //content: //com.example.bivy.sunshine.app/location/1
        type = mContext.getContentResolver().getType(
            LocationEntry.buildLocationUri(1L));
        assertEquals(LocationEntry.CONTENT_ITEM_TYPE, type);

  }

    public void testInsertReadProvider() {


        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        WeatherDBHelper dbHelper = new WeatherDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestDB.createNorthPoleLocationValues();

        long locationRowId;
        locationRowId = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
            WeatherContract.LocationEntry.CONTENT_URI,

            null, // leaving "columns" null just returns all the columns.
            null, // cols for "where" clause
            null, // values for "where" clause
            null  // sort order
        );

        TestDB.validateCursor(cursor, testValues);
        String[] stringWhereClause = new String[] {"1"};

        // Now see if we can successfully query if we include the row id
        cursor = mContext.getContentResolver().query(
            WeatherContract.LocationEntry.buildLocationUri(locationRowId),
            null, // leaving "columns" null just returns all the columns.
            LocationEntry._ID, // cols for "where" clause
            stringWhereClause, // values for "where" clause
            null  // sort order
        );

        TestDB.validateCursor(cursor, testValues);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues weatherValues = TestDB.createWeatherValues(locationRowId);

        long weatherRowId = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, weatherValues);
        assertTrue(weatherRowId != -1);

        // A cursor is your primary interface to the query results.
        Cursor weatherCursor = mContext.getContentResolver().query(
            WeatherContract.WeatherEntry.CONTENT_URI,  // Table to Query
            null, // leaving "columns" null just returns all the columns.
            null, // cols for "where" clause
            null, // values for "where" clause
            null // columns to group by
        );

        TestDB.validateCursor(weatherCursor, weatherValues);


        // Add the location values in with the weather data so that we can make
        // sure that the join worked and we actually get all the values back
        addAllContentValues(weatherValues, testValues);

        // Get the joined Weather and Location data
        weatherCursor = mContext.getContentResolver().query(
            WeatherContract.WeatherEntry.buildWeatherLocation(TestDB.TEST_LOCATION),
            null, // leaving "columns" null just returns all the columns.
            null, // cols for "where" clause
            null, // values for "where" clause
            null  // sort order
        );
        TestDB.validateCursor(weatherCursor, weatherValues);

        // Get the joined Weather and Location data with a start date
        weatherCursor = mContext.getContentResolver().query(
            WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                TestDB.TEST_LOCATION, TestDB.TEST_DATE),
            null, // leaving "columns" null just returns all the columns.
            null, // cols for "where" clause
            null, // values for "where" clause
            null  // sort order
        );
        TestDB.validateCursor(weatherCursor, weatherValues);

        // Get the joined Weather data for a specific date
        weatherCursor = mContext.getContentResolver().query(
            WeatherContract.WeatherEntry.buildWeatherLocationWithDate(TestDB.TEST_LOCATION, TestDB.TEST_DATE),
            null,
            null,
            null,
            null
        );
        TestDB.validateCursor(weatherCursor, weatherValues);

        dbHelper.close();
    }

    // The target api annotation is needed for the call to keySet -- we wouldn't want
    // to use this in our app, but in a test it's fine to assume a higher target.
   // @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    void addAllContentValues(ContentValues destination, ContentValues source) {
        for (String key : source.keySet()) {
            destination.put(key, source.getAsString(key));
        }
    }
}
