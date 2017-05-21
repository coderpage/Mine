package com.coderpage.mine.app.tally.provider;

import android.net.Uri;

import com.coderpage.utils.LogUtils;

import java.util.List;

import static com.coderpage.utils.LogUtils.LOGE;

/**
 * @author abner-l. 2017-04-15
 */

public class ProviderUtils {
    private static final String TAG = LogUtils.makeLogTag(ProviderUtils.class);

    public static long parseIdFromUri(Uri uri) {
        long cid = -1;
        if (uri == null) {
            return cid;
        }
        List<String> pathSegments = uri.getPathSegments();
        if (pathSegments.size() == 0) {
            return cid;
        }
        String cidStr = pathSegments.get(pathSegments.size() - 1);
        try {
            cid = Long.parseLong(cidStr);
        } catch (NumberFormatException e) {
            LOGE(TAG, "解析记录 ID 失败", e);
        }
        return cid;
    }
}
