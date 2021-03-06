package com.freakybyte.sunshine.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.CursorLoader;
import android.text.format.Time;
import android.util.Log;

import com.freakybyte.sunshine.R;
import com.freakybyte.sunshine.SunshineApplication;
import com.freakybyte.sunshine.data.tables.LocationEntry;
import com.freakybyte.sunshine.data.tables.WeatherEntry;
import com.freakybyte.sunshine.model.ListModel;
import com.freakybyte.sunshine.model.WeatherModel;
import com.freakybyte.sunshine.utils.NotificationUtils;
import com.freakybyte.sunshine.utils.Utils;

import java.util.Vector;

import static android.text.format.DateUtils.DAY_IN_MILLIS;
import static com.freakybyte.sunshine.utils.SunshineUtil.formatHighLows;

/**
 * Created by Jose Torres on 31/10/2016.
 */

public class WeatherDao {
    private static final String TAG = WeatherDao.class.getSimpleName();

    private static WeatherDao singleton;
    private Context mContext;


    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_LOCATION_SETTING = 5;
    public static final int COL_WEATHER_CONDITION_ID = 6;
    public static final int COL_COORD_LAT = 7;
    public static final int COL_COORD_LONG = 8;

    private static final String[] FORECAST_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherEntry.COLUMN_WEATHER_ID,
            LocationEntry.COLUMN_COORD_LAT,
            LocationEntry.COLUMN_COORD_LONG
    };

    private static final String[] DETAIL_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_DEGREES,
            WeatherEntry.COLUMN_WEATHER_ID,
            // This works because the WeatherProvider returns location data joined with
            // weather data, even though they're stored in two different tables.
            LocationEntry.COLUMN_LOCATION_SETTING
    };


    public static WeatherDao getInstance() {
        if (singleton == null)
            singleton = new WeatherDao();
        return singleton;
    }

    public static WeatherDao getInstance(Context context) {
        if (singleton == null)
            singleton = new WeatherDao(context);
        return singleton;
    }

    public WeatherDao() {
    }

    public WeatherDao(Context context) {
        this.mContext = context;
    }


    public void addWeatherList(String locationSetting, WeatherModel weather) {
        LocationDao mLocationDao = LocationDao.getInstance();
        long locationId = mLocationDao.addLocation(locationSetting, weather.getCity().getName(), weather.getCity().getCoord().getLat(), weather.getCity().getCoord().getLat());
        Time dayTime = new Time();
        dayTime.setToNow();

        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
        Vector<ContentValues> cVVector = new Vector<>(weather.getList().size());

        for (int i = 0; i < weather.getList().size(); i++) {
            long dateTime = dayTime.setJulianDay(julianStartDay + i);
            ListModel dayForecast = weather.getList().get(i);

            ContentValues weatherValues = new ContentValues();

            weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationId);
            weatherValues.put(WeatherEntry.COLUMN_DATE, dateTime);
            weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, dayForecast.getHumidity());
            weatherValues.put(WeatherEntry.COLUMN_PRESSURE, dayForecast.getPressure());
            weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, dayForecast.getSpeed());
            weatherValues.put(WeatherEntry.COLUMN_DEGREES, dayForecast.getDeg());
            weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, dayForecast.getTemp().getMax());
            weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, dayForecast.getTemp().getMin());
            weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, dayForecast.getWeather().get(0).getDescription());
            weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, dayForecast.getWeather().get(0).getId());

            cVVector.add(weatherValues);
        }

        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            getContext().getContentResolver().bulkInsert(WeatherEntry.CONTENT_URI, cvArray);

            getContext().getContentResolver().delete(WeatherEntry.CONTENT_URI,
                    WeatherEntry.COLUMN_DATE + " <= ?",
                    new String[]{Long.toString(dayTime.setJulianDay(julianStartDay - 1))});

            NotificationUtils.notifyWeather();
        }

        String sortOrder = WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());

        Cursor cur = getContext().getContentResolver().query(weatherForLocationUri,
                null, null, null, sortOrder);

        cVVector = new Vector<>(cur.getCount());
        if (cur.moveToFirst()) {
            do {
                ContentValues cv = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(cur, cv);
                cVVector.add(cv);
            } while (cur.moveToNext());
        }
        Log.d(TAG, "Days Inserted:: " + cVVector.size());
    }

    public CursorLoader getWeatherCursorWithStartDate(String locationSetting) {
        String sortOrder = WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());

        return new CursorLoader(getContext(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder);
    }

    public CursorLoader getWeatherDetailCursor(Uri uri) {
        return new CursorLoader(
                getContext(),
                uri,
                DETAIL_COLUMNS,
                null,
                null,
                null
        );
    }


    public String convertCursorRowToUXFormat(Cursor cursor) {
        String highAndLow = formatHighLows(
                cursor.getDouble(COL_WEATHER_MAX_TEMP),
                cursor.getDouble(COL_WEATHER_MIN_TEMP));

        return Utils.formatDate(cursor.getLong(COL_WEATHER_DATE)) +
                " - " + cursor.getString(COL_WEATHER_DESC) +
                " - " + highAndLow;
    }

    private Context getContext() {
        return SunshineApplication.getInstance() == null ? mContext : SunshineApplication.getInstance();
    }


}
