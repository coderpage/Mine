package com.coderpage.framework.utils;

/**
 * @author abner-l. 2017-02-05
 * @since 0.1.0
 */

public class CommonUtils {

    public static void checkNotNull(Object object) {
        if (object == null) {
            throw new NullPointerException();
        }
    }

}
