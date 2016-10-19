package com.freakybyte.sunshine;

import android.app.Application;

/**
 * Created by Jose Torres on 28/09/2016.
 */

public class SunshineApplication extends Application {

    private static SunshineApplication singleton;

    public static SunshineApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }
}