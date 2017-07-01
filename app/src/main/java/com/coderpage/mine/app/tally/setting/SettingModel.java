package com.coderpage.mine.app.tally.setting;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.coderpage.common.IError;
import com.coderpage.framework.Model;
import com.coderpage.framework.QueryEnum;
import com.coderpage.framework.UserActionEnum;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.backup.Backup;
import com.coderpage.mine.app.tally.backup.BackupModel;
import com.coderpage.utils.ResUtils;

import java.io.File;

/**
 * @author abner-l. 2017-06-18
 * @since 0.4.0
 */

class SettingModel implements Model<SettingModel.SettingQueryEnum,
        SettingModel.SettingUserActionEnum> {

    static final String EXTRA_ACTION_CODE = "extra_action_code";
    static final String EXTRA_MESSAGE = "extra_message";
    static final String EXTRA_FILE_PATH = "extra_file_path";

    static final int ACTION_CODE_UPDATE = 1;
    static final int ACTION_CODE_FINISH = 2;

    private Context mContext;
    private Handler mHandler;
    private BackupModel mBackupModel;

    SettingModel(Context context) {
        mContext = context.getApplicationContext();
        mHandler = new Handler(Looper.getMainLooper());
    }

    BackupModel getBackupModel() {
        return mBackupModel;
    }

    @Override
    public SettingQueryEnum[] getQueries() {
        return SettingQueryEnum.values();
    }

    @Override
    public SettingUserActionEnum[] getUserActions() {
        return SettingUserActionEnum.values();
    }

    @Override
    public void requestData(SettingQueryEnum query, DataQueryCallback callback) {

    }

    @Override
    public void deliverUserAction(SettingUserActionEnum action,
                                  @Nullable Bundle args,
                                  UserActionCallback callback) {
        switch (action) {
            case BACKUP_TO_JSON_FILE:
                backup2JsonFile(mContext, action, args, callback);
                break;
            case READ_BACKUP_JSON_FILE:
                if (args == null || !args.containsKey(EXTRA_FILE_PATH)) {
                    throw new IllegalArgumentException("缺少参数: " + EXTRA_FILE_PATH);
                }
                String filePath = args.getString(EXTRA_FILE_PATH, "");
                readDataFromBackupJsonFile(mContext, filePath, action, args, callback);
                break;
            case RESTORE_TO_DB_WITH_BACKUP_MODEL:
                restoreToDbFromBackupModel(mContext, mBackupModel, action, args, callback);
                break;
        }
    }

    private void backup2JsonFile(Context context,
                                 SettingUserActionEnum action,
                                 Bundle args,
                                 UserActionCallback callback) {
        Backup.backupToJsonFile(context, new Backup.BackupProgressListener() {
            @Override
            public void onProgressUpdate(Backup.BackupProgress backupProgress) {
                switch (backupProgress) {
                    case READ_DATA:
                        mHandler.post(() -> {
                            args.putInt(EXTRA_ACTION_CODE, ACTION_CODE_UPDATE);
                            args.putString(EXTRA_MESSAGE,
                                    ResUtils.getString(context, R.string.tally_alert_reading_db_data));
                            callback.onModelUpdated(SettingModel.this, action);
                        });
                        break;
                    case WRITE_FILE:
                        mHandler.post(() -> {
                            args.putInt(EXTRA_ACTION_CODE, ACTION_CODE_UPDATE);
                            args.putString(EXTRA_MESSAGE,
                                    ResUtils.getString(context, R.string.tally_alert_write_data_2_file));
                            callback.onModelUpdated(SettingModel.this, action);
                        });
                        break;
                }
            }

            @Override
            public void success(Void aVoid) {
                mHandler.post(() -> {
                    args.putInt(EXTRA_ACTION_CODE, ACTION_CODE_FINISH);
                    args.putString(EXTRA_MESSAGE,
                            ResUtils.getString(context, R.string.tally_alert_backup_success));
                    callback.onModelUpdated(SettingModel.this, action);
                });
            }

            @Override
            public void failure(IError iError) {
                mHandler.post(() ->
                        callback.onError(action));
            }
        });
    }

    private void readDataFromBackupJsonFile(Context context,
                                            String filePath,
                                            SettingUserActionEnum action,
                                            Bundle args,
                                            UserActionCallback callback) {
        File file = new File(filePath);
        Backup.readBackupJsonFile(file, new Backup.RestoreProgressListener() {
            @Override
            public void onProgressUpdate(Backup.RestoreProgress restoreProgress) {
                switch (restoreProgress) {
                    case READ_FILE:
                        mHandler.post(() -> {
                            args.putInt(EXTRA_ACTION_CODE, ACTION_CODE_UPDATE);
                            args.putString(EXTRA_MESSAGE,
                                    ResUtils.getString(context, R.string.tally_alert_reading_db_data));
                            callback.onModelUpdated(SettingModel.this, action);
                        });
                        break;
                    case CHECK_FILE_FORMAT:
                        mHandler.post(() -> {
                            args.putInt(EXTRA_ACTION_CODE, ACTION_CODE_UPDATE);
                            args.putString(EXTRA_MESSAGE,
                                    ResUtils.getString(context, R.string.tally_alert_check_data_format));
                            callback.onModelUpdated(SettingModel.this, action);
                        });
                        break;
                    case RESTORE_TO_DB:
                        mHandler.post(() -> {
                            args.putInt(EXTRA_ACTION_CODE, ACTION_CODE_UPDATE);
                            args.putString(EXTRA_MESSAGE,
                                    ResUtils.getString(context, R.string.tally_alert_restore_data));
                            callback.onModelUpdated(SettingModel.this, action);
                        });
                        break;
                }
            }

            @Override
            public void success(BackupModel backupModel) {
                mBackupModel = backupModel;
                mHandler.post(() -> {
                    args.putInt(EXTRA_ACTION_CODE, ACTION_CODE_FINISH);
                    args.putString(EXTRA_MESSAGE,
                            ResUtils.getString(context, R.string.tally_alert_read_data_success));
                    callback.onModelUpdated(SettingModel.this, action);
                });
            }

            @Override
            public void failure(IError iError) {
                mHandler.post(() ->
                        callback.onError(action));
            }
        });
    }

    private void restoreToDbFromBackupModel(Context context,
                                            BackupModel backupModel,
                                            SettingUserActionEnum action,
                                            @Nullable Bundle args,
                                            UserActionCallback callback) {
        Backup.restoreDataFromBackupData(context, backupModel, new Backup.RestoreProgressListener() {
            @Override
            public void onProgressUpdate(Backup.RestoreProgress restoreProgress) {
                switch (restoreProgress) {
                    case READ_FILE:
                        mHandler.post(() -> {
                            args.putInt(EXTRA_ACTION_CODE, ACTION_CODE_UPDATE);
                            args.putString(EXTRA_MESSAGE,
                                    ResUtils.getString(context, R.string.tally_alert_reading_db_data));
                            callback.onModelUpdated(SettingModel.this, action);
                        });
                        break;
                    case CHECK_FILE_FORMAT:
                        mHandler.post(() -> {
                            args.putInt(EXTRA_ACTION_CODE, ACTION_CODE_UPDATE);
                            args.putString(EXTRA_MESSAGE,
                                    ResUtils.getString(context, R.string.tally_alert_check_data_format));
                            callback.onModelUpdated(SettingModel.this, action);
                        });
                        break;
                    case RESTORE_TO_DB:
                        mHandler.post(() -> {
                            args.putInt(EXTRA_ACTION_CODE, ACTION_CODE_UPDATE);
                            args.putString(EXTRA_MESSAGE,
                                    ResUtils.getString(context, R.string.tally_alert_restore_data));

                            callback.onModelUpdated(SettingModel.this, action);
                        });
                        break;
                }
            }

            @Override
            public void success(BackupModel backupModel) {
                mBackupModel = backupModel;
                mHandler.post(() -> {
                    args.putInt(EXTRA_ACTION_CODE, ACTION_CODE_FINISH);
                    args.putString(EXTRA_MESSAGE,
                            ResUtils.getString(context, R.string.tally_alert_read_data_success));
                    callback.onModelUpdated(SettingModel.this, action);
                });
                mBackupModel = null;
            }

            @Override
            public void failure(IError iError) {
                mBackupModel = null;
                mHandler.post(() -> callback.onError(action));
            }
        });
    }

    @Override
    public void cleanUp() {
        mBackupModel = null;
    }

    enum SettingQueryEnum implements QueryEnum {
        ;
        private int id;
        private String[] projection;

        public int getId() {
            return id;
        }

        public String[] getProjection() {
            return projection;
        }

        SettingQueryEnum(int id, String[] projection) {
            this.id = id;
            this.projection = projection;
        }
    }

    enum SettingUserActionEnum implements UserActionEnum {
        /**
         * 备份到 JSON 文件
         */
        BACKUP_TO_JSON_FILE(1),
        /**
         * 从 JSON 备份文件读取数据
         */
        READ_BACKUP_JSON_FILE(2),
        /**
         * 从 JSON 备份文件中恢复数据到数据库
         */
        RESTORE_TO_DB_WITH_BACKUP_MODEL(3);

        private int id;

        @Override
        public int getId() {
            return id;
        }

        SettingUserActionEnum(int id) {
            this.id = id;
        }
    }
}
