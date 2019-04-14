package com.coderpage.base.utils;

import android.text.TextUtils;

import java.util.Collection;
import java.util.WeakHashMap;

/**
 * @author abner-l. 2017-02-05
 * @since 0.1.0
 */

public class CommonUtils {

    /**
     * 缓存控件点击时间，用于检测是否是连续点击，作防重复点击控制
     *
     * Key   - View
     * Value - 上一次点击时间
     */
    private static final WeakHashMap<Object, Long> VIEW_CLICK_TIME = new WeakHashMap<>();

    /**
     * 判断是否是重复点击。
     *
     * @param view 发生点击事件的控件
     * @return true-是重复点击 false-不是重复点击
     */
    public static boolean isViewFastDoubleClick(Object view) {
        if (view == null) {
            return false;
        }

        long currentTimeMillis = System.currentTimeMillis();

        // 获取上一次点击时间
        Long lastClickTime = VIEW_CLICK_TIME.get(view);
        // 没有获取到上一次点击时间，不是重复点击
        if (lastClickTime == null) {
            VIEW_CLICK_TIME.put(view, currentTimeMillis);
            return false;
        }

        long fastDoubleClickInterval = 1500;
        // 与上一次点击间隔小于 1.5 秒，判定为重复点击
        if (currentTimeMillis - lastClickTime < fastDoubleClickInterval) {
            return true;
        }

        VIEW_CLICK_TIME.put(view, currentTimeMillis);
        return false;
    }

    public static void checkNotNull(Object object) {
        checkNotNull(object, null);
    }

    public static void checkNotNull(Object object, String message) {
        if (TextUtils.isEmpty(message)) message = "param check failed";
        if (object == null) throw new NullPointerException(message);
    }

    /**
     * 连接集合元素，通过指定的 #split 连接成字符串
     *
     * @param collection 集合
     * @param split      分隔符
     * @return 拼接好的字符串
     */
    public static String collectionJoinElements(Collection collection, String split) {
        StringBuilder builder = new StringBuilder();

        int count = 0;
        for (Object object : collection) {
            builder.append(String.valueOf(object));
            count++;
            if (count < collection.size()) {
                builder.append(split);
            }
        }

        return builder.toString();
    }

    /**
     * 比较两个对象是否相等（equal）
     */
    public static boolean isEqual(Object obj1, Object obj2) {
        if (obj1 == null) {
            return obj2 == null;
        }
        return obj1.equals(obj2);
    }

    public static int string2int(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int string2int(String string, int defaultVal) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return defaultVal;
    }

    public static float string2float(String string, float defaultVal) {
        try {
            return Float.parseFloat(string);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return defaultVal;
    }

    /**
     * 去掉数字后无用的小数部分
     */
    public static String removeOddDecimal(String number) {
        if (TextUtils.isEmpty(number)) {
            return number;
        }
        if (number.indexOf(".") > 0) {
            //去掉后面无用的零
            number = number.replaceAll("0+?$", "");
            //如小数点后面全是零则去掉小数点
            number = number.replaceAll("[.]$", "");
        }
        return number;
    }
}
