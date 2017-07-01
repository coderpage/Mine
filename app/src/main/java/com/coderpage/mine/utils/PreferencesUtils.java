package com.coderpage.mine.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.UUID;

/**
 * @author abner-l. 2017-06-01
 */

public class PreferencesUtils {

    // preferences key of client changeable uuid
    // value keep by this key changed after app reinstall or clear files
    private static final String PRE_CLIENT_CHANGEABLE_UUID = "pre_client_changeable_uuid";

    /**
     * get uuid generated with {@link UUID#randomUUID()};
     *
     * @return uuid
     */
    public static String getClientChangeableUuid(Context context) {
        String uuidStr = getPreferences(context).getString(PRE_CLIENT_CHANGEABLE_UUID, "");
        if (TextUtils.isEmpty(uuidStr)) {
            UUID uuid = UUID.randomUUID();
            uuidStr = uuid.toString();
            getPreferences(context).edit().putString(PRE_CLIENT_CHANGEABLE_UUID, uuidStr).apply();
        }
        return uuidStr;
    }

    private static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

}
