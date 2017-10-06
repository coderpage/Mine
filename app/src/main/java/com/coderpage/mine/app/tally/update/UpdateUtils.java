package com.coderpage.mine.app.tally.update;

import android.content.Context;

import com.coderpage.concurrency.AsyncTaskExecutor;
import com.coderpage.lib.update.ApkModel;
import com.coderpage.lib.update.DefaultVersionComparator;
import com.coderpage.lib.update.Updater;
import com.coderpage.mine.R;

/**
 * @author lc. 2017-10-01 00:29
 * @since 0.5.0
 */

public class UpdateUtils {

    public static void startNewClientVersionCheck(Context context) {
        new Updater.Builder(context, new LatestVersionFetcher())
                .setExecutor(AsyncTaskExecutor.executor())
                .setNotifyIcon(R.mipmap.ic_launcher)
                .create()
                .checkNewVersion(context);
    }

    public static void startNewClientVersionCheckBackground(Context context) {
        new Updater.Builder(context, new LatestVersionFetcher())
                .setExecutor(AsyncTaskExecutor.executor())
                .showCheckProgressDialog(false)
                .showCheckResultToast(false)
                .showApkDownloadConfirmDialog(false)
                .setNotifyIcon(R.mipmap.ic_launcher)
                .create()
                .checkNewVersion(context);
    }

    public static void checkPersistedNewVersionAndShowUpdateConfirmDialog(Context context) {
        ApkModel newVersionApkModelPersisted = Updater.getNewVersionApkModelPersisted(context);
        boolean hasNewVersion = new DefaultVersionComparator()
                .compare(context, newVersionApkModelPersisted);
        if (hasNewVersion) {
            new Updater.Builder(context, new LatestVersionFetcher())
                    .setNotifyIcon(R.mipmap.ic_launcher)
                    .create()
                    .showApkDownloadConfirmDialog(context, newVersionApkModelPersisted);
        }
    }
}
