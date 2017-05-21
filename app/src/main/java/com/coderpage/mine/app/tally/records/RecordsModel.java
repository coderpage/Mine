package com.coderpage.mine.app.tally.records;

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

import java.util.ArrayList;
import java.util.List;

import static com.coderpage.utils.LogUtils.LOGE;
import static com.coderpage.utils.LogUtils.makeLogTag;

/**
 * @author abner-l. 2017-05-12
 */

class RecordsModel implements
        Model<RecordsModel.RecordsQueryEnum, RecordsModel.RecordsUserActionEnum> {
    private static final String TAG = makeLogTag(RecordsModel.class);

    static final String EXTRA_EXPENSE_ID = "extra_expense_id";
    static final String EXTRA_LOAD_MORE_START_DATE = "extra_load_more_start_date";

    private Context mContext;

    private ExpenseItem mEditedExpenseItem;
    private List<ExpenseItem> mInitExpenseList = new ArrayList<>();
    private List<ExpenseItem> mLoadMoreExpenseList = new ArrayList<>();

    RecordsModel(Context context) {
        mContext = context;
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
    public void requestData(RecordsQueryEnum query, DataQueryCallback callback) {
        switch (query) {
            case INIT_DATA:
                queryExpenseAsync(null, null, "expense_time DESC LIMIT 25", (result) -> {
                    mInitExpenseList.clear();
                    mInitExpenseList.addAll(result);
                    callback.onModelUpdated(RecordsModel.this, query);
                });
                break;
        }
    }

    @Override
    public void deliverUserAction(RecordsUserActionEnum action,
                                  @Nullable Bundle args,
                                  UserActionCallback callback) {
        switch (action) {
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
                        callback.onModelUpdated(RecordsModel.this, action);
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
                        callback.onModelUpdated(RecordsModel.this, action);
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
                            mLoadMoreExpenseList.clear();
                            mLoadMoreExpenseList.addAll(result);
                            callback.onModelUpdated(RecordsModel.this, action);
                        });
                break;
        }
    }

    @Override
    public void cleanUp() {

    }

    public void queryExpenseByIdAsync(long expenseId, SimpleCallback<ExpenseItem> callback) {
        new AsyncTask<Void, Void, ExpenseItem>() {
            @Override
            protected ExpenseItem doInBackground(Void... params) {
                Cursor cursor = mContext.getContentResolver().query(
                        TallyContract.Expense.CONTENT_URI,
                        null,
                        TallyContract.Expense._ID + "=?",
                        new String[]{String.valueOf(expenseId)},
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

            @Override
            protected void onPostExecute(ExpenseItem item) {
                callback.success(item);
            }
        }.executeOnExecutor(AsyncTaskExecutor.executor());
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

     ExpenseItem getEditedExpenseItem() {
        return mEditedExpenseItem;
    }

     List<ExpenseItem> getInitExpenseList() {
        return mInitExpenseList;
    }

     List<ExpenseItem> getLoadMoreExpenseList() {
        return mLoadMoreExpenseList;
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
        LOAD_MORE(1),
        EXPENSE_EDITED(2),
        EXPENSE_DELETE(3);
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
