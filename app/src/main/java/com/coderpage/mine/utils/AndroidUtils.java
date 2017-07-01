package com.coderpage.mine.utils;

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

}
