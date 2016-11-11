package com.freakybyte.sunshine.controller.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.freakybyte.sunshine.R;
import com.freakybyte.sunshine.data.WeatherDao;

/**
 * Created by Jose Torres on 10/11/2016.
 */

public class ForecastAdapter extends CursorAdapter {

    private WeatherDao mWeatherDao;


    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mWeatherDao = new WeatherDao(context);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_forecast, parent, false);
        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        TextView tv = (TextView) view.findViewById(R.id.list_item_forecast_textview);
        tv.setText(mWeatherDao.convertCursorRowToUXFormat(cursor));
    }

}