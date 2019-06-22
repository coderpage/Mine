package com.coderpage.mine.app.tally.worker;

import android.content.Context;
import android.support.annotation.NonNull;

import com.coderpage.base.common.IError;
import com.coderpage.mine.app.tally.module.backup.Backup;
import com.coderpage.mine.app.tally.persistence.preference.SettingPreference;

import java.io.File;

import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * @author lc. 2019-06-22 16:44
 * @since 0.7.0
 *
 * 自动备份任务
 */
public class AutoBackupWorker extends Worker {

    public AutoBackupWorker(Context appContext, WorkerParameters parameters) {
        super(appContext, parameters);
    }

    @NonNull
    @Override
    public Result doWork() {
        // 取消自动备份。取消任务
        if (!SettingPreference.isAutoBackup(getApplicationContext())) {
            WorkManager.getInstance().cancelWorkById(getId());
            return Result.success();
        }

        // 备份数据
        File file = Backup.backupToJsonFileSync(getApplicationContext(), new Backup.BackupProgressListener() {
            @Override
            public void onProgressUpdate(Backup.BackupProgress backupProgress) {

            }

            @Override
            public void success(Void aVoid) {

            }

            @Override
            public void failure(IError iError) {

            }
        });
        return file == null ? Result.failure() : Result.success();
    }
}
