package com.coderpage.mine.app.tally.module.debug;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.support.annotation.NonNull;

import com.coderpage.base.cache.Cache;
import com.coderpage.base.utils.LogUtils;
import com.coderpage.concurrency.MineExecutors;
import com.coderpage.framework.BaseViewModel;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.common.permission.PermissionReqHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * @author lc. 2019-04-28 10:08
 * @since 0.6.0
 */
public class DebugViewModel extends BaseViewModel {

    private static final String TAG = LogUtils.makeLogTag(DebugViewModel.class);

    /** 申请写文件权限 CODE */
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1;
    /** 申请读文件权限 CODE */
    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 2;

    private PermissionReqHandler mPermissionReqHandler;

    public DebugViewModel(Application application) {
        super(application);
    }

    public void onExportDataBaseClick(Activity activity) {
        String[] permissionArray = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        if (mPermissionReqHandler == null) {
            mPermissionReqHandler = new PermissionReqHandler(activity);
        }
        mPermissionReqHandler.requestPermission(false, permissionArray, new PermissionReqHandler.Listener() {
            @Override
            public void onGranted(boolean grantedAll, String[] permissionArray) {
                copyDatabaseFileToSdcard();
            }

            @Override
            public void onDenied(String[] permissionArray) {
                showToastShort(R.string.permission_request_failed_write_external_storage);
            }
        });
    }

    private void copyDatabaseFileToSdcard() {
        MineExecutors.ioExecutor().execute(() -> {
            File oldfile = getApplication().getDatabasePath("sql_tally");
            try {
                int bytesum = 0;
                int byteread = 0;

                String newPath = Cache.getCacheFolder(getApplication()).getAbsolutePath() + "/记账本.db";
                if (oldfile.exists()) {
                    InputStream inStream = new FileInputStream(oldfile);
                    FileOutputStream fs = new FileOutputStream(newPath);
                    byte[] buffer = new byte[1444];
                    int length;
                    while ((byteread = inStream.read(buffer)) != -1) {
                        bytesum += byteread;
                        System.out.println(bytesum);
                        fs.write(buffer, 0, byteread);
                    }
                    inStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                showToastLong("导出异常:" + e.getMessage());
            }
            showToastShort("导出成功");
        });
    }

    ///////////////////////////////////////////////////////////////////////////
    // 生命周期
    ///////////////////////////////////////////////////////////////////////////

    public void onRequestPermissionsResult(Activity activity,
                                           int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (mPermissionReqHandler != null) {
            mPermissionReqHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
