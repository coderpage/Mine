package com.coderpage.mine;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;
import android.util.SparseArray;

import com.alibaba.android.arouter.launcher.ARouter;
import com.coderpage.base.utils.UIUtils;
import com.coderpage.base.widget.LoadingLayout;
import com.coderpage.framework.Framework;
import com.coderpage.mine.app.tally.update.UpdateUtils;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * @author abner-l. 2017-02-05
 * @since 0.1.0
 */

public class MineApp extends Application {

    private static Application mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = this;
        Global.init(this);
        Framework.onAppOnCreate();
        if (BuildConfig.DEBUG) {
            ARouter.openDebug();
            ARouter.openLog();
        }
        if (!BuildConfig.DEBUG) {
            CrashReport.UserStrategy userStrategy = new CrashReport.UserStrategy(getApplicationContext());
            userStrategy.setAppChannel(BuildConfig.FLAVOR);
            CrashReport.initCrashReport(getApplicationContext(),
                    BuildConfig.BUGLY_APP_ID, BuildConfig.DEBUG, userStrategy);
        }
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        // 检查新版本
        UpdateUtils.startNewClientVersionCheckBackground(this);
        // 初始化 LoadingLayout
        initLoadingLayout();
        // 初始化 ARouter
        ARouter.init(this);
    }

    public static Application getAppContext() {
        return mAppContext;
    }

    private void initLoadingLayout() {
        SparseArray<LoadingLayout.Config> globalConfig = LoadingLayout.getGlobalConfig();

        LoadingLayout.Config emptyConfig = new LoadingLayout.Config();
        emptyConfig.setIconRes(R.drawable.ic_loading_layout_empty);
        emptyConfig.setMessage(UIUtils.getString(this, R.string.loading_layout_message_empty));
        emptyConfig.setMessageTextStyle(R.style.TextAppearance_LoadingMessage);
        emptyConfig.setButtonPositiveBackgroundRes(R.drawable.bg_accent_btn_round);
        emptyConfig.setButtonPositiveText(UIUtils.getString(this, R.string.loading_layout_button_positive_text_empty));
        emptyConfig.setButtonPositiveTextStyle(R.style.TextAppearance_LoadingPositiveButton);

        LoadingLayout.Config errorConfig = new LoadingLayout.Config();
        errorConfig.setIconRes(R.drawable.ic_loading_layout_empty);
        errorConfig.setMessage(UIUtils.getString(this, R.string.loading_layout_message_error));
        errorConfig.setMessageTextStyle(R.style.TextAppearance_LoadingMessage);
        errorConfig.setButtonPositiveBackgroundRes(R.drawable.bg_accent_btn_round);
        errorConfig.setButtonPositiveText(UIUtils.getString(this, R.string.loading_layout_button_positive_text_error));
        errorConfig.setButtonPositiveTextStyle(R.style.TextAppearance_LoadingPositiveButton);

        globalConfig.append(LoadingLayout.STATUS_SUCCESS, new LoadingLayout.Config());
        globalConfig.append(LoadingLayout.STATUS_LOADING, new LoadingLayout.Config());
        globalConfig.append(LoadingLayout.STATUS_EMPTY, emptyConfig);
        globalConfig.append(LoadingLayout.STATUS_ERROR, errorConfig);
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
