package com.coderpage.mine.app.tally.module.edit.income;

import com.coderpage.base.common.IError;
import com.coderpage.base.common.Result;
import com.coderpage.base.common.SimpleCallback;
import com.coderpage.concurrency.MineExecutors;
import com.coderpage.mine.app.tally.persistence.model.CategoryModel;
import com.coderpage.mine.app.tally.persistence.model.Record;
import com.coderpage.mine.app.tally.persistence.sql.TallyDatabase;

import java.util.List;

/**
 * @author lc. 2018-09-18 23:12
 * @since 0.6.0
 */

class IncomeRepository {

    private TallyDatabase mDataBase;

    IncomeRepository() {
        mDataBase = TallyDatabase.getInstance();
    }

    /** 查询所有收入分类 */
    void queryAllCategory(SimpleCallback<List<CategoryModel>> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            List<CategoryModel> categoryList = mDataBase.categoryDao().allIncomeCategory();
            MineExecutors.executeOnUiThread(() -> callback.success(categoryList));
        });
    }

    /** 通过 ID 查询收入记录 */
    void queryIncomeById(long expenseId, SimpleCallback<Record> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            Record income = mDataBase.incomeDao().queryById(expenseId);
            MineExecutors.executeOnUiThread(() -> callback.success(income));
        });
    }

    /** 保存记录 */
    void saveIncome(Record income, SimpleCallback<Result<Long, IError>> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            long id = mDataBase.incomeDao().insert(income.createEntity());
            MineExecutors.executeOnUiThread(() -> callback.success(new Result<>(id, null)));
        });
    }

    /** 保存记录 */
    void updateIncome(Record income, SimpleCallback<Result<Long, IError>> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            long id = mDataBase.incomeDao().update(income.createEntity());
            MineExecutors.executeOnUiThread(() -> callback.success(new Result<>(id, null)));
        });
    }
}
