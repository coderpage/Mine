package com.coderpage.lib.update;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @author lc. 2017-09-24 18:49
 * @since 0.5.0
 */

public class UploadTest {

    @Test
    public void testUpload() {
        File apkFile = new File("/Users/lc/Desktop/app-yingyongbao-release.apk");
        RequestBody apkFileBody = RequestBody.create(
                MediaType.parse("application/vnd.android.package-archive"), apkFile);
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", apkFile.getName(), apkFileBody)
                .addFormDataPart("token", "DIxNCwiaXNzIjoiSHVtYmxlIn0")
                .addFormDataPart("appName", "我的记账本")
                .addFormDataPart("changeLog", "修复部分已知bug")
                .addFormDataPart("isRelease", "true")
                .build();
        Request request = new Request.Builder().url("http://127.0.0.1:8001/api/v1/version/upload")
                .post(multipartBody)
                .build();
        try {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();
            okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
