package com.coderpage.lib.update;

import android.content.Context;

/**
 * @author lc. 2017-09-23 23:44
 * @since 0.5.0
 */

public interface VersionComparator {

    /**
     * 比较 ApkModel 是否是新版本，如果较当前已安装版本新，返回 true，反之，返回 false
     *
     * @param context  {@link Context}
     * @param apkModel {@link ApkModel}
     *
     * @return 如果 ApkModel 较新，返回{@code true}，反之，返回{@code false}
     */
    boolean compare(Context context, ApkModel apkModel);
}
