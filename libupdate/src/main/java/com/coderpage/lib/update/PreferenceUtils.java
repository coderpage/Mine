package com.coderpage.lib.update;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author lc. 2017-09-23 23:56
 * @since 0.5.0
 */

public class PreferenceUtils {

    private static final String FILE_NAME = "lib_client_update_preference";

    /**
     * 新版本版本号
     */
    private static final String PRE_NEW_VERSION = "pre_new_version";
    /**
     * 新版本构建号
     */
    private static final String PRE_NEW_VERSION_BUILD_CODE = "pre_new_version_code";
    /**
     * 新版本 APK 大小
     */
    private static final String PRE_NEW_VERSION_APK_SIZE = "pre_new_version_apk_size";
    /**
     * 新版本应用名称
     */
    private static final String PRE_NEW_VERSION_APK_NAME = "pre_new_version_apk_name";
    /**
     * 新版本 APK 下载地址
     */
    private static final String PRE_NEW_VERSION_APK_DOWNLOAD_URL = "pre_new_version_apk_download_url";
    /**
     * 新版本更新日志
     */
    private static final String PRE_NEW_VERSION_CHANGELOG = "pre_new_version_changelog";

    /**
     * 不再提示
     */
    private static final String PRE_DO_NOT_REMIND_AGAIN = "pre_do_not_remind_again";

    public static void refreshNewVersionApkInfo(Context context, ApkModel apkModel) {
        getPreference(context)
                .edit()
                .putString(PRE_NEW_VERSION, apkModel.getVersion())
                .putLong(PRE_NEW_VERSION_BUILD_CODE, apkModel.getBuildCode())
                .putString(PRE_NEW_VERSION_APK_NAME, apkModel.getName())
                .putLong(PRE_NEW_VERSION_APK_SIZE, apkModel.getApkSizeBytes())
                .putString(PRE_NEW_VERSION_APK_DOWNLOAD_URL, apkModel.getDownloadUrl())
                .putString(PRE_NEW_VERSION_CHANGELOG, apkModel.getChangelog())
                .apply();
    }

    public static ApkModel restoreApkModel(Context context) {
        SharedPreferences preference = getPreference(context);
        long buildCode = preference.getLong(PRE_NEW_VERSION_BUILD_CODE, 0L);
        long apkSize = preference.getLong(PRE_NEW_VERSION_APK_SIZE, 0L);
        String version = preference.getString(PRE_NEW_VERSION, "");
        String name = preference.getString(PRE_NEW_VERSION_APK_NAME, "");
        String downloadUrl = preference.getString(PRE_NEW_VERSION_APK_DOWNLOAD_URL, "");
        String changelog = preference.getString(PRE_NEW_VERSION_CHANGELOG, "");

        ApkModelImpl apkModel = new ApkModelImpl();
        apkModel.setBuildCode(buildCode);
        apkModel.setApkSizeBytes(apkSize);
        apkModel.setVersion(version);
        apkModel.setName(name);
        apkModel.setDownloadUrl(downloadUrl);
        apkModel.setChangelog(changelog);
        return apkModel;
    }

    public static String getNewVersion(Context context) {
        return getPreference(context).getString(PRE_NEW_VERSION, "");
    }

    public static long getNewVersionBuildCode(Context context) {
        return getPreference(context).getLong(PRE_NEW_VERSION_BUILD_CODE, 0);
    }

    public static boolean isRemindAgain(Context context,
                                        String versionName,
                                        long versionCode) {
        return getPreference(context).getBoolean(
                PRE_DO_NOT_REMIND_AGAIN + versionName + versionCode, true);
    }

    public static void setRemindAgain(Context context,
                                      String versionName,
                                      long versionCode,
                                      boolean remindAgain) {
        getPreference(context).edit().putBoolean(
                PRE_DO_NOT_REMIND_AGAIN + versionName + versionCode, remindAgain).apply();
    }

    private static SharedPreferences getPreference(Context context) {
        return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }
}
