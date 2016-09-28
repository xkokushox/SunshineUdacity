package com.freakybyte.sunshine.web.retrofit;

import com.freakybyte.sunshine.R;
import com.freakybyte.sunshine.SunShineApplication;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Jose Torres on 28/09/2016.
 */

public class RetrofitBuilder {

    private static Retrofit retrofit;

    public static Retrofit getRetrofitBuilder() {
        if (retrofit == null)
            retrofit = new Retrofit.Builder()
                    .baseUrl(SunShineApplication.getInstance().getString(R.string.url_base))
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();
        return retrofit;
    }

}
