package com.coderpage.mine.app.tally.network;

import android.os.Build;

import com.coderpage.mine.BuildConfig;
import com.coderpage.mine.MineApp;
import com.coderpage.mine.utils.AndroidUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author lc. 2019-05-20 17:32
 * @since 0.6.2
 */
public class RequestInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request().newBuilder()
                .addHeader("Platform", "android")
                .addHeader("Platform-Version", String.valueOf(Build.VERSION.SDK_INT))
                .addHeader("Device-Id", AndroidUtils.generateDeviceId(MineApp.getAppContext()))
                .addHeader("Device-Name", Build.MODEL)
                .addHeader("Client-Version", String.valueOf(BuildConfig.VERSION_CODE))
                .addHeader("Client-Version-Name", BuildConfig.VERSION_NAME)
                .build();
        return chain.proceed(request);
    }
}
