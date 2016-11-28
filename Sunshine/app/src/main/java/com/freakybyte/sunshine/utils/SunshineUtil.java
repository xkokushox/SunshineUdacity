package com.freakybyte.sunshine.utils;

import android.content.Context;

import java.text.SimpleDateFormat;

/**
 * Created by Jose Torres on 29/09/2016.
 */

public class SunshineUtil {

    public static String getReadableDateString(long time) {
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    public static String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    private String formatHighLows(Context mContext, double high, double low) {
        boolean isMetric = Utils.isMetric(mContext);
        String highLowStr = Utils.formatTemperature(high) + "/" + Utils.formatTemperature(low);
        return highLowStr;
    }
}
