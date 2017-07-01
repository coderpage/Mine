package com.coderpage.utils;

import android.text.TextUtils;

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

}
