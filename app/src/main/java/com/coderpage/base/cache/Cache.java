package com.coderpage.base.cache;

import android.content.Context;
import android.os.Environment;

import java.io.File;

import static com.coderpage.base.utils.LogUtils.LOGE;
import static com.coderpage.base.utils.LogUtils.makeLogTag;

/**
 * @author abner-l. 2017-06-01
 */

public class Cache {

    private static final String TAG = makeLogTag(Cache.class);

    /** SD卡 目录 */
    private static String SD_CARD_ROOT_PATH = Environment.getExternalStorageDirectory().getPath();
    /** 手机缓存目录 */
    private static String DATA_ROOT_PATH = null;
    /** 缓存文件夹名称 */
    private static String CACHE_FOLDER_NAME = "MineApp";

    /**
     * 获取应用缓存文件夹
     */
    public static synchronized File getCacheFolder(Context context) {
        CACHE_FOLDER_NAME = context.getPackageName();
        DATA_ROOT_PATH = context.getCacheDir().getPath();
        // 获取手机缓存或SD卡路径
        String cacheRootPath = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
                SD_CARD_ROOT_PATH : DATA_ROOT_PATH;
        // 创建应用缓存目录
        String cachePath = cacheRootPath + "/" + CACHE_FOLDER_NAME;
        File cacheFolder = new File(cachePath);
        if (!cacheFolder.exists()) {
            boolean mkDirOk = cacheFolder.mkdir();
            if (!mkDirOk) {
                LOGE(TAG, "create cache folder failed");
            }
        }
        return cacheFolder;
    }

}
