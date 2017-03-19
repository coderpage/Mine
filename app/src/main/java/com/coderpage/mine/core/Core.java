package com.coderpage.mine.core;

import com.coderpage.framework.concurrency.DefaultWorkExecutor;

/**
 * @author abner-l. 2017-01-22
 */

public class Core {

    private DefaultWorkExecutor mWorkExecutor;
    private static Core mInstance;

    private Core() {
        mWorkExecutor = new DefaultWorkExecutor();
    }

    public static synchronized Core getInstance() {
        if (mInstance == null) {
            mInstance = new Core();
        }

        return mInstance;
    }

    public DefaultWorkExecutor workExecutor() {
        return mWorkExecutor;
    }
}
