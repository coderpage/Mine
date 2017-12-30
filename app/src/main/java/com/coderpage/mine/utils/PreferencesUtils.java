package com.coderpage.mine.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.coderpage.base.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author abner-l. 2017-06-01
 */

public class PreferencesUtils {

    /**
     * preferences key of client changeable uuid
     * value keep by this key changed after app reinstall or clear files
     */
    private static final String PRE_CLIENT_CHANGEABLE_UUID = "pre_client_changeable_uuid";

    /** search history */
    private static final String PRE_SEARCH_HISTORY_KEYWORDS = "pre_search_history_keywords";

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

    /**
     * @return 返回搜索历史记录
     */
    public static List<String> getSearchHistory(Context context) {
        String source = getPreferences(context).getString(PRE_SEARCH_HISTORY_KEYWORDS, "");
        if (TextUtils.isEmpty(source)) return new ArrayList<>(0);
        return Arrays.asList(source.split(","));
    }

    /**
     * 保存搜索历史记录
     *
     * @param context {@link Context}
     * @param history 搜索历史记录
     */
    public static void setSearchHistory(Context context, List<String> history) {
        getPreferences(context).edit().putString(PRE_SEARCH_HISTORY_KEYWORDS,
                CommonUtils.collectionJoinElements(history, ",")).apply();
    }

    private static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

}
