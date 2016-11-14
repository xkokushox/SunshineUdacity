package com.freakybyte.sunshine.controller.ui.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.freakybyte.sunshine.R;
import com.freakybyte.sunshine.data.WeatherDao;
import com.freakybyte.sunshine.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jose Torres on 18/10/2016.
 */

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final static String TAG = "DetailFragment";

    @BindView(R.id.detail_icon)
    public ImageView mIconView;
    @BindView(R.id.detail_date_textview)
    public TextView mDateView;
    @BindView(R.id.detail_day_textview)
    public TextView mFriendlyDateView;
    @BindView(R.id.detail_forecast_textview)
    public TextView mDescriptionView;
    @BindView(R.id.detail_high_textview)
    public TextView mHighTempView;
    @BindView(R.id.detail_low_textview)
    public TextView mLowTempView;
    @BindView(R.id.detail_humidity_textview)
    public TextView mHumidityView;
    @BindView(R.id.detail_wind_textview)
    public TextView mWindView;
    @BindView(R.id.detail_pressure_textview)
    public TextView mPressureView;

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private ShareActionProvider mShareActionProvider;
    private String mForecastStr;
    private WeatherDao mWeatherDao;
    private static final int DETAIL_LOADER = 0;

    public DetailFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detail, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mForecastStr != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastStr + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mWeatherDao = WeatherDao.getInstance();

        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        return mWeatherDao.getWeatherDetailCursor(intent.getData());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            // Read weather condition ID from cursor
            int weatherId = data.getInt(WeatherDao.COL_WEATHER_CONDITION_ID);
            // Use placeholder Image
            mIconView.setImageResource(R.mipmap.ic_launcher);

            // Read date from cursor and update views for day of week and date
            long date = data.getLong(WeatherDao.COL_WEATHER_DATE);
            String friendlyDateText = Utils.getDayName(getActivity(), date);
            String dateText = Utils.getFormattedMonthDay(getActivity(), date);
            mFriendlyDateView.setText(friendlyDateText);
            mDateView.setText(dateText);

            // Read description from cursor and update view
            String description = data.getString(WeatherDao.COL_WEATHER_DESC);
            mDescriptionView.setText(description);

            // Read high temperature from cursor and update view
            boolean isMetric = Utils.isMetric(getActivity());

            double high = data.getDouble(WeatherDao.COL_WEATHER_MAX_TEMP);
            String highString = Utils.formatTemperature(high, isMetric);
            mHighTempView.setText(highString);

            // Read low temperature from cursor and update view
            double low = data.getDouble(WeatherDao.COL_WEATHER_MIN_TEMP);
            String lowString = Utils.formatTemperature(low, isMetric);
            mLowTempView.setText(lowString);

            // Read humidity from cursor and update view
            float humidity = data.getFloat(WeatherDao.COL_WEATHER_HUMIDITY);
            mHumidityView.setText(getActivity().getString(R.string.format_humidity, humidity));

            // Read wind speed and direction from cursor and update view
            float windSpeedStr = data.getFloat(WeatherDao.COL_WEATHER_WIND_SPEED);
            float windDirStr = data.getFloat(WeatherDao.COL_WEATHER_DEGREES);
            mWindView.setText(Utils.getFormattedWind(getActivity(), windSpeedStr, windDirStr));

            // Read pressure from cursor and update view
            float pressure = data.getFloat(WeatherDao.COL_WEATHER_PRESSURE);
            mPressureView.setText(getActivity().getString(R.string.format_pressure, pressure));

            // We still need this for the share intent
            mForecastStr = String.format("%s - %s - %s/%s", dateText, description, high, low);

            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
