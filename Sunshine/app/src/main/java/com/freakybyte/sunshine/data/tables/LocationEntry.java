package com.freakybyte.sunshine.data.tables;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Jose Torres on 31/10/2016.
 */

public class LocationEntry extends WeatherContract implements BaseColumns{

    public static final Uri CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();
    public static final String CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

    public static final String TABLE_NAME = "location";

    // The location setting string is what will be sent to openweathermap
    // as the location query.
    public static final String COLUMN_LOCATION_SETTING = "location_setting";

    // Human readable location string, provided by the API.  Because for styling,
    // "Mountain View" is more recognizable than 94043.
    public static final String COLUMN_CITY_NAME = "city_name";

    // In order to uniquely pinpoint the location on the map when we launch the
    // map intent, we store the latitude and longitude as returned by openweathermap.
    public static final String COLUMN_COORD_LAT = "coord_lat";
    public static final String COLUMN_COORD_LONG = "coord_long";

    public static final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

            COLUMN_LOCATION_SETTING + " TEXT UNIQUE NOT NULL, " +
            COLUMN_CITY_NAME + "  TEXT NOT NULL, " +
            COLUMN_COORD_LAT + " REAL NOT NULL, " +
            COLUMN_COORD_LONG + " REAL NOT NULL " +
            " );";


    public static Uri buildLocationUri(long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }
}