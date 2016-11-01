package com.freakybyte.sunshine.utils;

import android.text.format.Time;

/**
 * Created by Jose Torres on 31/10/2016.
 */

public class Utils {
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }
}
