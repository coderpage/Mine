package com.coderpage.mine.app.tally.module.edit.record;

import com.coderpage.base.common.IError;
import com.coderpage.base.common.Result;
import com.coderpage.base.common.SimpleCallback;
import com.coderpage.concurrency.MineExecutors;
import com.coderpage.mine.app.tally.common.RecordType;
import com.coderpage.mine.app.tally.persistence.model.CategoryModel;
import com.coderpage.mine.app.tally.persistence.model.Record;
import com.coderpage.mine.app.tally.persistence.sql.TallyDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lc. 2018-08-29 20:34
 * @since 0.6.0
 */

class RecordRepository {

    private TallyDatabase mDataBase;

    RecordRepository() {
        mDataBase = TallyDatabase.getInstance();
    }

    /** 查询所有支付分类 */
    void queryAllCategory(RecordType type, SimpleCallback<List<CategoryModel>> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            if (type == RecordType.EXPENSE) {
                List<CategoryModel> categoryList = mDataBase.categoryDao().allExpenseCategory();
                MineExecutors.executeOnUiThread(() -> callback.success(categoryList));
                return;
            }
            if (type == RecordType.INCOME) {
                List<CategoryModel> categoryList = mDataBase.categoryDao().allIncomeCategory();
                MineExecutors.executeOnUiThread(() -> callback.success(categoryList));
                return;
            }
            MineExecutors.executeOnUiThread(() -> callback.success(new ArrayList<>()));
        });
    }

    /** 通过 ID 查询记录 */
    void queryRecordById(long recordId, SimpleCallback<Record> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            Record record = mDataBase.recordDao().queryById(recordId);
            MineExecutors.executeOnUiThread(() -> callback.success(record));
        });
    }

    /** 保存记录 */
    void saveRecord(Record record, SimpleCallback<Result<Long, IError>> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            long id = mDataBase.recordDao().insert(record.createEntity());
            MineExecutors.executeOnUiThread(() -> callback.success(new Result<>(id, null)));
        });
    }

    /** 更新记录 */
    void updateExpense(Record record, SimpleCallback<Result<Long, IError>> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            long id = mDataBase.recordDao().update(record.createEntity());
            MineExecutors.executeOnUiThread(() -> callback.success(new Result<>(id, null)));
        });
    }
}
