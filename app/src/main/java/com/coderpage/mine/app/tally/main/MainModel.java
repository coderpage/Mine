package com.coderpage.mine.app.tally.main;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.coderpage.concurrency.AsyncTaskExecutor;
import com.coderpage.framework.Model;
import com.coderpage.framework.QueryEnum;
import com.coderpage.framework.SimpleCallback;
import com.coderpage.framework.UserActionEnum;
import com.coderpage.mine.app.tally.data.ExpenseItem;
import com.coderpage.mine.app.tally.provider.TallyContract;
import com.coderpage.utils.LogUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.coderpage.utils.LogUtils.LOGE;

/**
 * @author abner-l. 2017-04-12
 * @since 0.2.0
 */

class MainModel implements Model<MainModel.MainQueryEnum, MainModel.MainUserActionEnum> {
    private static final String TAG = LogUtils.makeLogTag(MainModel.class);

    static final String EXTRA_EXPENSE_ID = "extra_expense_id";

    private Context mContext;

    private volatile float mMonthTotal = 0.0f;
    private List<ExpenseItem> mCurrentMonthExpenseItemList = new ArrayList<>();
    private List<ExpenseItem> mTodayExpenseList = new ArrayList<>();

    MainModel(Context context) {
        mContext = context;
    }

    @Override
    public MainQueryEnum[] getQueries() {
        return MainQueryEnum.values();
    }

    @Override
    public MainUserActionEnum[] getUserActions() {
        return MainUserActionEnum.values();
    }

    @Override
    public void deliverUserAction(MainUserActionEnum action,
                                  @Nullable Bundle args,
                                  UserActionCallback callback) {
        switch (action) {
            case RELOAD_MONTH_TOTAL:
                reloadMonthTotalAsync((monthRecords) -> {
                    float amountTotal = 0.0F;
                    for (ExpenseItem item : monthRecords) {
                        amountTotal += item.getAmount();
                    }
                    mMonthTotal = amountTotal;
                    mCurrentMonthExpenseItemList.clear();
                    mCurrentMonthExpenseItemList.addAll(monthRecords);
                    callback.onModelUpdated(MainModel.this, action);
                });
                break;
            case EXPENSE_DELETE:
                if (args == null || !args.containsKey(EXTRA_EXPENSE_ID)) {
                    LOGE(TAG, "action " + action.getId() + " request args with " + EXTRA_EXPENSE_ID);
                    return;
                }
                long deletedExpenseId = args.getLong(EXTRA_EXPENSE_ID);
                deleteExpenseByIdAsync(deletedExpenseId, (delNum) -> {
                    if (delNum > 0) {
                        callback.onModelUpdated(MainModel.this, action);
                    } else {
                        callback.onError(action);
                    }
                });
                break;
            case REFRESH_TODAY_EXPENSE:
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                long todayStartTime = calendar.getTimeInMillis();
                queryExpenseAsync(
                        TallyContract.Expense.TIME + ">=?",
                        new String[]{String.valueOf(todayStartTime)},
                        "expense_time DESC",
                        (result) -> {
                            mTodayExpenseList.clear();
                            mTodayExpenseList.addAll(result);
                            callback.onModelUpdated(MainModel.this, action);
                        });
                break;
        }
    }

    @Override
    public void requestData(MainQueryEnum query, DataQueryCallback callback) {
        switch (query) {
            case MONTH_TOTAL:
                reloadMonthTotalAsync((monthRecords) -> {
                    float amountTotal = 0.0F;
                    for (ExpenseItem item : monthRecords) {
                        amountTotal += item.getAmount();
                    }
                    mMonthTotal = amountTotal;
                    mCurrentMonthExpenseItemList.clear();
                    mCurrentMonthExpenseItemList.addAll(monthRecords);
                    callback.onModelUpdated(MainModel.this, query);
                });
                break;
            case EXPENSE_INIT:
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                long todayStartTime = calendar.getTimeInMillis();
                queryExpenseAsync(
                        TallyContract.Expense.TIME + ">=?",
                        new String[]{String.valueOf(todayStartTime)},
                        "expense_time DESC",
                        (result) -> {
                            mTodayExpenseList.clear();
                            mTodayExpenseList.addAll(result);
                            callback.onModelUpdated(MainModel.this, query);
                        });
                break;
        }
    }

    @Override
    public void cleanUp() {

    }

    private void queryExpenseAsync(String selection, String[] selectionArgs,
                                   String order, SimpleCallback<List<ExpenseItem>> callback) {
        new AsyncTask<Void, Void, List<ExpenseItem>>() {
            @Override
            protected List<ExpenseItem> doInBackground(Void... params) {
                Cursor cursor = mContext.getContentResolver().query(
                        TallyContract.Expense.CONTENT_URI,
                        null,
                        selection,
                        selectionArgs,
                        order);
                if (cursor == null) {
                    return new ArrayList<>(0);
                }
                List<ExpenseItem> items = new ArrayList<>(cursor.getCount());
                while (cursor.moveToNext()) {
                    items.add(ExpenseItem.fromCursor(cursor));
                }
                cursor.close();
                return items;
            }

            @Override
            protected void onPostExecute(List<ExpenseItem> items) {
                callback.success(items);
            }
        }.executeOnExecutor(AsyncTaskExecutor.executor());
    }

    private void reloadMonthTotalAsync(SimpleCallback<List<ExpenseItem>> callback) {
        Calendar monthStartCalendar = Calendar.getInstance();
        monthStartCalendar.set(Calendar.DAY_OF_MONTH, 1);
        monthStartCalendar.set(Calendar.HOUR_OF_DAY, 0);
        monthStartCalendar.set(Calendar.MINUTE, 0);
        monthStartCalendar.set(Calendar.SECOND, 0);
        long monthStartDate = monthStartCalendar.getTimeInMillis();

        String selection = TallyContract.Expense.TIME + ">=?";
        String[] selectionArgs = new String[]{String.valueOf(monthStartDate)};
        queryExpenseAsync(selection, selectionArgs, null, callback);
    }

    void deleteExpenseByIdAsync(long expenseId, SimpleCallback<Integer> callback) {
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                return mContext.getContentResolver().delete(
                        TallyContract.Expense.CONTENT_URI,
                        TallyContract.Expense._ID + "=?",
                        new String[]{String.valueOf(expenseId)});
            }

            @Override
            protected void onPostExecute(Integer deletedNum) {
                callback.success(deletedNum);
            }
        }.executeOnExecutor(AsyncTaskExecutor.executor());
    }

    /**
     * @return 返回本月消费总额
     */
    float getMonthTotal() {
        return mMonthTotal;
    }

    List<ExpenseItem> getCurrentMonthExpenseItemList() {
        return mCurrentMonthExpenseItemList;
    }

    List<ExpenseItem> getTodayExpenseList() {
        return mTodayExpenseList;
    }

    private ExpenseItem queryExpenseItemById(long id) {
        Cursor cursor = mContext.getContentResolver().query(
                TallyContract.Expense.CONTENT_URI,
                null,
                TallyContract.Expense._ID + "=?",
                new String[]{String.valueOf(id)},
                null
        );
        ExpenseItem item = null;
        if (cursor == null) return null;
        if (cursor.moveToFirst()) {
            item = ExpenseItem.fromCursor(cursor);
        }
        cursor.close();
        return item;
    }

    enum MainQueryEnum implements QueryEnum {
        MONTH_TOTAL(0, null),
        EXPENSE_INIT(1, null);

        private int id;
        private String[] projection;

        MainQueryEnum(int id, String[] projection) {
            this.id = id;
            this.projection = projection;
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public String[] getProjection() {
            return projection;
        }
    }

    enum MainUserActionEnum implements UserActionEnum {
        RELOAD_MONTH_TOTAL(1),
        EXPENSE_DELETE(2),
        REFRESH_TODAY_EXPENSE(3);

        private int id;

        MainUserActionEnum(int id) {
            this.id = id;
        }

        @Override
        public int getId() {
            return id;
        }
    }
}
