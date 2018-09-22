package com.coderpage.framework;

/**
 * @author abner-l. 2017-02-05
 * @since 0.1.0
 */

public class Framework {

    private static Framework mInstance;


    private Framework() {
    }

    private synchronized static Framework getInstance() {
        if (mInstance == null) {
            mInstance = new Framework();
        }
        return mInstance;
    }

    public static void onAppOnCreate() {
    }

}
