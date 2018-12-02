package com.coderpage.base.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.view.WindowManager;
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

    /**
     * 计算窗口大小
     */
    public static Point getWindowSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        return point;
    }

    /** 获取颜色值 */
    public static int getColor(Context context, @ColorRes int colorRes) {
        try {
            return context.getResources().getColor(colorRes);
        } catch (Resources.NotFoundException e) {
            return Color.TRANSPARENT;
        }
    }

    /** 获取字符串资源 */
    public static String getString(Context context, @StringRes int stringRes, Object... args) {
        try {
            return context.getResources().getString(stringRes, args);
        } catch (Resources.NotFoundException e) {
            return "";
        }
    }
}
