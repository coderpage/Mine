package com.coderpage.mine;

import android.content.Context;

import com.coderpage.mine.app.tally.persistence.preference.SettingPreference;

/**
 * @author lc. 2017-10-05 23:18
 * @since 0.5.0
 */

public class Global {

    private volatile static Global mInstance = null;

    /** 是否需要验证指纹 */
    private boolean mNeedFingerprint;

    private Context mAppContext = null;

    private Global() {
    }

    public static Global getInstance() {
        if (mInstance == null) {
            synchronized (Global.class) {
                if (mInstance == null) {
                    mInstance = new Global();
                }
            }
        }
        return mInstance;
    }

    public static void init(Context context) {
        getInstance().setAppContext(context);
        getInstance().setNeedFingerprint(SettingPreference.isFingerprintSecretOpen(context));
    }

    private void setAppContext(Context context) {
        mAppContext = context.getApplicationContext();
    }

    public Context getmAppContext() {
        return mAppContext;
    }

    /** 是否需要验证指纹 */
    public boolean isNeedFingerprint() {
        return mNeedFingerprint;
    }

    public void setNeedFingerprint(boolean needFingerprint) {
        mNeedFingerprint = needFingerprint;
    }
}
