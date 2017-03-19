package com.coderpage.framework;

import com.coderpage.framework.utils.CommonUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author abner-l. 2017-02-05
 * @since 0.1.0
 */

public class Framework {

    private static Framework mInstance;

    private Set<GlobalCallback> mGlobalCallbackSet = new HashSet<>();

    private Framework() {
    }

    private synchronized static Framework getInstance() {
        if (mInstance == null) {
            mInstance = new Framework();
        }
        return mInstance;
    }

    public static void onAppOnCreate() {
        for (GlobalCallback callback : getInstance().mGlobalCallbackSet) {
            callback.onAppCreate();
        }
    }

    public static void registerGlobalCallback(GlobalCallback callback) {
        CommonUtils.checkNotNull(callback);
        getInstance().mGlobalCallbackSet.add(callback);
    }

    public static void unregisterGlobalCallback(GlobalCallback callback) {
        CommonUtils.checkNotNull(callback);
        getInstance().mGlobalCallbackSet.remove(callback);
    }
}
