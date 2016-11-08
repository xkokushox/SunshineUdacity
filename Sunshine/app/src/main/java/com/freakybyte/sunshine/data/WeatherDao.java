package com.freakybyte.sunshine.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.text.format.Time;
import android.util.Log;

import com.freakybyte.sunshine.SunshineApplication;
import com.freakybyte.sunshine.data.tables.LocationEntry;
import com.freakybyte.sunshine.data.tables.WeatherEntry;
import com.freakybyte.sunshine.model.ListModel;
import com.freakybyte.sunshine.model.Weather;
import com.freakybyte.sunshine.model.WeatherModel;

import org.json.JSONObject;

import java.util.List;
import java.util.Vector;

/**
 * Created by Jose Torres on 31/10/2016.
 */

public class WeatherDao {
    private static final String TAG = WeatherDao.class.getSimpleName();

    private static WeatherDao singleton;
    private Context mContext;

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

    private Context getContext() {
        return SunshineApplication.getInstance() == null ? mContext : SunshineApplication.getInstance();
    }


}
