package com.coderpage.app.gradle.plugin

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

/**
 * @author lc. 2017-09-26 22:44
 * @since 0.5.0 */
class UploadClient {

    String API_SERVER = "http://app.coderpage.com";
    String UPLOAD_API = "/api/v1/version/upload"

    MediaType JSON = MediaType.parse("application/json; charset=utf-8")

    UploadClient() {}

    UploadClient(String apiServer) {
        API_SERVER = apiServer;
    }

    void uploadApk(Version version) {

        File apkFile = new File(version.apkPath)
        RequestBody bodyFile = RequestBody.create(
                MediaType.parse("application/vnd.android.package-archive"), apkFile)

        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", apkFile.name, bodyFile)
                .addFormDataPart("token", version.token)
                .addFormDataPart("appName", version.appName)
                .addFormDataPart("changeLog", version.changeLog)
                .addFormDataPart("channelName", version.channelName)
                .addFormDataPart("isRelease", String.valueOf(version.isRelease))
                .build()

        println("upload url: " + API_SERVER + UPLOAD_API)
        Request request = new Request.Builder().url(API_SERVER + UPLOAD_API)
                .post(multipartBody)
                .build()

        Response response = new OkHttpClient().newCall(request).execute()
        if (response.code() == 200) {
            println("==> 上传成功")
            println(response.body().string())
        } else {
            println("==> 上传失败")
            println(response.code() + " " + response.message())
            println()
        }
    }


}
