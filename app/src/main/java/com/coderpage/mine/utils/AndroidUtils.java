package com.coderpage.mine.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import java.util.UUID;

/**
 * @author abner-l. 2017-06-01
 */

public class AndroidUtils {

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
