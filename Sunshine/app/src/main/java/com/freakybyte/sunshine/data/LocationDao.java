package com.freakybyte.sunshine.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.freakybyte.sunshine.SunshineApplication;
import com.freakybyte.sunshine.data.tables.LocationEntry;

/**
 * Created by Jose Torres on 08/11/2016.
 */

public class LocationDao {
    private static final String TAG = LocationDao.class.getSimpleName();

    private static LocationDao singleton;

    private Context mContext;

    public static LocationDao getInstance() {
        if (singleton == null)
            singleton = new LocationDao();
        return singleton;
    }

    public static LocationDao getInstance(Context context) {
        if (singleton == null)
            singleton = new LocationDao(context);
        return singleton;
    }

    public LocationDao() {
    }

    public LocationDao(Context context) {
        this.mContext = context;
    }

    /**
     * Helper method to handle insertion of a new location in the weather database.
     *
     * @param locationSetting The location string used to request updates from the server.
     * @param cityName        A human-readable city name, e.g "Mountain View"
     * @param lat             the latitude of the city
     * @param lon             the longitude of the city
     * @return the row ID of the added location.
     */
    public long addLocation(String locationSetting, String cityName, double lat, double lon) {

        long locationId;

        // First, check if the location with this city name exists in the db
        Cursor locationCursor = getContext().getContentResolver().query(
                LocationEntry.CONTENT_URI,
                new String[]{LocationEntry._ID},
                LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
                new String[]{locationSetting},
                null);


        if (locationCursor.moveToFirst()) {
            int locationIdIndex = locationCursor.getColumnIndex(LocationEntry._ID);
            locationId = locationCursor.getLong(locationIdIndex);
        } else {
            // Now that the content provider is set up, inserting rows of data is pretty simple.
            // First create a ContentValues object to hold the data you want to insert.
            ContentValues locationValues = new ContentValues();

            // Then add the data, along with the corresponding name of the data type,
            // so the content provider knows what kind of value is being inserted.
            locationValues.put(LocationEntry.COLUMN_CITY_NAME, cityName);
            locationValues.put(LocationEntry.COLUMN_LOCATION_SETTING, locationSetting);
            locationValues.put(LocationEntry.COLUMN_COORD_LAT, lat);
            locationValues.put(LocationEntry.COLUMN_COORD_LONG, lon);

            // Finally, insert location data into the database.
            Uri insertedUri = getContext().getContentResolver().insert(
                    LocationEntry.CONTENT_URI,
                    locationValues);

            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            locationId = ContentUris.parseId(insertedUri);
        }

        locationCursor.close();
        return locationId;
    }

    private Context getContext() {
        return SunshineApplication.getInstance() == null ? mContext : SunshineApplication.getInstance();
    }

}