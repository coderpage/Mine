package com.coderpage.mine.app.tally.update;

import android.support.annotation.Keep;

import com.alibaba.fastjson.annotation.JSONField;
import com.coderpage.base.utils.LogUtils;
import com.coderpage.lib.update.ApkModel;
import com.coderpage.lib.update.Error;
import com.coderpage.lib.update.Result;
import com.coderpage.lib.update.SourceFetcher;
import com.coderpage.mine.BuildConfig;
import com.coderpage.mine.MineApp;
import com.coderpage.mine.app.tally.common.error.ErrorCode;
import com.coderpage.mine.app.tally.common.server.BaseResponse;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.fastjson.FastJsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static com.coderpage.base.utils.LogUtils.makeLogTag;

/**
 * @author lc. 2017-10-01 00:30
 * @since 0.5.0
 *
 * 用于获取最新版本 APK 信息类；传递给 {@link com.coderpage.lib.update.Updater}；
 */

class LatestVersionFetcher implements SourceFetcher {
    private static final String TAG = makeLogTag(LatestVersionFetcher.class);

    //    private static final String VERSION_BASE_URL = "http://192.168.1.29:8001";
    private static final String VERSION_BASE_URL = "http://app.coderpage.com";

    @Override
    public Result<ApkModel, Error> fetchApkModel() {

        Result<ApkModel, Error> result = new Result<>();

        OkHttpClient okHttpClient = new OkHttpClient();
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor((message) -> {
                LogUtils.LOGI(TAG, message);
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();
        }

        Retrofit apiRetrofit = new Retrofit.Builder()
                .baseUrl(VERSION_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(FastJsonConverterFactory.create())
                .build();
        UpdateApi api = apiRetrofit.create(UpdateApi.class);

        // 获取最新版本信息
        try {
            Response<LatestVersionResponse> response =
                    api.fetchLatestVersion(MineApp.getAppContext().getPackageName()).execute();
            if (!response.isSuccessful()) {
                result.setErr(new Error(response.code(), response.message()));
                return result;
            }
            LatestVersionResponse body = response.body();
            if (body.getStatus() != 200) {
                result.setErr(new Error(body.getStatus(), body.getMessage()));
                return result;
            }
            result.setData(body.getLatestVersion());
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            result.setErr(new Error(ErrorCode.UNKNOWN, e.getMessage()));
            return result;
        }
    }

    interface UpdateApi {
        @GET("/api/v1/version/latest")
        Call<LatestVersionResponse> fetchLatestVersion(@Query("packageName") String packageName);
    }

    @Keep
    private static class LatestVersionResponse extends BaseResponse {

        @JSONField(name = "data")
        private LatestVersion latestVersion;

        public LatestVersion getLatestVersion() {
            return latestVersion;
        }

        public void setLatestVersion(LatestVersion latestVersion) {
            this.latestVersion = latestVersion;
        }
    }

}
