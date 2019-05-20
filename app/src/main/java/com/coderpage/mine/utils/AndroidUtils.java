package com.coderpage.mine.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.util.UUID;

/**
 * @author abner-l. 2017-06-01
 */

public class AndroidUtils {

    /**
     * 获取唯一 DEVICE ID
     */
    public static String generateDeviceId(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String deviceId = "" + tm.getDeviceId();
            String simSerialNumber = "" + tm.getSimSerialNumber();
            String androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            UUID deviceUuid = new UUID(androidId.hashCode(), ((long) deviceId.hashCode() << 32) | simSerialNumber.hashCode());
            return deviceUuid.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "DEFAULT";
        }
    }

    /**
     * generate uuid
     *
     * @return uuid
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().toUpperCase();
    }

    /**
     * 打开应用设置页面
     */
    public static void openAppSettingPage(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }

}
