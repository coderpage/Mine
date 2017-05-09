package com.coderpage.mine.app.tally.main;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.coderpage.framework.Model;
import com.coderpage.framework.QueryEnum;
import com.coderpage.framework.SimpleCallback;
import com.coderpage.framework.UserActionEnum;
import com.coderpage.concurrency.AsyncTaskExecutor;
import com.coderpage.utils.LogUtils;
import com.coderpage.mine.app.tally.data.ExpenseItem;
import com.coderpage.mine.app.tally.provider.TallyContract;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.coderpage.utils.LogUtils.LOGE;
import static com.coderpage.mine.app.tally.main.MainModel.MainQueryEnum.MONTH_TOTAL;

/**
 * @author abner-l. 2017-04-12
 * @since 0.2.0
 */

public class MainModel implements Model<MainModel.MainQueryEnum, MainModel.MainUserActionEnum> {
    private static final String TAG = LogUtils.makeLogTag(MainModel.class);

    public static final String EXTRA_EXPENSE_ID = "extra_expense_id";
    public static final String EXTRA_LOAD_MORE_START_DATE = "extra_load_more_start_date";

    private Context mContext;

    private volatile float mMonthTotal = 0.0f;
    private volatile ExpenseItem mNewAddExpenseItem;
    private volatile ExpenseItem mEditedExpenseItem;
    private List<ExpenseItem> mCurrentMonthExpenseItemList = new ArrayList<>();
    private List<ExpenseItem> mInitExpenseItemList = new ArrayList<>();
    private List<ExpenseItem> mLoadMoreExpenseItemList = new ArrayList<>();

    public MainModel(Context context) {
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
    public void deliverUserAction(MainUserActionEnum action, @Nullable Bundle args, UserActionCallback callback) {
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
            case NEW_EXPENSE_CREATED:
                if (args == null || !args.containsKey(EXTRA_EXPENSE_ID)) {
                    LOGE(TAG, "action " + action.getId() + " request args with " + EXTRA_EXPENSE_ID);
                    return;
                }
                long expenseId = args.getLong(EXTRA_EXPENSE_ID);
                queryExpenseByIdAsync(expenseId, (item) -> {
                    mNewAddExpenseItem = item;
                    if (mNewAddExpenseItem == null) {
                        callback.onError(action);
                    } else {
                        callback.onModelUpdated(MainModel.this, action);
                    }
                });
                break;
            case EXPENSE_EDITED:
                if (args == null || !args.containsKey(EXTRA_EXPENSE_ID)) {
                    LOGE(TAG, "action " + action.getId() + " request args with " + EXTRA_EXPENSE_ID);
                    return;
                }
                long editedExpenseId = args.getLong(EXTRA_EXPENSE_ID);
                queryExpenseByIdAsync(editedExpenseId, (item) -> {
                    if (item == null) {
                        callback.onError(action);
                    } else {
                        mEditedExpenseItem = item;
                        callback.onModelUpdated(MainModel.this, action);
                    }
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
            case LOAD_MORE:
                if (args == null || !args.containsKey(EXTRA_LOAD_MORE_START_DATE)) {
                    LOGE(TAG, "action " + action.getId()
                            + " request args with " + EXTRA_LOAD_MORE_START_DATE);
                    return;
                }
                long loadMoreStartDate = args.getLong(EXTRA_LOAD_MORE_START_DATE);
                queryExpenseAsync(
                        TallyContract.Expense.TIME + "<?",
                        new String[]{String.valueOf(loadMoreStartDate)},
                        "expense_time DESC LIMIT 15",
                        (result) -> {
                            mLoadMoreExpenseItemList.clear();
                            mLoadMoreExpenseItemList.addAll(result);
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
                queryExpenseAsync(null, null, "expense_time DESC LIMIT 15", (result) -> {
                    mInitExpenseItemList.clear();
                    mInitExpenseItemList.addAll(result);
                    callback.onModelUpdated(MainModel.this, query);
                });
                break;
        }
    }

    @Override
    public void cleanUp() {

    }

    public void queryExpenseAsync(String selection, String[] selectionArgs,
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

    public void reloadMonthTotalAsync(SimpleCallback<List<ExpenseItem>> callback) {
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

    public void queryExpenseByIdAsync(long expenseId, SimpleCallback<ExpenseItem> callback) {
        new AsyncTask<Void, Void, ExpenseItem>() {
            @Override
            protected ExpenseItem doInBackground(Void... params) {
                return queryExpenseItemById(expenseId);
            }

            @Override
            protected void onPostExecute(ExpenseItem item) {
                callback.success(item);
                mNewAddExpenseItem = item;
            }
        }.executeOnExecutor(AsyncTaskExecutor.executor());
    }

    public void deleteExpenseByIdAsync(long expenseId, SimpleCallback<Integer> callback) {
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
    public float getMonthTotal() {
        return mMonthTotal;
    }

    public List<ExpenseItem> getInitExpenseItemList() {
        return mInitExpenseItemList;
    }

    public List<ExpenseItem> getLoadMoreExpenseItemList() {
        return mLoadMoreExpenseItemList;
    }

    public ExpenseItem getNewAddExpenseItem() {
        return mNewAddExpenseItem;
    }

    public ExpenseItem getEditedExpenseItem() {
        return mEditedExpenseItem;
    }

    public List<ExpenseItem> getCurrentMonthExpenseItemList() {
        return mCurrentMonthExpenseItemList;
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

    private float calculateMonthTotalExpense() {
        Calendar monthStartCalendar = Calendar.getInstance();
        monthStartCalendar.set(Calendar.DAY_OF_MONTH, 1);
        monthStartCalendar.set(Calendar.HOUR_OF_DAY, 0);
        monthStartCalendar.set(Calendar.MINUTE, 0);
        monthStartCalendar.set(Calendar.SECOND, 0);
        long monthStartDate = monthStartCalendar.getTimeInMillis();

        Cursor cursor = mContext.getContentResolver().query(
                TallyContract.Expense.CONTENT_URI, MONTH_TOTAL.projection,
                TallyContract.Expense.TIME + ">=?",
                new String[]{String.valueOf(monthStartDate)},
                null);
        if (cursor == null) {
            return 0.0F;
        }
        float amountTotal = 0.0F;
        while (cursor.moveToNext()) {
            float amount = cursor.getFloat(cursor.getColumnIndex(TallyContract.Expense.AMOUNT));
            amountTotal += amount;
        }
        cursor.close();

        return amountTotal;
    }

    public enum MainQueryEnum implements QueryEnum {
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

    public enum MainUserActionEnum implements UserActionEnum {
        RELOAD_MONTH_TOTAL(1),
        EXPENSE_EDITED(2),
        EXPENSE_DELETE(3),
        NEW_EXPENSE_CREATED(4),
        LOAD_MORE(5);

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
