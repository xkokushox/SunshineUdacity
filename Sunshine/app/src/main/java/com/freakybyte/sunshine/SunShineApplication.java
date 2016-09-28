package com.freakybyte.sunshine;

import android.app.Application;

/**
 * Created by Jose Torres on 28/09/2016.
 */

public class SunShineApplication extends Application {

    private static SunShineApplication singleton;

    public static SunShineApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }
}