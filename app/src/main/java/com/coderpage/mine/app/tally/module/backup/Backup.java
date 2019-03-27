package com.coderpage.mine.app.tally.module.backup;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.coderpage.base.common.Callback;
import com.coderpage.base.common.IError;
import com.coderpage.base.common.NonThrowError;
import com.coderpage.concurrency.AsyncTaskExecutor;
import com.coderpage.mine.BuildConfig;
import com.coderpage.mine.app.tally.common.error.ErrorCode;
import com.coderpage.mine.app.tally.persistence.model.CategoryModel;
import com.coderpage.mine.app.tally.persistence.model.Record;
import com.coderpage.mine.app.tally.persistence.sql.TallyDatabase;
import com.coderpage.mine.app.tally.persistence.sql.dao.CategoryDao;
import com.coderpage.mine.app.tally.persistence.sql.entity.CategoryEntity;
import com.coderpage.mine.app.tally.persistence.sql.entity.RecordEntity;

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
        /**
         * 回到
         *
         * @param backupProgress progress
         */
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
        /**
         * 更新回调
         *
         * @param restoreProgress progress
         */
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
            BackupModel backupModel = readData();

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

            BackupModelMetadata metadata = backupModel.getMetadata();
            // 恢复分类表数据
            List<BackupModelCategory> categoryList = backupModel.getCategoryList();
            if (categoryList != null && !categoryList.isEmpty()) {
                boolean restoreCategoryOk = restoreCategoryTable(metadata, categoryList);
                if (!restoreCategoryOk) {
                    listener.failure(new NonThrowError(ErrorCode.SQL_ERR, "恢复分类数据失败"));
                }
            }

            // 恢复消费表数据
            List<BackupModelRecord> expenseList = backupModel.getExpenseList();
            if (expenseList != null && !expenseList.isEmpty()) {
                boolean restoreExpenseOk = restoreExpenseTable(metadata, expenseList);
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
     * @return 默认备份文件存放目录中的所有备份文件
     */
    public static List<File> listBackupFiles(Context context) {
        return new BackupCache(context).listBackupFiles();
    }

    private static boolean restoreCategoryTable(BackupModelMetadata metadata,
                                                List<BackupModelCategory> categoryList) {
        CategoryDao categoryDao = TallyDatabase.getInstance().categoryDao();

        CategoryEntity[] insertArray = new CategoryEntity[categoryList.size()];
        for (int i = 0; i < categoryList.size(); i++) {
            BackupModelCategory backupCategory = categoryList.get(i);
            CategoryEntity entity = new CategoryEntity();
            entity.setName(backupCategory.getName());
            entity.setIcon(backupCategory.getIcon());
            entity.setAccountId(backupCategory.getAccountId());
            entity.setSyncStatus(backupCategory.getSyncStatus());
            // 0.6.0 版本之前没有 type 之分，全部为支出分类类型
            entity.setType(metadata.getClientVersionCode() < 60 ?
                    CategoryEntity.TYPE_EXPENSE : backupCategory.getType());
            // 0.6.0 版本之前没有 uniqueCategoryName，全部统一使用 category icon
            entity.setUniqueName(TextUtils.isEmpty(backupCategory.getUniqueName()) ?
                    backupCategory.getIcon() : backupCategory.getUniqueName());

            insertArray[i] = entity;
        }

        try {
            categoryDao.insert(insertArray);
            return true;
        } catch (Exception e) {
            LOGE(TAG, "恢复数据失败-分类表", e);
        }

        return false;
    }

    private static boolean restoreExpenseTable(BackupModelMetadata metadata,
                                               List<BackupModelRecord> expenseList) {
        TallyDatabase database = TallyDatabase.getInstance();
        List<CategoryModel> categoryList = database.categoryDao().allCategory();

        // categoryName - categoryUniqueName Map
        HashMap<String, String> getCategoryUniqueNameByName = new HashMap<>();

        for (CategoryModel category : categoryList) {
            getCategoryUniqueNameByName.put(category.getName(), category.getUniqueName());
        }

        RecordEntity[] insertArray = new RecordEntity[expenseList.size()];
        for (int i = 0; i < expenseList.size(); i++) {
            BackupModelRecord backupExpense = expenseList.get(i);

            // 从 0.6.0 版本之后，使用 categoryUniqueName 来保证分类唯一
            String categoryUniqueName = backupExpense.getCategoryUniqueName();
            // 如果备份数据中记录了 categoryUniqueName，使用 categoryUniqueName
            if (TextUtils.isEmpty(categoryUniqueName)) {
                categoryUniqueName = getCategoryUniqueNameByName.get(backupExpense.getCategory());
                categoryUniqueName = TextUtils.isEmpty(categoryUniqueName) ? "" : categoryUniqueName;
            }

            RecordEntity entity = new RecordEntity();
            entity.setAccountId(backupExpense.getAccountId());
            entity.setAmount(backupExpense.getAmount());
            entity.setTime(backupExpense.getTime());
            entity.setCategoryUniqueName(categoryUniqueName);
            entity.setDesc(backupExpense.getDesc());
            entity.setSyncId(backupExpense.getSyncId());
            entity.setSyncStatus(backupExpense.getSyncStatus());
            entity.setType(backupExpense.getType());

            insertArray[i] = entity;
        }

        try {
            database.recordDao().insert(insertArray);
            return true;
        } catch (Exception e) {
            LOGE(TAG, "恢复数据失败-消费记录表", e);
        }

        return false;
    }

    /**
     * 读取数据库数据并格式化为{@link BackupModel}
     *
     * @return 返回从数据库读取的所有数据
     */
    private static BackupModel readData() {

        List<BackupModelCategory> categoryList = new ArrayList<>();
        List<BackupModelRecord> recordList = null;
        BackupModelMetadata metadata = new BackupModelMetadata();

        TallyDatabase database = TallyDatabase.getInstance();

        List<CategoryModel> categoryEntityList = database.categoryDao().allCategory();
        for (CategoryModel entity : categoryEntityList) {
            BackupModelCategory category = new BackupModelCategory();
            category.setName(entity.getName());
            category.setUniqueName(entity.getUniqueName());
            category.setIcon(entity.getIcon());
            category.setAccountId(entity.getAccountId());
            category.setType(entity.getType());
            category.setSyncStatus(entity.getSyncStatus());

            categoryList.add(category);
        }

        List<Record> recordEntityList = database.recordDao().queryAll();
        recordList = new ArrayList<>(recordEntityList.size());
        for (Record entity : recordEntityList) {
            BackupModelRecord expense = new BackupModelRecord();
            expense.setAmount(entity.getAmount());
            expense.setDesc(entity.getDesc());
            expense.setCategory(entity.getCategoryName());
            expense.setTime(entity.getTime());
            expense.setSyncId(entity.getSyncId());
            expense.setAccountId(entity.getAccountId());
            expense.setSyncStatus(entity.getSyncStatus());
            expense.setCategoryUniqueName(entity.getCategoryUniqueName());
            expense.setType(entity.getType());

            recordList.add(expense);
        }

        metadata.setBackupDate(System.currentTimeMillis());
        metadata.setClientVersion(BuildConfig.VERSION_NAME);
        metadata.setDeviceName(Build.MODEL);
        metadata.setExpenseNumber(recordList.size());

        BackupModel backupModel = new BackupModel();

        backupModel.setMetadata(metadata);
        backupModel.setCategoryList(categoryList);
        backupModel.setExpenseList(recordList);

        return backupModel;
    }


}
