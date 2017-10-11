package com.coderpage.mine;

import android.app.Application;
import android.content.Context;

import com.coderpage.framework.Framework;
import com.coderpage.mine.app.tally.update.UpdateUtils;
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
        Global.init(this);
        Framework.onAppOnCreate();
        if (!BuildConfig.DEBUG) {
            CrashReport.UserStrategy userStrategy = new CrashReport.UserStrategy(getApplicationContext());
            userStrategy.setAppChannel(BuildConfig.FLAVOR);
            CrashReport.initCrashReport(getApplicationContext(),
                    Constants.BUGLY_APP_ID, BuildConfig.DEBUG, userStrategy);
        }
        // 检查新版本
        UpdateUtils.startNewClientVersionCheckBackground(this);
    }

    public static Context getAppContext() {
        return mAppContext;
    }

//    private void logPhoneInfo(){
//        LOGE("TAG","Build.ID: " + Build.ID);
//        LOGE("TAG","Build.BOARD: " + Build.BOARD);
//        LOGE("TAG","Build.BOOTLOADER: " + Build.BOOTLOADER);
//        LOGE("TAG","Build.DEVICE: " + Build.DEVICE);
//        LOGE("TAG","Build.DISPLAY: " + Build.DISPLAY);
//        LOGE("TAG","Build.FINGERPRINT: " + Build.FINGERPRINT);
//        LOGE("TAG","Build.getRadioVersion(): " + Build.getRadioVersion());
//        LOGE("TAG","Build.HARDWARE: " + Build.HARDWARE);
//        LOGE("TAG","Build.HOST: " + Build.HOST);
//        LOGE("TAG","Build.MANUFACTURER: " + Build.MANUFACTURER);
//        LOGE("TAG","Build.MODEL: " + Build.MODEL);
//        LOGE("TAG","Build.PRODUCT: " + Build.PRODUCT);
//        LOGE("TAG","Build.SERIAL: " + Build.SERIAL);
//        LOGE("TAG","Build.TAGS: " + Build.TAGS);
//        LOGE("TAG","Build.TYPE: " + Build.TYPE);
//        LOGE("TAG","Build.UNKNOWN: " + Build.UNKNOWN);
//        LOGE("TAG","Build.USER: " + Build.USER);
//        LOGE("TAG","Build.TIME: " + Build.TIME);
//        LOGE("TAG","Build.VERSION.SDK_INT: " + Build.VERSION.SDK_INT);
//    }
}
