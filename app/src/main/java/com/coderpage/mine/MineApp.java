package com.coderpage.mine;

import android.app.Application;

import com.coderpage.framework.Framework;

/**
 * @author abner-l. 2017-02-05
 * @since 0.1.0
 */

public class MineApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Framework.onAppOnCreate();
    }

}
