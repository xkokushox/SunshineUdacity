package com.freakybyte.sunshine.data;

/**
 * Created by Jose Torres on 31/10/2016.
 */

public class WeatherDao {
    private static final String TAG = WeatherDao.class.getSimpleName();

    private static WeatherDao singleton;

    public static WeatherDao getInstance() {
        if (singleton == null)
            singleton = new WeatherDao();
        return singleton;
    }

    public WeatherDao() {
    }
}
