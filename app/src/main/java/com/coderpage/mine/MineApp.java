package com.coderpage.mine;

import android.app.Application;
import android.content.Context;

import com.coderpage.framework.Framework;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * @author abner-l. 2017-02-05
 * @since 0.1.0
 */

public class MineApp extends Application {

    private static Context mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = this;
        Framework.onAppOnCreate();
        if (!BuildConfig.DEBUG) {
            CrashReport.UserStrategy userStrategy = new CrashReport.UserStrategy(getApplicationContext());
            userStrategy.setAppChannel(BuildConfig.FLAVOR);
            CrashReport.initCrashReport(getApplicationContext(),
                    Constants.BUGLY_APP_ID, BuildConfig.DEBUG, userStrategy);
        }
    }

    public static Context getAppContext() {
        return mAppContext;
    }
}
