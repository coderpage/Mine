package com.coderpage.mine.app.tally.persistence.preference;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author lc. 2019-04-06 23:17
 * @since 0.6.0
 *
 * 账本设置
 */

public class SettingPreference {

    private static final String FILE_NAME = "tally_setting_preference";

    private static final String KEY_HIDE_MONEY = "key_hide_money";
    private static final String KEY_BUDGET_MONTH = "key_budget_month";
    private static final String KEY_AUTO_BACKUP = "key_auto_backup";
    private static final String KEY_FINGERPRINT_OPEN = "key_fingerprint_open";

    /** 是否隐藏金额 */
    public static boolean getHideMoney(Context context) {
        return getPreference(context).getBoolean(KEY_HIDE_MONEY, false);
    }

    /** 设置是否隐藏金额 */
    public static void setHideMoney(Context context, boolean hideMoney) {
        getPreference(context).edit().putBoolean(KEY_HIDE_MONEY, hideMoney).apply();
    }

    /** 月预算金额 */
    public static float getBudgetMonth(Context context) {
        return getPreference(context).getFloat(KEY_BUDGET_MONTH, 0);
    }

    /** 设置月预算金额 */
    public static void setBudgetMonth(Context context, float budget) {
        getPreference(context).edit().putFloat(KEY_BUDGET_MONTH, budget).apply();
    }

    /** 是否自动备份 */
    public static boolean isAutoBackup(Context context) {
        return getPreference(context).getBoolean(KEY_AUTO_BACKUP, false);
    }

    /** 设置自动备份 */
    public static void setAutoBackup(Context context, boolean autoBackup) {
        getPreference(context).edit().putBoolean(KEY_AUTO_BACKUP, autoBackup).apply();
    }

    /** 是否开启指纹密码 */
    public static boolean isFingerprintSecretOpen(Context context) {
        return getPreference(context).getBoolean(KEY_FINGERPRINT_OPEN, false);
    }

    /** 设置是否开启指纹密码 */
    public static void setFingerprintSecretOpen(Context context, boolean fingerprintSecretOpen) {
        getPreference(context).edit().putBoolean(KEY_FINGERPRINT_OPEN, fingerprintSecretOpen).apply();
    }

    private static SharedPreferences getPreference(Context context) {
        return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }
}
