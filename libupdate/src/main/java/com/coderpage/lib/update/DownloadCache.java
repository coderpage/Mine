package com.coderpage.lib.update;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * @author lc. 2017-09-23 23:50
 * @since 0.5.0
 */

public class DownloadCache {
    private static final String TAG = DownloadCache.class.getSimpleName();

    /** SD卡 目录 */
    private static String SD_CARD_ROOT_PATH = Environment.getExternalStorageDirectory().getPath();
    /** 缓存文件夹名称 */
    private static String CACHE_FOLDER_NAME = "cache";
    /** 下载目录 */
    private static final String DOWNLOAD_FOLDER_NAME = "download";
    private static String DATA_ROOT_PATH = null;

    private Context mContext;
    private File downloadFolder;
    private String downloadFolderPath = DATA_ROOT_PATH + File.separator + DOWNLOAD_FOLDER_NAME;

    public DownloadCache(Context context) {
        mContext = context;
        DATA_ROOT_PATH = context.getCacheDir().getPath();
        initDownloadFolder();
    }

    /**
     * 获取应用缓存文件夹
     */
    private synchronized File getCacheFolder(Context context) {
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
                Log.e(TAG, "create cache folder failed.");
            }
        }
        return cacheFolder;
    }

    private synchronized boolean initDownloadFolder() {
        if (downloadFolder != null && downloadFolder.exists()) {
            return true;
        }

        File cacheRoot = getCacheFolder(mContext);
        if (!cacheRoot.exists()) {
            return false;
        }

        downloadFolderPath = cacheRoot.getPath() + File.separator + DOWNLOAD_FOLDER_NAME;
        downloadFolder = new File(downloadFolderPath);
        if (downloadFolder.exists()) {
            return true;
        }
        boolean mkDirOk = downloadFolder.mkdir();
        if (!mkDirOk) {
            Log.e(TAG, "create download cache folder failed");
        }
        return mkDirOk;
    }

    public String getDownloadFolderPath() {
        return downloadFolderPath;
    }

    public File saveFile(String fileName, BufferedSource source) {
        File file = new File(downloadFolderPath, fileName);
        if (!createFileIfNotExists(file)) {
            return null;
        }

        boolean saveFileOk = true;
        try {
            // http://stackoverflow.com/questions/25893030/download-binary-file-from-okhttp
            BufferedSink sink = Okio.buffer(Okio.sink(file));
            sink.writeAll(source);
            sink.close();
        } catch (IOException e) {
            saveFileOk = false;
            Log.e(TAG, "save file error:" + e.getMessage());
        }
        if (saveFileOk) {
            return file;
        }
        return null;
    }

    private boolean createFileIfNotExists(File file) {
        if (file == null) {
            return false;
        }
        boolean createFileOk = true;
        if (!file.exists()) {
            try {
                createFileOk = file.createNewFile();
            } catch (IOException e) {
                createFileOk = false;
                Log.e(TAG, "create file error:" + e.getMessage());
            }
        }
        return createFileOk;
    }

    public boolean deleteAllDownloadFile() {
        File imageFolder = new File(downloadFolderPath);
        if (!imageFolder.exists()) {
            return true;
        }
        if (imageFolder.isDirectory()) {
            String[] imageFileList = imageFolder.list();
            for (int i = 0; i < imageFileList.length; i++) {
                new File(imageFolder, imageFileList[i]).delete();
            }
        }
        return true;
    }
}
