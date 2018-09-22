package com.coderpage.lib.update;

/**
 * @author lc. 2017-09-23 23:42
 * @since 0.5.0
 */

public interface ApkModel {
    /**
     * 返回 APK 编译构建号
     */
    long getBuildCode();

    /**
     * 返回 APK 版本号
     */
    String getVersion();

    /**
     * 获取应用名称
     */
    String getName();

    /**
     * 返回 APK 更新日志
     */
    String getChangelog();

    /**
     * 返回 APK 下载地址
     */
    String getDownloadUrl();

    /**
     * 返回 APK 包大小
     */
    long getApkSizeBytes();
}
