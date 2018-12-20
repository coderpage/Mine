package com.coderpage.mine.app.tally.module.edit.expense;

import com.coderpage.base.common.IError;
import com.coderpage.base.common.Result;
import com.coderpage.base.common.SimpleCallback;
import com.coderpage.concurrency.MineExecutors;
import com.coderpage.mine.app.tally.persistence.model.CategoryModel;
import com.coderpage.mine.app.tally.persistence.model.Record;
import com.coderpage.mine.app.tally.persistence.sql.TallyDatabase;

import java.util.List;

/**
 * @author lc. 2018-08-29 20:34
 * @since 0.6.0
 */

class ExpenseRepository {

    private TallyDatabase mDataBase;

    ExpenseRepository() {
        mDataBase = TallyDatabase.getInstance();
    }

    /** 查询所有支付分类 */
    void queryAllCategory(SimpleCallback<List<CategoryModel>> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            List<CategoryModel> categoryList = mDataBase.categoryDao().allExpenseCategory();
            MineExecutors.executeOnUiThread(() -> callback.success(categoryList));
        });
    }

    /** 通过 ID 查询支出记录 */
    void queryExpenseById(long expenseId, SimpleCallback<Record> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            Record expense = mDataBase.expenseDao().queryById(expenseId);
            MineExecutors.executeOnUiThread(() -> callback.success(expense));
        });
    }

    /** 保存记录 */
    void saveExpense(Record expense, SimpleCallback<Result<Long, IError>> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            long id = mDataBase.expenseDao().insert(expense.createEntity());
            MineExecutors.executeOnUiThread(() -> callback.success(new Result<>(id, null)));
        });
    }

    /** 保存记录 */
    void updateExpense(Record expense, SimpleCallback<Result<Long, IError>> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            long id = mDataBase.expenseDao().update(expense.createEntity());
            MineExecutors.executeOnUiThread(() -> callback.success(new Result<>(id, null)));
        });
    }
}
