package com.coderpage.lib.update;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.concurrent.Executor;

/**
 * @author lc. 2017-09-23 23:50
 * @since 0.5.0
 */

public class Updater {

    private Context context;
    private SourceFetcher sourceFetcher;
    private Executor executor;
    private VersionComparator versionComparator;
    private int notifyIconResId;
    private int dialogStyle;
    private boolean showCheckProgressDialog = true;
    private boolean showCheckResultToast = true;
    private boolean showApkDownloadConfirmDialog = true;

    private Updater(Context context, SourceFetcher sourceFetcher) {
        this.context = context.getApplicationContext();
        this.sourceFetcher = sourceFetcher;
    }

    public void checkNewVersion(Context context) {
        checkNewVersion(context, new NewVersionCheckCallBack() {
        });
    }

    public void checkNewVersion(Context context, NewVersionCheckCallBack callBack) {
        callBack.onCheckStart();
        Context appContext = context.getApplicationContext();
        Dialog progressDialogTemp = null;
        if (showCheckProgressDialog) {
            progressDialogTemp = showCheckProgressDialog(
                    context, R.string.libupdate_alert_on_checking_new_version);
        }
        final Dialog progressDialog = progressDialogTemp;
        AsyncTask<Void, Void, Result<ApkModel, Error>> checkAsyncTask
                = new AsyncTask<Void, Void, Result<ApkModel, Error>>() {
            @Override
            protected Result<ApkModel, Error> doInBackground(Void... params) {
                return sourceFetcher.fetchApkModel();
            }

            @Override
            protected void onPostExecute(Result<ApkModel, Error> result) {
                dismissDialog(progressDialog);
                if (result.isOk()) {
                    PreferenceUtils.refreshNewVersionApkInfo(appContext, result.data());
                    callBack.onCheckFinish(result.data());
                    boolean foundNewVersion = versionComparator.compare(appContext, result.data());
                    if (foundNewVersion) {
                        callBack.onFindNewVersion(result.data());
                    } else {
                        callBack.onAlreadyNewestVersion(result.data());
                    }
                    if (foundNewVersion && showApkDownloadConfirmDialog) {
                        showApkDownloadConfirmDialogForce(context, result.data());
                    }
                    if (!foundNewVersion && showCheckResultToast) {
                        Toast.makeText(appContext,
                                R.string.libupdate_toast_already_latest_version,
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (showCheckResultToast) {
                        String tipMsg = appContext.getString(
                                R.string.libupdate_toast_http_error,
                                String.valueOf(result.error().code()),
                                result.error().message());
                        Toast.makeText(appContext, tipMsg, Toast.LENGTH_SHORT).show();
                    }
                    callBack.onCheckFail(result.error());
                }
            }
        };
        if (executor != null) {
            checkAsyncTask.executeOnExecutor(executor);
        } else {
            checkAsyncTask.execute();
        }
    }


    /**
     * 开始下载 APK 文件
     *
     * @param context  {@link Context}
     * @param apkModel {@link ApkModel}
     */
    public void startDownloadApk(Context context, ApkModel apkModel) {
        String fileName = apkModel.getName() + "_" + apkModel.getVersion()
                + "_" + apkModel.getBuildCode() + ".apk";
        startDownloadApk(context, apkModel, fileName, R.mipmap.ic_updater);
    }

    /**
     * 开始下载 APK 文件
     *
     * @param context         {@link Context}
     * @param apkModel        {@link ApkModel}
     * @param notifyIconResId 下载进度状态栏图标资源ID
     */
    public void startDownloadApk(Context context,
                                 ApkModel apkModel,
                                 @DrawableRes int notifyIconResId) {
        String fileName = apkModel.getName() + "_" + apkModel.getVersion()
                + "_" + apkModel.getBuildCode() + ".apk";
        startDownloadApk(context, apkModel, fileName, notifyIconResId);
    }


    /**
     * 开始下载 APK 文件
     *
     * @param context  {@link Context}
     * @param apkModel {@link ApkModel}
     * @param filename {@link String} 文件名称
     */
    public void startDownloadApk(Context context, ApkModel apkModel, String filename) {
        startDownloadApk(context, apkModel, filename, R.mipmap.ic_updater);
    }

    /**
     * 开始下载 APK 文件
     *
     * @param context         {@link Context}
     * @param apkModel        {@link ApkModel}
     * @param filename        {@link String} 文件名称
     * @param notifyIconResId 下载进度状态栏图标资源ID
     */
    public void startDownloadApk(Context context,
                                 ApkModel apkModel,
                                 String filename,
                                 @DrawableRes int notifyIconResId) {
        if (notifyIconResId == 0) {
            throw new IllegalArgumentException("notifyIconResId must not be 0");
        }
        DownloadService.startDownloadApk(
                context, apkModel.getDownloadUrl(), filename, notifyIconResId);
    }

    /**
     * 获取持久化在本地的最新版本号；
     * 在{@link Updater#checkNewVersion(Context)}成功之后，会在本地保存最新版本的信息
     *
     * @param context {@link Context}
     *
     * @return 获取持久化在本地的最新版本号
     */
    public static String getVersionPersisted(Context context) {
        return PreferenceUtils.getNewVersion(context);
    }

    /**
     * 获取持久化在本地的最新版本的构建号；
     * 在{@link Updater#checkNewVersion(Context)}成功之后，会在本地保存最新版本的信息
     *
     * @param context {@link Context}
     *
     * @return 获取持久化在本地的最新版本的构建号；
     */
    public static long getBuildCodePersisted(Context context) {
        return PreferenceUtils.getNewVersionBuildCode(context);
    }

    /**
     * 获取持久化在本地的最新版本的信息。
     *
     * @param context {@link Context}
     *
     * @return 获取持久化在本地的最新版本的信息。
     */
    public static ApkModel getNewVersionApkModelPersisted(Context context) {
        return PreferenceUtils.restoreApkModel(context);
    }

    /**
     * 是否存在新版本
     *
     * @param context {@link Context}
     *
     * @return 如果存在新版本，返回{@code true}，反之，返回{@code false}
     */
    public static boolean hasNewVersion(Context context) {
        ApkModelImpl apkModel = new ApkModelImpl();
        apkModel.setVersion(getVersionPersisted(context));
        apkModel.setBuildCode(getBuildCodePersisted(context));
        return new DefaultVersionComparator().compare(context, apkModel);
    }

    private AlertDialog showCheckProgressDialog(Context context, @StringRes int text) {
        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.libupdate_widget_version_progress_dialog, null);
        ((TextView) rootView.findViewById(R.id.tvText)).setText(text);
        AlertDialog.Builder builder;
        if (dialogStyle != 0) {
            builder = new AlertDialog.Builder(context, dialogStyle);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        AlertDialog dialog = builder.setView(rootView).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        return dialog;
    }

    private AlertDialog showApkDownloadConfirmDialog(Context context,
                                                     ApkModel apkModel,
                                                     boolean force) {
        if (!PreferenceUtils.isRemindAgain(context) && !force) {
            return null;
        }
        final Context appContext = context.getApplicationContext();
        AlertDialog.Builder builder;
        if (dialogStyle != 0) {
            builder = new AlertDialog.Builder(context, dialogStyle);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        AlertDialog dialog = builder.setCancelable(true)
                .setTitle(context.getString(
                        R.string.libupdate_alert_new_version_title,
                        apkModel.getVersion(),
                        String.valueOf(apkModel.getBuildCode())))
                .setView(R.layout.libupdate_apk_download_content_view)
                .setNegativeButton(R.string.libupdate_alert_cancel, null)
                .setPositiveButton(R.string.libupdate_alert_confirm_download,
                        (dialogInterface, which) -> {
                            if (notifyIconResId != 0) {
                                startDownloadApk(appContext, apkModel, notifyIconResId);
                            } else {
                                startDownloadApk(appContext, apkModel);
                            }
                        }).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        TextView messageTv = (TextView) dialog.findViewById(R.id.tvMessage);
        CheckBox checkbox = (CheckBox) dialog.findViewById(R.id.checkboxDoNotRemindAgain);
        messageTv.setText(apkModel.getChangelog());
        checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferenceUtils.setRemindAgain(appContext, !isChecked);
        });
        if (!NetWorkUtils.isWifiOK(context)) {
            Log.e("TAG", "====" + apkModel.getApkSizeBytes());
            float sizeOfM = apkModel.getApkSizeBytes() / (1024f * 1024f);
            DecimalFormat format = new DecimalFormat(".00");
            String formattedSizeOfM = format.format(sizeOfM);
            TextView wifiTipTv = (TextView) dialog.findViewById(R.id.tvWifiTip);
            wifiTipTv.setVisibility(View.VISIBLE);
            wifiTipTv.setText(context.getResources().getString(R.string.libupdate_alert_without_wifi, formattedSizeOfM));
        }
        return dialog;
    }

    public AlertDialog showApkDownloadConfirmDialog(Context context, ApkModel apkModel) {
        return showApkDownloadConfirmDialog(context, apkModel, false);
    }

    public AlertDialog showApkDownloadConfirmDialogForce(Context context, ApkModel apkModel) {
        return showApkDownloadConfirmDialog(context, apkModel, true);
    }

    private void dismissDialog(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public static class Builder {
        private Context context;
        private SourceFetcher sourceFetcher;
        private Executor executor;
        private VersionComparator versionComparator = new DefaultVersionComparator();
        private int notifyIconResId = R.mipmap.ic_updater;
        private int dialogTheme;
        private boolean showCheckProgressDialog = true;
        private boolean showCheckResultToast = true;
        private boolean showApkDownloadConfirmDialog = true;

        public Builder(Context context, SourceFetcher sourceFetcher) {
            this.context = context;
            this.sourceFetcher = sourceFetcher;
        }

        /**
         * 设置对话框样式
         */
        public Builder setDialogStyle(int dialogTheme) {
            this.dialogTheme = dialogTheme;
            return this;
        }

        /**
         * 设置下载进度状态栏图标资源ID
         */
        public Builder setNotifyIcon(@DrawableRes int notifyIconResId) {
            this.notifyIconResId = notifyIconResId;
            return this;
        }

        /**
         * 设置网络请求使用的线程池；若不设置默认使用 {@link AsyncTask#THREAD_POOL_EXECUTOR}
         */
        public Builder setExecutor(Executor executor) {
            this.executor = executor;
            return this;
        }

        /**
         * 新版本比较器，若不设置默认使用{@link DefaultVersionComparator}
         */
        public Builder setVersionComparator(VersionComparator comparator) {
            this.versionComparator = comparator;
            return this;
        }

        /**
         * 是否显示检查新版的 progress dialog，默认显示
         */
        public Builder showCheckProgressDialog(boolean showCheckProgressDialog) {
            this.showCheckProgressDialog = showCheckProgressDialog;
            return this;
        }

        /**
         * 是否弹出检查新版本结果的 Toast，默认弹出
         */
        public Builder showCheckResultToast(boolean showCheckResultToast) {
            this.showCheckResultToast = showCheckResultToast;
            return this;
        }

        /**
         * 是否显示新版本 APK 现在确认的 Dialog，默认显示；
         * 若设置为{@code true}，在检查到新版后会自动弹出下载确认的 Dialog。
         */
        public Builder showApkDownloadConfirmDialog(boolean showApkDownloadConfirmDialog) {
            this.showApkDownloadConfirmDialog = showApkDownloadConfirmDialog;
            return this;
        }

        public Updater create() {
            Updater updater = new Updater(context, sourceFetcher);
            updater.executor = executor;
            updater.versionComparator = versionComparator;
            updater.notifyIconResId = notifyIconResId;
            updater.dialogStyle = dialogTheme;
            updater.showCheckProgressDialog = showCheckProgressDialog;
            updater.showCheckResultToast = showCheckResultToast;
            updater.showApkDownloadConfirmDialog = showApkDownloadConfirmDialog;
            return updater;
        }
    }

    public abstract class NewVersionCheckCallBack {
        public void onCheckStart() {
        }

        public void onCheckFinish(ApkModel apkModel) {
        }

        public void onFindNewVersion(ApkModel apkModel) {
        }

        public void onAlreadyNewestVersion(ApkModel apkModel) {
        }

        public void onCheckFail(Error error) {
        }
    }
}
