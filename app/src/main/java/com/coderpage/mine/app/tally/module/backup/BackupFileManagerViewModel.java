package com.coderpage.mine.app.tally.module.backup;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.DialogInterface;

import com.coderpage.base.utils.ArrayUtils;
import com.coderpage.base.utils.ResUtils;
import com.coderpage.concurrency.MineExecutors;
import com.coderpage.framework.BaseViewModel;
import com.coderpage.framework.ViewReliedTask;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.common.permission.PermissionReqHandler;
import com.coderpage.mine.app.tally.ui.dialog.PermissionReqDialog;
import com.coderpage.mine.utils.AndroidUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author lc. 2019-05-19 17:11
 * @since 0.6.2
 */

public class BackupFileManagerViewModel extends BaseViewModel implements LifecycleObserver {

    private SimpleDateFormat mFileTimeDateFormat;
    private MutableLiveData<List<BackupFileManagerItem>> mBackupFileList = new MutableLiveData<>();
    private MutableLiveData<ViewReliedTask<Activity>> mViewReliedTask = new MutableLiveData<>();

    private PermissionReqHandler mPermissionReqHandler;

    public BackupFileManagerViewModel(Application application) {
        super(application);
        mFileTimeDateFormat = new SimpleDateFormat(
                ResUtils.getString(application, R.string.date_format_y_m_d_hh_mm), Locale.getDefault());
    }

    LiveData<List<BackupFileManagerItem>> getBackupFileList() {
        return mBackupFileList;
    }

    LiveData<ViewReliedTask<Activity>> getViewReliedTask() {
        return mViewReliedTask;
    }

    public void onItemDeleteClick(BackupFileManagerItem item) {
        deleteBackupFile(item);
    }

    /** 格式化文件时间 */
    public synchronized String formatBackupTime(BackupFileManagerItem item) {
        if (item == null || item.getFile() == null) {
            return "";
        }
        return mFileTimeDateFormat.format(new Date(item.getCreateTime()));
    }

    /** 格式化文件大小 */
    public String formatBackupFileSize(BackupFileManagerItem item) {
        if (item == null || item.getFile() == null) {
            return "0 KB";
        }

        long size = item.getSize();
        double sizeKB = size / 1024D;
        double sizeM = sizeKB / 1024D;
        if (sizeM > 1) {
            return new DecimalFormat("0.00M").format(sizeM);
        }
        if (sizeKB > 1) {
            return new DecimalFormat("0.00KB").format(sizeKB);
        }
        return size + "B";
    }

    /** 刷新列表数据 */
    private void refreshData() {
        mViewReliedTask.setValue(activity -> {
            mPermissionReqHandler.requestPermission(true,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    new PermissionReqHandler.Listener() {
                        @Override
                        public void onGranted(boolean grantedAll, String[] permissionArray) {
                            MineExecutors.ioExecutor().execute(() -> {
                                List<File> fileList = Backup.listBackupFiles(getApplication());
                                List<BackupFileManagerItem> resultList = new ArrayList<>(fileList.size());
                                ArrayUtils.forEach(fileList, (count, index, item) -> resultList.add(new BackupFileManagerItem(item)));
                                Collections.sort(resultList, (o1, o2) -> {
                                    if (o1.getCreateTime() == o2.getCreateTime()) {
                                        return 0;
                                    }
                                    return o1.getCreateTime() > o2.getCreateTime() ? -1 : 1;
                                });
                                mBackupFileList.postValue(resultList);
                            });
                        }

                        @Override
                        public void onDenied(String[] permissionArray) {
                            showToastShort(R.string.permission_request_failed_read_external_storage);
                            // 读存储权限 写存储权限，显示一条即可
                            List<String> permissionList = new ArrayList<>(Arrays.asList(permissionArray));
                            if (permissionList.contains(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                                    && permissionList.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                permissionList.remove(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                            }
                            new PermissionReqDialog(activity, permissionList)
                                    .setTitleText(ResUtils.getString(activity, R.string.permission_req_title_format, ResUtils.getString(activity, R.string.app_name)))
                                    .setPositiveText(ResUtils.getString(activity, R.string.permission_req_go_open))
                                    .setListener(new PermissionReqDialog.Listener() {
                                        @Override
                                        public void onCancelClick(DialogInterface dialog) {
                                            dialog.dismiss();
                                        }

                                        @Override
                                        public void onConfirmClick(DialogInterface dialog) {
                                            dialog.dismiss();
                                            AndroidUtils.openAppSettingPage(activity);
                                        }
                                    })
                                    .show();
                        }
                    });
        });
    }

    /**
     * 删除备份文件
     *
     * @param item 文件ITEM
     */
    private void deleteBackupFile(BackupFileManagerItem item) {
        File file = item.getFile();
        if (file == null || !file.exists()) {
            return;
        }

        boolean deleted = file.delete();
        if (deleted) {
            List<BackupFileManagerItem> currentList = mBackupFileList.getValue();
            ArrayUtils.remove(currentList, item1 -> item1 == item);
            mBackupFileList.setValue(currentList);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate(LifecycleOwner owner) {
        Activity activity = (Activity) owner;
        mPermissionReqHandler = new PermissionReqHandler(activity);
        refreshData();
    }
}
