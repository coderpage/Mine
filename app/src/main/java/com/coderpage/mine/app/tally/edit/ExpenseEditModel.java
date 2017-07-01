package com.coderpage.mine.app.tally.edit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.coderpage.framework.Model;
import com.coderpage.framework.QueryEnum;
import com.coderpage.framework.SimpleCallback;
import com.coderpage.framework.UserActionEnum;
import com.coderpage.concurrency.AsyncTaskExecutor;
import com.coderpage.mine.app.tally.data.CategoryIconHelper;
import com.coderpage.mine.app.tally.data.CategoryItem;
import com.coderpage.mine.app.tally.data.ExpenseItem;
import com.coderpage.mine.app.tally.provider.ProviderUtils;
import com.coderpage.mine.app.tally.provider.TallyContract;
import com.coderpage.mine.utils.AndroidUtils;

import java.util.ArrayList;
import java.util.List;

import static com.coderpage.utils.LogUtils.LOGE;
import static com.coderpage.utils.LogUtils.makeLogTag;

/**
 * @author abner-l. 2017-04-16
 */

public class ExpenseEditModel implements Model<ExpenseEditModel.EditQueryEnum
        , ExpenseEditModel.EditUserActionEnum> {
    private static final String TAG = makeLogTag(ExpenseEditModel.class);

    public static final String EXTRA_EXPENSE_ID = "extra_expense_id";
    public static final String EXTRA_EXPENSE_AMOUNT = "extra_expense_amount";
    public static final String EXTRA_EXPENSE_CATEGORY_ID = "extra_expense_category_id";
    public static final String EXTRA_EXPENSE_CATEGORY = "extra_expense_category";
    public static final String EXTRA_EXPENSE_DESC = "extra_expense_desc";
    public static final String EXTRA_EXPENSE_TIME = "extra_expense_time";

    private Context mContext;
    private ExpenseItem mExpenseItem;
    private List<CategoryItem> mCategoryItemList = new ArrayList<>();

    public ExpenseEditModel(Context context) {
        mContext = context;
    }

    public List<CategoryItem> getCategoryItemList() {
        return mCategoryItemList;
    }

    public ExpenseItem getExpenseItem() {
        return mExpenseItem;
    }

    @Override
    public EditQueryEnum[] getQueries() {
        return EditQueryEnum.values();
    }

    @Override
    public EditUserActionEnum[] getUserActions() {
        return EditUserActionEnum.values();
    }

    @Override
    public void requestData(EditQueryEnum query, DataQueryCallback callback) {
        switch (query) {
            case LOAD_CATEGORY:
                loadCategoryAsync((result) -> {
                    if (result != null) {
                        mCategoryItemList.clear();
                        mCategoryItemList.addAll(result);
                        callback.onModelUpdated(ExpenseEditModel.this, query);
                    } else {
                        callback.onError(query);
                    }
                });
                break;
        }
    }

    @Override
    public void deliverUserAction(EditUserActionEnum action,
                                  @Nullable Bundle args, UserActionCallback callback) {
        switch (action) {
            case RELOAD:
                if (args == null || !args.containsKey(EXTRA_EXPENSE_ID)) {
                    LOGE(TAG, "action " + action.getId() + " request args with " + EXTRA_EXPENSE_ID);
                    return;
                }
                if (mExpenseItem == null) {
                    mExpenseItem = new ExpenseItem();
                }
                long editedExpenseId = args.getLong(EXTRA_EXPENSE_ID);
                // 没有记录ID，说明为创建一个新纪录，赋值为默认值
                if (editedExpenseId <= 0) {
                    queryFirstPlaceCategory((item) -> {
                        if (item == null) {
                            callback.onError(action);
                        } else {
                            mExpenseItem.setAmount(0.0f);
                            mExpenseItem.setCategoryIconResId(item.getIcon());
                            mExpenseItem.setTime(System.currentTimeMillis());
                            mExpenseItem.setCategoryId(item.getId());
                            mExpenseItem.setCategoryName(item.getName());
                            callback.onModelUpdated(ExpenseEditModel.this, action);
                        }
                    });
                    return;
                }
                // 读取当前记录最新数据
                queryExpenseByIdAsync(editedExpenseId, (item) -> {
                    if (item == null) {
                        callback.onError(action);
                    } else {
                        mExpenseItem = item;
                        callback.onModelUpdated(ExpenseEditModel.this, action);
                    }
                });
                break;
            case SAVE_DATA:
                if (args == null) {
                    LOGE(TAG, "action " + action.getId() + " request args");
                    return;
                }
                long id = args.getLong(EXTRA_EXPENSE_ID, -1);
                float amount = args.getFloat(EXTRA_EXPENSE_AMOUNT, 0.0F);
                long categoryId = args.getLong(EXTRA_EXPENSE_CATEGORY_ID, -1);
                String categoryName = args.getString(EXTRA_EXPENSE_CATEGORY, "");
                String desc = args.getString(EXTRA_EXPENSE_DESC, "");
                long date = args.getLong(EXTRA_EXPENSE_TIME, System.currentTimeMillis());
                saveDataAsync(id, amount, categoryId, categoryName, desc, date, (eid) -> {
                    if (mExpenseItem == null) {
                        mExpenseItem = new ExpenseItem();
                    }
                    if (eid > 0) {
                        mExpenseItem.setId(eid);
                        mExpenseItem.setAmount(amount);
                        mExpenseItem.setCategoryId(categoryId);
                        mExpenseItem.setDesc(desc);
                        mExpenseItem.setTime(date);
                        mExpenseItem.setCategoryIconResId(CategoryIconHelper.resId(categoryName));
                        callback.onModelUpdated(ExpenseEditModel.this, action);
                    } else {
                        callback.onError(action);
                    }
                });
                break;
        }
    }

    @Override
    public void cleanUp() {

    }

    private void queryFirstPlaceCategory(SimpleCallback<CategoryItem> callback) {
        new AsyncTask<Void, Void, CategoryItem>() {
            @Override
            protected CategoryItem doInBackground(Void... params) {
                Cursor cursor = mContext.getContentResolver().query(
                        TallyContract.Category.CONTENT_URI,
                        null, null, null, "category_order DESC");
                if (cursor == null) {
                    return null;
                }
                CategoryItem item = null;
                if (cursor.moveToFirst()) {
                    item = CategoryItem.fromCursor(cursor);
                }
                cursor.close();
                return item;
            }

            @Override
            protected void onPostExecute(CategoryItem item) {
                callback.success(item);
            }
        }.executeOnExecutor(AsyncTaskExecutor.executor());
    }

    private void loadCategoryAsync(SimpleCallback<List<CategoryItem>> callback) {
        new AsyncTask<Void, Void, List<CategoryItem>>() {
            @Override
            protected List<CategoryItem> doInBackground(Void... params) {
                Cursor cursor = mContext.getContentResolver().query(
                        TallyContract.Category.CONTENT_URI,
                        null, null, null, "category_order DESC");
                if (cursor == null) {
                    return null;
                }
                List<CategoryItem> result = new ArrayList<>(cursor.getCount());
                while (cursor.moveToNext()) {
                    result.add(CategoryItem.fromCursor(cursor));
                }
                cursor.close();
                return result;
            }

            @Override
            protected void onPostExecute(List<CategoryItem> categoryItems) {
                callback.success(categoryItems);
            }
        }.executeOnExecutor(AsyncTaskExecutor.executor());
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

    public void saveDataAsync(long expenseId,
                              float amount,
                              long categoryId,
                              String categoryName,
                              String desc,
                              long date,
                              SimpleCallback<Long> callback) {
        new AsyncTask<Void, Void, Long>() {
            @Override
            protected Long doInBackground(Void... params) {
                ContentValues values = new ContentValues();
                values.put(TallyContract.Expense.AMOUNT, amount);
                values.put(TallyContract.Expense.CATEGORY_ID, categoryId);
                values.put(TallyContract.Expense.CATEGORY, categoryName);
                values.put(TallyContract.Expense.DESC, desc == null ? "" : desc);
                values.put(TallyContract.Expense.TIME, date);

                // categoryId 大于0，更新记录
                // 否则插入新记录
                if (expenseId > 0) {
                    mContext.getContentResolver().update(
                            TallyContract.Expense.CONTENT_URI,
                            values,
                            TallyContract.Expense._ID + "=?",
                            new String[]{String.valueOf(expenseId)});
                    return expenseId;
                } else {
                    values.put(TallyContract.Expense.SYNC_ID, AndroidUtils.generateUUID());
                    Uri uri = mContext.getContentResolver()
                            .insert(TallyContract.Expense.CONTENT_URI, values);
                    return ProviderUtils.parseIdFromUri(uri);
                }
            }

            @Override
            protected void onPostExecute(Long id) {
                callback.success(id);
            }
        }.executeOnExecutor(AsyncTaskExecutor.executor());

    }

    public enum EditQueryEnum implements QueryEnum {
        LOAD_CATEGORY(1, null);

        private int id;
        private String[] projection;

        EditQueryEnum(int id, String[] projection) {
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

    public enum EditUserActionEnum implements UserActionEnum {
        RELOAD(1),
        SAVE_DATA(2);

        private int id;

        private EditUserActionEnum(int id) {
            this.id = id;
        }

        @Override
        public int getId() {
            return id;
        }
    }
}
