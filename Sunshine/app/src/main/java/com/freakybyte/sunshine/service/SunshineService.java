package com.freakybyte.sunshine.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.util.ArrayMap;

import com.freakybyte.sunshine.R;
import com.freakybyte.sunshine.data.WeatherDao;
import com.freakybyte.sunshine.model.WeatherModel;
import com.freakybyte.sunshine.utils.DebugUtils;
import com.freakybyte.sunshine.web.retrofit.OpenWeatherMapService;
import com.freakybyte.sunshine.web.retrofit.RetrofitBuilder;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jose Torres on 16/11/2016.
 */

public class SunshineService extends IntentService {
    private static String TAG = "SunshineService";
    private OpenWeatherMapService apiService;
    public static final String LOCATION_QUERY_EXTRA = "lqe";
    private final String LOG_TAG = SunshineService.class.getSimpleName();

    public SunshineService() {
        super("Sunshine");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        apiService = RetrofitBuilder.getRetrofitBuilder().create(OpenWeatherMapService.class);
        final String locationQuery = intent.getStringExtra(LOCATION_QUERY_EXTRA);

        String format = "json";
        String units = "metric";
        int numDays = 14;

        Map<String, String> params = new ArrayMap<>();
        params.put("q", locationQuery);
        params.put("mode", format);
        params.put("units", units);
        params.put("cnt", String.valueOf(numDays));
        params.put("appid", getString(R.string.api));
        Call<WeatherModel> call = apiService.getWeatherByPostalCode(params);
        call.enqueue(new Callback<WeatherModel>() {
            @Override
            public void onResponse(Call<WeatherModel> call, Response<WeatherModel> response) {

                switch (response.code()) {
                    case 200:
                        WeatherDao mWeatherDao = WeatherDao.getInstance();
                        mWeatherDao.addWeatherList(locationQuery, response.body());
                        break;
                    default:
                        DebugUtils.logError(TAG, "LogInInServer:: Error Code:: " + response.code());
                        break;
                }
            }

            @Override
            public void onFailure(Call<WeatherModel> call, Throwable t) {
                DebugUtils.logError(TAG, "GetWeatherReport:: onFailure:: " + t.getLocalizedMessage());
            }

        });

    }

    public static class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Intent sendIntent = new Intent(context, SunshineService.class);
            sendIntent.putExtra(SunshineService.LOCATION_QUERY_EXTRA, intent.getStringExtra(SunshineService.LOCATION_QUERY_EXTRA));
            context.startService(sendIntent);

        }
    }
}