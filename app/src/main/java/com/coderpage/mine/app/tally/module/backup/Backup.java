package com.coderpage.mine.app.tally.module.backup;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Build;
import android.os.RemoteException;

import com.alibaba.fastjson.JSON;
import com.coderpage.base.common.Callback;
import com.coderpage.base.common.IError;
import com.coderpage.base.common.NonThrowError;
import com.coderpage.concurrency.AsyncTaskExecutor;
import com.coderpage.mine.BuildConfig;
import com.coderpage.mine.app.tally.common.error.ErrorCode;
import com.coderpage.mine.app.tally.provider.TallyContract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.coderpage.base.utils.LogUtils.LOGE;
import static com.coderpage.base.utils.LogUtils.makeLogTag;

/**
 * @author abner-l. 2017-06-01
 * @since 0.4.0
 */

public class Backup {

    private static final String TAG = makeLogTag(Backup.class);

    /**
     * 备份过程回调；
     * 回调包括 {@link BackupProgress#READ_DATA} {@link BackupProgress#WRITE_FILE}
     */
    public interface BackupProgressListener extends Callback<Void, IError> {
        void onProgressUpdate(BackupProgress backupProgress);
    }

    public enum BackupProgress {
        // 读取数据
        READ_DATA,
        // 写入备份文件
        WRITE_FILE,
    }

    /**
     * 恢复文件过程回调；
     */
    public interface RestoreProgressListener extends Callback<BackupModel, IError> {
        void onProgressUpdate(RestoreProgress restoreProgress);
    }

    public enum RestoreProgress {
        // 读取文件
        READ_FILE,
        // 检查文件格式
        CHECK_FILE_FORMAT,
        // 恢复文件到数据库
        RESTORE_TO_DB
    }

    /**
     * 备份消费记录到 JSON 文件中；
     *
     * @param context  {@link Context}
     * @param listener 备份回调
     */
    public static void backupToJsonFile(Context context, BackupProgressListener listener) {
        AsyncTaskExecutor.execute(() -> {
            listener.onProgressUpdate(BackupProgress.READ_DATA);
            BackupModel backupModel = readData(context);

            listener.onProgressUpdate(BackupProgress.WRITE_FILE);
            new BackupCache(context).backup2JsonFile(backupModel, listener);
        });
    }

    /**
     * 读取备份的 JSON 文件。
     *
     * @param file     {@link File}备份文件
     * @param listener 回调
     */
    public static void readBackupJsonFile(File file, RestoreProgressListener listener) {
        AsyncTaskExecutor.execute(() -> {
            listener.onProgressUpdate(RestoreProgress.READ_FILE);
            if (file == null) {
                listener.failure(new NonThrowError(ErrorCode.INTERNAL_ERR, "File is null"));
                return;
            }
            if (!file.exists()) {
                listener.failure(new NonThrowError(ErrorCode.ILLEGAL_ARGS, "File not exist"));
                return;
            }
            if (file.isDirectory()) {
                listener.failure(new NonThrowError(ErrorCode.ILLEGAL_ARGS, "Illegal file type"));
                return;
            }

            FileInputStream fis;
            try {
                fis = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                LOGE(TAG, "File not found", e);
                listener.failure(new NonThrowError(ErrorCode.ILLEGAL_ARGS, "File not found"));
                return;
            }

            InputStreamReader inputStreamReader = null;
            BufferedReader bufferedReader = null;
            String sourceString = null;
            try {
                inputStreamReader = new InputStreamReader(fis);
                bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                StringBuilder sourceBuilder = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    sourceBuilder.append(line);
                }
                sourceString = sourceBuilder.toString();
            } catch (IOException e) {
                LOGE(TAG, "IO Err", e);
                listener.failure(new NonThrowError(ErrorCode.INTERNAL_ERR, "File io err"));
                return;
            } finally {
                try {
                    bufferedReader.close();
                    inputStreamReader.close();
                } catch (IOException e) {
                    // no-op
                }
            }

            listener.onProgressUpdate(RestoreProgress.CHECK_FILE_FORMAT);
            try {
                BackupModel backupModel = JSON.parseObject(sourceString, BackupModel.class);
                listener.success(backupModel);
            } catch (Exception e) {
                LOGE(TAG, "Parse json err", e);
                listener.failure(new NonThrowError(ErrorCode.INTERNAL_ERR, "not a json file"));
            }
        });
    }

    /**
     * 将备份的数据恢复到数据库
     *
     * @param context     {@link Context}
     * @param backupModel {@link BackupModel} 备份的数据
     * @param listener    恢复到数据库的回调
     */
    public static void restoreDataFromBackupData(Context context,
                                                 BackupModel backupModel,
                                                 RestoreProgressListener listener) {
        AsyncTaskExecutor.execute(() -> {
            listener.onProgressUpdate(RestoreProgress.RESTORE_TO_DB);
            ContentResolver contentResolver = context.getContentResolver();

            // 恢复分类表数据
            List<BackupModelCategory> categoryList = backupModel.getCategoryList();
            if (categoryList != null && !categoryList.isEmpty()) {
                boolean restoreCategoryOk = restoreCategoryTable(contentResolver, categoryList);
                if (!restoreCategoryOk) {
                    listener.failure(new NonThrowError(ErrorCode.SQL_ERR, "恢复分类数据失败"));
                }
            }

            // 恢复消费表数据
            List<BackupModelExpense> expenseList = backupModel.getExpenseList();
            if (expenseList != null && !expenseList.isEmpty()) {
                boolean restoreExpenseOk = restoreExpenseTable(contentResolver, expenseList);
                if (!restoreExpenseOk) {
                    listener.failure(new NonThrowError(ErrorCode.SQL_ERR, "恢复消费数据失败"));
                }
            }

            listener.success(backupModel);
        });
    }

    /**
     * 读取默认备份文件目录中所有的备份文件。
     *
     * @param context {@link Context}
     *
     * @return 默认备份文件存放目录中的所有备份文件
     */
    public static List<File> listBackupFiles(Context context) {
        return new BackupCache(context).listBackupFiles();
    }

    private static boolean restoreCategoryTable(ContentResolver contentResolver,
                                                List<BackupModelCategory> categoryList) {
        ArrayList<ContentProviderOperation> categoryRestoreOps = new ArrayList<>();
        for (BackupModelCategory category : categoryList) {
            ContentValues values = new ContentValues();
            values.put(TallyContract.Category.NAME, category.getName());
            values.put(TallyContract.Category.ICON, category.getIcon());
            ContentProviderOperation cpo = ContentProviderOperation
                    .newInsert(TallyContract.Category.CONTENT_URI)
                    .withValues(values)
                    .build();
            categoryRestoreOps.add(cpo);
        }
        try {
            contentResolver.applyBatch(TallyContract.CONTENT_AUTHORITY, categoryRestoreOps);
            return true;
        } catch (RemoteException e) {
            LOGE(TAG, "恢复数据失败-分类表", e);
        } catch (OperationApplicationException e) {
            LOGE(TAG, "恢复数据失败-分类表", e);
        }
        return false;
    }

    private static boolean restoreExpenseTable(ContentResolver contentResolver,
                                               List<BackupModelExpense> expenseList) {
        Cursor cursor = contentResolver.query(
                TallyContract.Category.CONTENT_URI,
                new String[]{TallyContract.Category._ID, TallyContract.Category.NAME},
                null,
                null,
                null);
        if (cursor == null) {
            LOGE(TAG, "查询分类数据失败");
            return false;
        }
        HashMap<String, Long> getCategoryIdByName = new HashMap<>(cursor.getCount());
        while (cursor.moveToNext()) {
            long categoryId = cursor.getLong(cursor.getColumnIndex(TallyContract.Category._ID));
            String categoryName = cursor.getString(
                    cursor.getColumnIndex(TallyContract.Category.NAME));
            getCategoryIdByName.put(categoryName, categoryId);
        }
        cursor.close();

        ArrayList<ContentProviderOperation> expenseRestoreOps = new ArrayList<>();
        for (BackupModelExpense expense : expenseList) {
            ContentValues values = new ContentValues();
            values.put(TallyContract.Expense.CATEGORY_ID, getCategoryIdByName.get(expense.getCategory()));
            values.put(TallyContract.Expense.CATEGORY, expense.getCategory());
            values.put(TallyContract.Expense.SYNC_ID, expense.getSyncId());
            values.put(TallyContract.Expense.TIME, expense.getTime());
            values.put(TallyContract.Expense.ACCOUNT_ID, expense.getAccountId());
            values.put(TallyContract.Expense.AMOUNT, expense.getAmount());
            values.put(TallyContract.Expense.DESC, expense.getDesc());
            ContentProviderOperation cpo = ContentProviderOperation
                    .newInsert(TallyContract.Expense.CONTENT_URI)
                    .withValues(values)
                    .build();
            expenseRestoreOps.add(cpo);
        }
        try {
            contentResolver.applyBatch(TallyContract.CONTENT_AUTHORITY, expenseRestoreOps);
            return true;
        } catch (RemoteException e) {
            LOGE(TAG, "恢复数据失败-消费记录表", e);
        } catch (OperationApplicationException e) {
            LOGE(TAG, "恢复数据失败-消费记录表", e);
        }
        return false;
    }

    /**
     * 读取数据库数据并格式化为{@link BackupModel}
     *
     * @param context {@link Context}
     *
     * @return 返回从数据库读取的所有数据
     */
    private static BackupModel readData(Context context) {

        List<BackupModelCategory> categoryList = new ArrayList<>();
        List<BackupModelExpense> expenseList = null;
        BackupModelMetadata metadata = new BackupModelMetadata();

        ContentResolver contentResolver = context.getContentResolver();

        Cursor categoryCursor = contentResolver.query(
                TallyContract.Category.CONTENT_URI, null, null, null, null);
        if (categoryCursor != null) {
            while (categoryCursor.moveToNext()) {
                String categoryName = categoryCursor.getString(
                        categoryCursor.getColumnIndex(TallyContract.Category.NAME));
                String icon = categoryCursor.getString(
                        categoryCursor.getColumnIndex(TallyContract.Category.ICON));
                BackupModelCategory category = new BackupModelCategory();
                category.setName(categoryName);
                category.setIcon(icon);
                categoryList.add(category);
            }
            categoryCursor.close();
        }

        Cursor expenseCursor = contentResolver.query(
                TallyContract.Expense.CONTENT_URI, null, null, null, null);
        if (expenseCursor != null) {

            expenseList = new ArrayList<>(expenseCursor.getCount());

            int amountIndex = expenseCursor.getColumnIndex(TallyContract.Expense.AMOUNT);
            int descIndex = expenseCursor.getColumnIndex(TallyContract.Expense.DESC);
            int categoryIndex = expenseCursor.getColumnIndex(TallyContract.Expense.CATEGORY);
            int timeIndex = expenseCursor.getColumnIndex(TallyContract.Expense.TIME);
            int syncIdIndex = expenseCursor.getColumnIndex(TallyContract.Expense.SYNC_ID);
            int accountIdIndex = expenseCursor.getColumnIndex(TallyContract.Expense.ACCOUNT_ID);

            while (expenseCursor.moveToNext()) {

                float amount = expenseCursor.getFloat(amountIndex);
                long time = expenseCursor.getLong(timeIndex);
                long accountId = expenseCursor.getLong(accountIdIndex);
                String syncId = expenseCursor.getString(syncIdIndex);
                String desc = expenseCursor.getString(descIndex);
                String category = expenseCursor.getString(categoryIndex);

                BackupModelExpense expense = new BackupModelExpense();
                expense.setAmount(amount);
                expense.setDesc(desc);
                expense.setCategory(category);
                expense.setTime(time);
                expense.setSyncId(syncId);
                expense.setAccountId(accountId);

                expenseList.add(expense);
            }
            expenseCursor.close();
        }

        metadata.setBackupDate(System.currentTimeMillis());
        metadata.setClientVersion(BuildConfig.VERSION_NAME);
        metadata.setDeviceName(Build.MODEL);
        metadata.setExpenseNumber(expenseList == null ? 0 : expenseList.size());

        BackupModel backupModel = new BackupModel();

        backupModel.setMetadata(metadata);
        backupModel.setCategoryList(categoryList);
        backupModel.setExpenseList(expenseList);

        return backupModel;
    }


}
