package com.coderpage.mine.app.tally.detail;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.coderpage.common.Callback;
import com.coderpage.common.IError;
import com.coderpage.framework.Model;
import com.coderpage.mine.app.tally.data.DataHelper;
import com.coderpage.mine.app.tally.data.Expense;

/**
 * @author lc. 2017-09-17
 * @since 0.5.0
 */

class ExpenseDetailModel implements Model<
        ExpenseDetailModel.Queries,
        ExpenseDetailModel.UserActions,
        ExpenseDetailModel,
        IError> {

    private Context mContext;

    private ExpenseDetailModel self;
    private long mExpenseId;
    private Expense mExpense;


    ExpenseDetailModel(Context context, long expenseId) {
        mContext = context;
        mExpenseId = expenseId;
        self = this;
    }

    public Expense getExpense() {
        return mExpense;
    }

    @Override
    public Queries[] getQueries() {
        return Queries.values();
    }

    @Override
    public UserActions[] getUserActions() {
        return UserActions.values();
    }

    @Override
    public void requestData(
            Queries query,
            DataQueryCallback<ExpenseDetailModel, Queries, IError> callback) {

        switch (query) {

            case DATA_INIT:
                DataHelper.queryExpenseByIdAsync(mContext, mExpenseId,
                        new Callback<Expense, IError>() {
                            @Override
                            public void success(Expense expense) {
                                mExpense = expense;
                                callback.onModelUpdated(self, query);
                            }

                            @Override
                            public void failure(IError iError) {
                                callback.onError(query, iError);
                            }
                        });
                break;
        }
    }

    @Override
    public void deliverUserAction(
            UserActions action,
            @Nullable Bundle args,
            UserActionCallback<ExpenseDetailModel, UserActions, IError> callback) {

        switch (action) {

            case DELETE:
                DataHelper.deleteExpenseByIdAsync(mContext, mExpenseId, new Callback<Integer, IError>() {
                    @Override
                    public void success(Integer integer) {
                        callback.onModelUpdated(self, action);
                    }

                    @Override
                    public void failure(IError error) {
                        callback.onError(action, error);
                    }
                });
                break;

            case MODIFY_FINISH:
                DataHelper.queryExpenseByIdAsync(mContext, mExpenseId,
                        new Callback<Expense, IError>() {
                            @Override
                            public void success(Expense expense) {
                                mExpense = expense;
                                callback.onModelUpdated(self, action);
                            }

                            @Override
                            public void failure(IError iError) {
                                callback.onError(action, iError);
                            }
                        });
                break;
        }
    }


    @Override
    public void cleanUp() {

    }

    enum Queries implements com.coderpage.framework.QueryEnum {
        DATA_INIT(1, null);

        @Override
        public int getId() {
            return 0;
        }

        @Override
        public String[] getProjection() {
            return new String[0];
        }

        private int id;
        private String[] projection;

        Queries(int id, String[] projection) {
            this.id = id;
            this.projection = projection;
        }
    }


    enum UserActions implements com.coderpage.framework.UserActionEnum {
        DELETE(1),
        MODIFY_FINISH(2);

        @Override
        public int getId() {
            return 0;
        }

        private int id;

        UserActions(int id) {
            this.id = id;
        }
    }
}
