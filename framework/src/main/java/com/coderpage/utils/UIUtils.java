package com.coderpage.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 * @author abner-l. 2017-03-06
 * @since 0.1.0
 */

public class UIUtils {

    public static void showToastShort(Context context, @StringRes int msgResId) {
        showToastShort(context, context.getString(msgResId));
    }

    public static void showToastLong(Context context, @StringRes int msgResId) {
        showToastLong(context, context.getString(msgResId));
    }

    public static void showToastShort(final Context context, final String message) {
        CommonUtils.checkNotNull(context, "");
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show());
        }
    }

    public static void showToastLong(final Context context, final String message) {
        CommonUtils.checkNotNull(context, "");
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> Toast.makeText(context, message, Toast.LENGTH_LONG).show());
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
