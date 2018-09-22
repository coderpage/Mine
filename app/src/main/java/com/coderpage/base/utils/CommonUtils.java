package com.coderpage.base.utils;

import android.text.TextUtils;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author abner-l. 2017-02-05
 * @since 0.1.0
 */

public class CommonUtils {

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
     *
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
     * 检查集合是否包含元素
     *
     * @param collection 集合
     * @param object     元素
     *
     * @return 如果包含该元素，返回 true，反之 false
     */
    public static boolean collectionContains(Collection collection, Object object) {
        if (collection == null || collection.isEmpty()) {
            return false;
        }
        for (Object obj : collection) {
            if (isEqual(obj, object)) {
                return true;
            }
        }
        return false;
    }

    public static void collectionRemoveElememt(Collection collection, Object element) {
        Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            if (isEqual(iterator.next(), element)) {
                iterator.remove();
            }
        }
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
}
