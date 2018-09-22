package com.coderpage.lib.update;

/**
 * @author lc. 2017-09-23 23:49
 * @since 0.5.0
 */

public interface SourceFetcher {

    /**
     * 从服务器获取最新 APK 信息；会在一个工作线程中异步执行。
     * 由于获取 APK 信息会连接网络，因此应该返回可能出现的错误信息；
     *
     * @return 返回获取最新的 APK 信息的结果；
     */
    Result<ApkModel, Error> fetchApkModel();

}
