package com.freakybyte.sunshine.controller.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.freakybyte.sunshine.model.WeatherModel;
import com.freakybyte.sunshine.utils.DebugUtils;
import com.freakybyte.sunshine.utils.SunshineUtil;
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
public class ForecastFragment extends Fragment {
    private static String TAG = "ForecastFragment";

    private View rootView;
    @BindView(R.id.listview_forecast)
    public ListView listView;

    private OpenWeatherMapService apiService;
    private ArrayAdapter<String> mForecastAdapter;
    private ArrayList<String> weekForecast = new ArrayList<>();

    public ForecastFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        apiService = RetrofitBuilder.getRetrofitBuilder().create(OpenWeatherMapService.class);

        mForecastAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, weekForecast);
        listView.setAdapter(mForecastAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent mDetail = new Intent(getContext(), DetailActivity.class);
                mDetail.putExtra(Intent.EXTRA_TEXT, mForecastAdapter.getItem(i));
                startActivity(mDetail);
            }
        });

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = prefs.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));
        String unit = prefs.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_units_metric));

        getWeatherReport(location, "json", unit, 7);
    }

    private void getWeatherReport(String zipCode, String mode, String unit, int count) {
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
                        mForecastAdapter.clear();
                        Time dayTime = new Time();
                        dayTime.setToNow();
                        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
                        for (int i = 0; i < response.body().getList().size(); i++) {
                            long dateTime = dayTime.setJulianDay(julianStartDay + i);
                            String day = SunshineUtil.getReadableDateString(dateTime);
                            String description = response.body().getList().get(i).getWeather().get(0).getDescription();
                            String highAndLow = SunshineUtil.formatHighLows(response.body().getList().get(i).getTemp().getMax(), response.body().getList().get(i).getTemp().getMin());
                            weekForecast.add(day + " - " + description + " - " + highAndLow);
                        }

                        mForecastAdapter.notifyDataSetChanged();
                        DebugUtils.logDebug(TAG, "All cool:: " + response.body().getCity().getName());
                        break;
                    default:
                        DebugUtils.logError(TAG, "LogInInServer:: Error Code:: " + response.code());
                        break;
                }
            }

            @Override
            public void onFailure(Call<WeatherModel> call, Throwable t) {
                DebugUtils.logError(TAG, "LogInInServer:: onFailure:: " + t.getLocalizedMessage());
            }

        });
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

    private void openPreferredLocationInMap() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String location = sharedPrefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));

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
