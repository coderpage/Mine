package com.coderpage.base.utils;

import android.content.Context;

/**
 * @author abner-l. 2017-03-06
 * @since 0.1.0
 */

public class AndroidUtils {

    /**
     * 计算 dp 对应 px 值
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 计算 px 对应 dp 值
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
