package com.freakybyte.sunshine.data;

import android.net.Uri;
import android.test.AndroidTestCase;

import com.freakybyte.sunshine.data.tables.WeatherEntry;

/**
 * Created by Jose Torres on 02/11/2016.
 */

public class TestWeatherContract  extends AndroidTestCase {

    // intentionally includes a slash to make sure Uri is getting quoted correctly
    private static final String TEST_WEATHER_LOCATION = "/North Pole";
    private static final long TEST_WEATHER_DATE = 1419033600L;  // December 20th, 2014

    public void testBuildWeatherLocation() {
        Uri locationUri = WeatherEntry.buildWeatherLocation(TEST_WEATHER_LOCATION);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildWeatherLocation in " +
                        "WeatherContract.",
                locationUri);
        assertEquals("Error: Weather location not properly appended to the end of the Uri",
                TEST_WEATHER_LOCATION, locationUri.getLastPathSegment());
        assertEquals("Error: Weather location Uri doesn't match our expected result",
                locationUri.toString(),
                "content://com.freakybyte.sunshine/weather/%2FNorth%20Pole");
    }
}