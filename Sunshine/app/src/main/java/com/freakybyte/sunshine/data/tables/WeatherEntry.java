package com.freakybyte.sunshine.data.tables;

import android.provider.BaseColumns;

/**
 * Created by Jose Torres on 31/10/2016.
 */

    /* Inner class that defines the table contents of the weather table */
public class WeatherEntry implements BaseColumns {

    public static final String TABLE_NAME = "weather";

    // Column with the foreign key into the location table.
    public static final String COLUMN_LOC_KEY = "location_id";
    // Date, stored as long in milliseconds since the epoch
    public static final String COLUMN_DATE = "date";
    // Weather id as returned by API, to identify the icon to be used
    public static final String COLUMN_WEATHER_ID = "weather_id";

    // Short description and long description of the weather, as provided by API.
    // e.g "clear" vs "sky is clear".
    public static final String COLUMN_SHORT_DESC = "short_desc";

    // Min and max temperatures for the day (stored as floats)
    public static final String COLUMN_MIN_TEMP = "min";
    public static final String COLUMN_MAX_TEMP = "max";

    // Humidity is stored as a float representing percentage
    public static final String COLUMN_HUMIDITY = "humidity";

    // Humidity is stored as a float representing percentage
    public static final String COLUMN_PRESSURE = "pressure";

    // Windspeed is stored as a float representing windspeed  mph
    public static final String COLUMN_WIND_SPEED = "wind";

    // Degrees are meteorological degrees (e.g, 0 is north, 180 is south).  Stored as floats.
    public static final String COLUMN_DEGREES = "degrees";

    public static final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            // Why AutoIncrement here, and not above?
            // Unique keys will be auto-generated in either case.  But for weather
            // forecasting, it's reasonable to assume the user will want information
            // for a certain date and all dates *following*, so the forecast data
            // should be sorted accordingly.
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

            // the ID of the location entry associated with this weather data
            COLUMN_LOC_KEY + " INTEGER NOT NULL, " +
            COLUMN_DATE + " INTEGER NOT NULL, " +
            COLUMN_SHORT_DESC + " TEXT NOT NULL, " +
            COLUMN_WEATHER_ID + " INTEGER NOT NULL," +

            COLUMN_MIN_TEMP + " REAL NOT NULL, " +
            COLUMN_MAX_TEMP + " REAL NOT NULL, " +

            COLUMN_HUMIDITY + " REAL NOT NULL, " +
            COLUMN_PRESSURE + " REAL NOT NULL, " +
            COLUMN_WIND_SPEED + " REAL NOT NULL, " +
            COLUMN_DEGREES + " REAL NOT NULL, " +

            // Set up the location column as a foreign key to location table.
            " FOREIGN KEY (" + COLUMN_LOC_KEY + ") REFERENCES " +
            LocationEntry.TABLE_NAME + " (" + LocationEntry._ID + "), " +

            // To assure the application have just one weather entry per day
            // per location, it's created a UNIQUE constraint with REPLACE strategy
            " UNIQUE (" + COLUMN_DATE + ", " +
            COLUMN_LOC_KEY + ") ON CONFLICT REPLACE);";
}

