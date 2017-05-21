package com.coderpage.utils;

import android.content.Context;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.StringRes;

/**
 * @author abner-l. 2017-05-11
 */

public class ResUtils {

    public static int getInteger(Context context, @IntegerRes int resId) {
        return context.getResources().getInteger(resId);
    }

    public static String getString(Context context, @StringRes int strId) {
        return context.getResources().getString(strId);
    }

    public static String getString(Context context, @StringRes int strId, Object... formatArgs) {
        return context.getResources().getString(strId, formatArgs);
    }

    public static int getColor(Context context, @ColorRes int colorResId) {
        if (Build.VERSION.SDK_INT >= 23) {
            return context.getColor(colorResId);
        } else {
            return context.getResources().getColor(colorResId);
        }
    }
}
