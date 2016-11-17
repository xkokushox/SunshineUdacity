package com.freakybyte.sunshine.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.freakybyte.sunshine.R;
import com.freakybyte.sunshine.data.WeatherDao;
import com.freakybyte.sunshine.model.WeatherModel;
import com.freakybyte.sunshine.utils.DebugUtils;
import com.freakybyte.sunshine.utils.Utils;
import com.freakybyte.sunshine.web.retrofit.OpenWeatherMapService;
import com.freakybyte.sunshine.web.retrofit.RetrofitBuilder;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jose Torres on 16/11/2016.
 */

public class SunshineSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = SunshineSyncAdapter.class.getSimpleName();
    private OpenWeatherMapService apiService;

    public SunshineSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        apiService = RetrofitBuilder.getRetrofitBuilder().create(OpenWeatherMapService.class);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync Called.");


        final String locationQuery = Utils.getPreferredLocation(getContext());

        String format = "json";
        String units = "metric";
        int numDays = 14;

        Map<String, String> params = new ArrayMap<>();
        params.put("q", locationQuery);
        params.put("mode", format);
        params.put("units", units);
        params.put("cnt", String.valueOf(numDays));
        params.put("appid", getContext().getString(R.string.api));
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
                        DebugUtils.logError(LOG_TAG, "LogInInServer:: Error Code:: " + response.code());
                        break;
                }
            }

            @Override
            public void onFailure(Call<WeatherModel> call, Throwable t) {
                DebugUtils.logError(LOG_TAG, "GetWeatherReport:: onFailure:: " + t.getLocalizedMessage());
            }

        });
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

        }
        return newAccount;
    }
}