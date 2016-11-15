package com.freakybyte.sunshine.controller.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.ArrayMap;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.freakybyte.sunshine.R;
import com.freakybyte.sunshine.controller.ui.activity.DetailActivity;
import com.freakybyte.sunshine.controller.ui.activity.SettingsActivity;
import com.freakybyte.sunshine.controller.ui.adapter.ForecastAdapter;
import com.freakybyte.sunshine.controller.ui.listener.CallbackWeather;
import com.freakybyte.sunshine.data.WeatherDao;
import com.freakybyte.sunshine.data.tables.WeatherEntry;
import com.freakybyte.sunshine.model.WeatherModel;
import com.freakybyte.sunshine.utils.DebugUtils;
import com.freakybyte.sunshine.utils.SunshineUtil;
import com.freakybyte.sunshine.utils.Utils;
import com.freakybyte.sunshine.web.retrofit.OpenWeatherMapService;
import com.freakybyte.sunshine.web.retrofit.RetrofitBuilder;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jose Torres in FreakyByte on 28/06/16.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static String TAG = "ForecastFragment";

    private View rootView;
    @BindView(R.id.listview_forecast)
    public ListView listView;

    private WeatherDao mWeatherDao;

    private OpenWeatherMapService apiService;
    private ForecastAdapter mForecastAdapter;
    private String mLocation = "";

    private static final int FORECAST_LOADER = 0;

    private SharedPreferences prefs;

    public ForecastFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        apiService = RetrofitBuilder.getRetrofitBuilder().create(OpenWeatherMapService.class);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        mForecastAdapter = new ForecastAdapter(getActivity(), null, 0);

        listView.setAdapter(mForecastAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
                if (cursor != null) {
                    String locationSetting = Utils.getPreferredLocation(getActivity());
                    ((CallbackWeather) getActivity())
                            .onItemSelected(WeatherEntry.buildWeatherLocationWithDate(
                                    locationSetting, cursor.getLong(WeatherDao.COL_WEATHER_DATE)
                            ));
                }
            }
        });

        mWeatherDao = WeatherDao.getInstance();

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mLocation.equals(prefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default))))
            updateWeather();
    }

    private void getWeatherFromDb() {
        getLoaderManager().initLoader(FORECAST_LOADER, null, ForecastFragment.this);
    }

    private void updateWeather() {
        mLocation = prefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        String unit = prefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_units_metric));
        getWeatherReport(mLocation, "json", unit, 14);
    }

    private void getWeatherReport(final String zipCode, String mode, String unit, int count) {
        Map<String, String> params = new ArrayMap<>();
        params.put("q", zipCode);
        params.put("mode", mode);
        params.put("units", unit);
        params.put("cnt", String.valueOf(count));
        params.put("appid", getString(R.string.api));
        Call<WeatherModel> call = apiService.getWeatherByPostalCode(params);
        call.enqueue(new Callback<WeatherModel>() {
            @Override
            public void onResponse(Call<WeatherModel> call, Response<WeatherModel> response) {

                switch (response.code()) {
                    case 200:
                        WeatherDao mWeatherDao = WeatherDao.getInstance();
                        mWeatherDao.addWeatherList(zipCode, response.body());
                        getWeatherFromDb();
                        break;
                    default:
                        DebugUtils.logError(TAG, "LogInInServer:: Error Code:: " + response.code());
                        break;
                }
            }

            @Override
            public void onFailure(Call<WeatherModel> call, Throwable t) {
                DebugUtils.logError(TAG, "GetWeatherReport:: onFailure:: " + t.getLocalizedMessage());
                getWeatherFromDb();
            }

        });
    }

    public void onLocationChanged() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(getContext(), SettingsActivity.class));
                return true;
            case R.id.action_map:
                openPreferredLocationInMap();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String locationSetting = Utils.getPreferredLocation(getActivity());

        return mWeatherDao.getWeatherCursorWithStartDate(locationSetting);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mForecastAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }

    private void openPreferredLocationInMap() {
        String location = Utils.getPreferredLocation(getActivity());

        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location)
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "Couldn't call " + location + ", no receiving apps installed!");
        }
    }
}
