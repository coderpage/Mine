package com.coderpage.mine.app.tally.module.detail;

import com.coderpage.base.common.Callback;
import com.coderpage.base.common.IError;
import com.coderpage.base.common.NonThrowError;
import com.coderpage.base.common.SimpleCallback;
import com.coderpage.base.error.ErrorCode;
import com.coderpage.concurrency.MineExecutors;
import com.coderpage.mine.app.tally.persistence.model.Record;
import com.coderpage.mine.app.tally.persistence.sql.TallyDatabase;
import com.coderpage.mine.app.tally.persistence.sql.entity.RecordEntity;

/**
 * @author lc. 2018-09-22 22:50
 * @since 0.6.0
 */

class RecordDetailRepository {

    void queryExpense(long expenseId, Callback<Record, IError> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            Record expense = TallyDatabase.getInstance().recordDao().queryById(expenseId);
            if (expense == null) {
                MineExecutors.executeOnUiThread(() -> callback.failure(new NonThrowError(ErrorCode.SQL_ERR, "EMPTY DATA")));
            } else {
                MineExecutors.executeOnUiThread(() -> callback.success(expense));
            }
        });
    }

    void queryIncome(long incomeId, Callback<Record, IError> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            Record income = TallyDatabase.getInstance().recordDao().queryById(incomeId);
            if (income == null) {
                MineExecutors.executeOnUiThread(() -> callback.failure(new NonThrowError(ErrorCode.SQL_ERR, "EMPTY DATA")));
            } else {
                MineExecutors.executeOnUiThread(() -> callback.success(income));
            }
        });
    }

    void deleteExpense(long expenseId, SimpleCallback<Boolean> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            RecordEntity entity = new RecordEntity();
            entity.setId(expenseId);
            TallyDatabase.getInstance().recordDao().delete(entity);
            MineExecutors.executeOnUiThread(() -> callback.success(true));
        });
    }

    void deleteIncome(long incomeId, SimpleCallback<Boolean> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            RecordEntity entity = new RecordEntity();
            entity.setId(incomeId);
            TallyDatabase.getInstance().recordDao().delete(entity);
            MineExecutors.executeOnUiThread(() -> callback.success(true));
        });
    }
}
