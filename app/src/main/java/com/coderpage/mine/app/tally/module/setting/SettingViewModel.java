package com.coderpage.mine.app.tally.module.setting;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.coderpage.base.cache.Cache;
import com.coderpage.base.utils.LogUtils;
import com.coderpage.base.utils.ResUtils;
import com.coderpage.concurrency.MineExecutors;
import com.coderpage.framework.BaseViewModel;
import com.coderpage.mine.MineApp;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.common.permission.PermissionReqHandler;
import com.coderpage.mine.app.tally.common.share.ShareProxy;
import com.coderpage.mine.app.tally.common.utils.TallyUtils;
import com.coderpage.mine.app.tally.module.backup.BackupFileActivity;
import com.coderpage.mine.app.tally.persistence.model.Record;
import com.coderpage.mine.app.tally.persistence.sql.TallyDatabase;
import com.coderpage.mine.app.tally.utils.TimeUtils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.coderpage.base.utils.LogUtils.LOGE;

/**
 * @author lc. 2019-04-27 09:59
 * @since 0.6.2
 */

public class SettingViewModel extends BaseViewModel {

    private static final String TAG = LogUtils.makeLogTag(SettingViewModel.class);

    /** 处理加载信息 */
    private MutableLiveData<String> mProcessMessage = new MutableLiveData<>();

    private PermissionReqHandler mPermissionReqHandler;

    public SettingViewModel(Application application) {
        super(application);
    }

    LiveData<String> getProcessMessage() {
        return mProcessMessage;
    }

    /** 导出为 CSV 文件 */
    public void onExportDataExcelClick(Activity activity) {
        String[] permissionArray = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        if (mPermissionReqHandler == null) {
            mPermissionReqHandler = new PermissionReqHandler(activity);
        }
        mPermissionReqHandler.requestPermission(false, permissionArray, new PermissionReqHandler.Listener() {
            @Override
            public void onGranted(boolean grantedAll, String[] permissionArray) {
                exportCsv();
            }

            @Override
            public void onDenied(String[] permissionArray) {
                showToastShort(R.string.permission_request_failed_write_external_storage);
            }
        });
    }

    private void exportCsv() {
        mProcessMessage.setValue("");
        MineExecutors.ioExecutor().execute(() -> {
            File exportFile = initExportCsvFile();
            if (exportFile == null) {
                showToastShort("create file failed");
                mProcessMessage.postValue(null);
                return;
            }
            List<Record> records = TallyDatabase.getInstance().recordDao().queryAll();

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(exportFile);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, Charset.forName("utf-8")));
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL.withHeader("UUID", "金额", "分类", "类型", "日期", "时间戳（毫秒）", "备注"));
                for (Record record : records) {
                    String typeStr = "";
                    if (record.getType() == Record.TYPE_INCOME) {
                        typeStr = ResUtils.getString(MineApp.getAppContext(), R.string.tally_income);
                    }
                    if (record.getType() == Record.TYPE_EXPENSE) {
                        typeStr = ResUtils.getString(MineApp.getAppContext(), R.string.tally_expense);
                    }
                    csvPrinter.printRecord(
                            record.getSyncId(),
                            TallyUtils.formatDisplayMoney(record.getAmount()),
                            record.getCategoryName(),
                            typeStr,
                            TimeUtils.getDatePreciseMinute(record.getTime()),
                            record.getTime(),
                            record.getDesc());
                }
                csvPrinter.printRecord();
                csvPrinter.flush();

                new ShareProxy().shareFile(getApplication(), exportFile);
            } catch (Exception e) {
                e.printStackTrace();
                showToastShort("export fail");
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            mProcessMessage.postValue(null);
        });
    }

    private File initExportCsvFile() {
        File cacheFolder = Cache.getCacheFolder(getApplication());
        if (!cacheFolder.exists()) {
            return null;
        }

        String exportFolderPath = cacheFolder.getPath() + File.separator + "csv";
        File exportFolder = new File(exportFolderPath);
        if (!exportFolder.exists()) {
            boolean mkDirOk = exportFolder.mkdir();
            if (!mkDirOk) {
                LOGE(TAG, "create backup cache folder failed");
                return null;
            }
        }

        String fileName = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date()) + ".csv";
        String exportFilePath = exportFolderPath + File.separator + fileName;
        File exportFile = new File(exportFilePath);
        try {
            boolean createFileOk = exportFile.createNewFile();
            if (createFileOk) {
                return exportFile;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 备份数据点击 */
    public void onBackupDataClick(Activity activity) {
        activity.startActivity(new Intent(activity, BackupFileActivity.class));
    }

    ///////////////////////////////////////////////////////////////////////////
    // 生命周期
    ///////////////////////////////////////////////////////////////////////////

    void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

    }

    void onRequestPermissionsResult(Activity activity,
                                    int requestCode,
                                    @NonNull String[] permissions,
                                    @NonNull int[] grantResults) {
        if (mPermissionReqHandler != null) {
            mPermissionReqHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
