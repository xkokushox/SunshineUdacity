package com.freakybyte.sunshine.web.retrofit;

import com.freakybyte.sunshine.model.WeatherModel;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by Jose Torres on 28/09/2016.
 */

public interface OpenWeatherMapService {

    @GET("forecast/daily?")
    Call<WeatherModel> getWeatherByPostalCode(@QueryMap Map<String, String> options);

}
