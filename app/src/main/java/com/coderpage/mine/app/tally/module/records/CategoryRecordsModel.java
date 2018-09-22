package com.coderpage.mine.app.tally.module.records;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.coderpage.base.common.IError;
import com.coderpage.base.common.NonThrowError;
import com.coderpage.concurrency.AsyncTaskExecutor;
import com.coderpage.framework.Model;
import com.coderpage.framework.QueryEnum;
import com.coderpage.base.common.SimpleCallback;
import com.coderpage.framework.UserActionEnum;
import com.coderpage.mine.app.tally.common.error.ErrorCode;
import com.coderpage.mine.app.tally.data.Expense;
import com.coderpage.mine.app.tally.provider.TallyContract;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.coderpage.base.utils.LogUtils.LOGE;
import static com.coderpage.base.utils.LogUtils.makeLogTag;

/**
 * @author abner-l. 2017-05-15
 * @since 0.1.0
 */

class CategoryRecordsModel implements Model<
        CategoryRecordsModel.RecordsQueryEnum,
        CategoryRecordsModel.RecordsUserActionEnum,
        CategoryRecordsModel,
        IError> {
    private static final String TAG = makeLogTag(RecordsModel.class);

    static final String EXTRA_EXPENSE_ID = "extra_expense_id";

    private Context mContext;
    private int mYear;
    private int mMonth;
    private long mCategoryId;
    private Expense mEditedExpense;
    private List<Expense> mInitExpenseList = new ArrayList<>();

    CategoryRecordsModel(Context context, int year, int month, long categoryId) {
        mContext = context;
        mYear = year;
        mMonth = month;
        mCategoryId = categoryId;
    }

    @Override
    public RecordsQueryEnum[] getQueries() {
        return RecordsQueryEnum.values();
    }

    @Override
    public RecordsUserActionEnum[] getUserActions() {
        return RecordsUserActionEnum.values();
    }

    @Override
    public void requestData(
            RecordsQueryEnum query,
            DataQueryCallback<CategoryRecordsModel, RecordsQueryEnum, IError> callback) {

        switch (query) {

            case INIT_DATA:
                Calendar monthStartCalendar = Calendar.getInstance();
                monthStartCalendar.set(Calendar.YEAR, mYear);
                // month -1 的原因是, Calendar MONTH 从 0 开始计数，即 0 是一月
                monthStartCalendar.set(Calendar.MONTH, mMonth - 1);
                monthStartCalendar.set(Calendar.DAY_OF_MONTH, 1);
                monthStartCalendar.set(Calendar.HOUR_OF_DAY, 0);
                monthStartCalendar.set(Calendar.MINUTE, 0);
                monthStartCalendar.set(Calendar.SECOND, 0);

                long monthStartDate = monthStartCalendar.getTimeInMillis();
                monthStartCalendar.set(Calendar.MONTH, mMonth);
                long nextMonthStartDate = monthStartCalendar.getTimeInMillis();

                String selection = "expense.category_id=? AND "
                        + TallyContract.Expense.TIME + ">=? " +
                        "AND " + TallyContract.Expense.TIME + "<?";
                String[] selectionArgs = new String[]{
                        String.valueOf(mCategoryId),
                        String.valueOf(monthStartDate),
                        String.valueOf(nextMonthStartDate)};
                String order = "expense_time DESC";
                queryExpenseAsync(selection, selectionArgs, order, (result) -> {
                    mInitExpenseList.clear();
                    mInitExpenseList.addAll(result);
                    callback.onModelUpdated(CategoryRecordsModel.this, query);
                });
                break;
        }
    }

    @Override
    public void deliverUserAction(
            RecordsUserActionEnum action,
            @Nullable Bundle args,
            UserActionCallback<CategoryRecordsModel, RecordsUserActionEnum, IError> callback) {

        switch (action) {

            case EXPENSE_EDITED:
                if (args == null || !args.containsKey(EXTRA_EXPENSE_ID)) {
                    LOGE(TAG, "action " + action.getId() + " request args with " + EXTRA_EXPENSE_ID);
                    return;
                }
                long editedExpenseId = args.getLong(EXTRA_EXPENSE_ID);
                queryExpenseByIdAsync(editedExpenseId, (item) -> {
                    if (item == null) {
                        callback.onError(action, new NonThrowError(ErrorCode.UNKNOWN, ""));
                    } else {
                        mEditedExpense = item;
                        callback.onModelUpdated(CategoryRecordsModel.this, action);
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
                        callback.onModelUpdated(CategoryRecordsModel.this, action);
                    } else {
                        callback.onError(action, new NonThrowError(ErrorCode.UNKNOWN, ""));
                    }
                });
                break;
        }
    }

    @Override
    public void cleanUp() {

    }

    private void queryExpenseByIdAsync(long expenseId, SimpleCallback<Expense> callback) {
        new AsyncTask<Void, Void, Expense>() {
            @Override
            protected Expense doInBackground(Void... params) {
                Cursor cursor = mContext.getContentResolver().query(
                        TallyContract.Expense.CONTENT_URI,
                        null,
                        TallyContract.Expense._ID + "=?",
                        new String[]{String.valueOf(expenseId)},
                        null
                );
                Expense item = null;
                if (cursor == null) return null;
                if (cursor.moveToFirst()) {
                    item = Expense.fromCursor(cursor);
                }
                cursor.close();
                return item;
            }

            @Override
            protected void onPostExecute(Expense item) {
                callback.success(item);
            }
        }.executeOnExecutor(AsyncTaskExecutor.executor());
    }

    private void queryExpenseAsync(String selection, String[] selectionArgs,
                                   String order, SimpleCallback<List<Expense>> callback) {
        new AsyncTask<Void, Void, List<Expense>>() {
            @Override
            protected List<Expense> doInBackground(Void... params) {
                Cursor cursor = mContext.getContentResolver().query(
                        TallyContract.Expense.CONTENT_URI,
                        null,
                        selection,
                        selectionArgs,
                        order);
                if (cursor == null) {
                    return new ArrayList<>(0);
                }
                List<Expense> items = new ArrayList<>(cursor.getCount());
                while (cursor.moveToNext()) {
                    items.add(Expense.fromCursor(cursor));
                }
                cursor.close();
                return items;
            }

            @Override
            protected void onPostExecute(List<Expense> items) {
                callback.success(items);
            }
        }.executeOnExecutor(AsyncTaskExecutor.executor());
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

    Expense getEditedExpenseItem() {
        return mEditedExpense;
    }

    List<Expense> getInitExpenseList() {
        return mInitExpenseList;
    }

    enum RecordsQueryEnum implements QueryEnum {
        INIT_DATA(1, null);
        private int id;
        private String[] projection;

        @Override
        public int getId() {
            return id;
        }

        @Override
        public String[] getProjection() {
            return projection;
        }

        RecordsQueryEnum(int id, String[] projection) {
            this.id = id;
            this.projection = projection;
        }
    }

    enum RecordsUserActionEnum implements UserActionEnum {
        EXPENSE_EDITED(1),
        EXPENSE_DELETE(2);
        private int id;

        @Override
        public int getId() {
            return id;
        }

        RecordsUserActionEnum(int id) {
            this.id = id;
        }
    }
}
