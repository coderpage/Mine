package com.coderpage.mine.app.tally.update;

import com.coderpage.lib.update.ApkModel;
import com.coderpage.lib.update.Error;
import com.coderpage.lib.update.Result;
import com.coderpage.lib.update.SourceFetcher;
import com.coderpage.mine.BuildConfig;
import com.coderpage.mine.MineApp;
import com.google.gson.JsonObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author lc. 2017-10-01 00:30
 * @since 0.5.0
 */

class MineSourceFetcher implements SourceFetcher {

    private static final String VERSION_BASE_URL = "http://127.0.0.1:8001";

    @Override
    public Result<ApkModel, Error> fetchApkModel() {

        OkHttpClient okHttpClient = new OkHttpClient();
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();
        }

        Retrofit apiRetrofit = new Retrofit.Builder()
                .baseUrl(VERSION_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        UpdateApi api = apiRetrofit.create(UpdateApi.class);
        api.fetchLatestVersion(MineApp.getAppContext().getPackageName());

        return null;
    }

    interface UpdateApi {
        @GET("/api/v1/version/latest")
        Call<JsonObject> fetchLatestVersion(@Query("packageName") String packageName);
    }

}
