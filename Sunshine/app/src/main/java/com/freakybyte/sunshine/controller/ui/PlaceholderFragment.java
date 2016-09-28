package com.freakybyte.sunshine.controller.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freakybyte.sunshine.R;
import com.freakybyte.sunshine.SunShineApplication;
import com.freakybyte.sunshine.model.WeatherModel;
import com.freakybyte.sunshine.utils.DebugUtils;
import com.freakybyte.sunshine.web.retrofit.OpenWeatherMapService;
import com.freakybyte.sunshine.web.retrofit.RetrofitBuilder;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jose Torres in FreakyByte on 28/06/16.
 */
public class PlaceholderFragment extends Fragment {
    private static String TAG = "PlaceholderFragment";

    private View rootView;
    private OpenWeatherMapService apiService;

    public PlaceholderFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        apiService = RetrofitBuilder.getRetrofitBuilder().create(OpenWeatherMapService.class);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Map<String, String> params = new ArrayMap<>();
        params.put("q", "Mexico");
        params.put("appid", getString(R.string.api));
        Call<WeatherModel> call = apiService.getWeatherByPostalCode(params);
        call.enqueue(new Callback<WeatherModel>() {
            @Override
            public void onResponse(Call<WeatherModel> call, Response<WeatherModel> response) {

                switch (response.code()) {
                    case 200:
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
}
