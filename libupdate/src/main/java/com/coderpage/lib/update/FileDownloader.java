package com.coderpage.lib.update;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * @author lc. 2017-09-23 23:55
 * @since 0.5.0
 */

class FileDownloader {
    private Context mContext;

    FileDownloader(Context context) {
        mContext = context;
    }

    Result<File, Error> download(String downloadUrl, String fileName, final DownloadProgressListener progressListener) {
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        okhttp3.Response originalResponse = chain.proceed(chain.request());
                        return originalResponse.newBuilder()
                                .body(new DownloadProgressResponseBody(
                                        originalResponse.body(), progressListener))
                                .build();
                    }
                })
                .build();

        Result<File, Error> result;
        try {
            okhttp3.Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                DownloadCache downloadCache = new DownloadCache(mContext);
                File apkFile = downloadCache.saveFile(fileName, response.body().source());
                if (apkFile != null) {
                    result = new Result<>(apkFile, null);
                } else {
                    result = new Result<>(null,
                            new Error(-1, "save file failed"));
                }

            } else {
                result = new Result<>(null, new Error(response.code(), response.message()));
            }
        } catch (IOException e) {
            result = new Result<>(null, new Error(-1, e.getMessage()));
        }

        return result;
    }
}
