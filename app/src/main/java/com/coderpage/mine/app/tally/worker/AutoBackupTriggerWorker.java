package com.coderpage.mine.app.tally.worker;

import android.content.Context;
import android.support.annotation.NonNull;

import com.coderpage.mine.app.tally.persistence.preference.SettingPreference;

import java.util.concurrent.TimeUnit;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * @author lc. 2019-06-22 19:14
 * @since 0.7.0
 */
public class AutoBackupTriggerWorker extends Worker {

    public AutoBackupTriggerWorker(Context context, WorkerParameters parameters) {
        super(context, parameters);
    }

    @NonNull
    @Override
    public Result doWork() {
        if (SettingPreference.isAutoBackup(getApplicationContext())) {
            PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(AutoBackupWorker.class, 1, TimeUnit.DAYS)
                    .addTag(WorkerConst.UNIQUE_NAME_AUTO_BACKUP_WORKER)
                    .build();
            WorkManager.getInstance().enqueueUniquePeriodicWork(
                    WorkerConst.UNIQUE_NAME_AUTO_BACKUP_WORKER,
                    ExistingPeriodicWorkPolicy.KEEP, request);
        }
        return Result.success();
    }
}
