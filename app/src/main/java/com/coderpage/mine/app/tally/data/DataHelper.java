package com.coderpage.mine.app.tally.data;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.coderpage.common.Callback;
import com.coderpage.common.IError;
import com.coderpage.common.NonThrowError;
import com.coderpage.concurrency.AsyncTaskExecutor;
import com.coderpage.mine.app.tally.common.error.ErrorCode;
import com.coderpage.mine.app.tally.provider.TallyContract;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lc. 2017-09-17
 * @since 0.5.0
 *
 * 提供操作数据的方法；
 */

public class DataHelper {

    public static void queryExpenseByIdAsync(Context context,
                                             long expenseId,
                                             Callback<Expense, IError> callback) {
        Context appContext = context.getApplicationContext();
        new AsyncTask<Void, Void, Expense>() {
            @Override
            protected Expense doInBackground(Void... params) {
                Cursor cursor = appContext.getContentResolver().query(
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
                if (item == null) {
                    callback.failure(new NonThrowError(ErrorCode.UNKNOWN, "unknown error"));
                } else {
                    callback.success(item);
                }
            }
        }.executeOnExecutor(AsyncTaskExecutor.executor());
    }

    public static void deleteExpenseByIdAsync(Context context,
                                              long expenseId,
                                              Callback<Integer, IError> callback) {

        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                return context.getContentResolver().delete(
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

    public static void searchByKeywordAsync(Context context,
                                            String keyword,
                                            Callback<List<Expense>, IError> callback) {
        new AsyncTask<Void, Void, List<Expense>>() {
            @Override
            protected List<Expense> doInBackground(Void... params) {

                String[] projection = null;
                String selection = TallyContract.Expense.CATEGORY + "=? or "
                        + TallyContract.Expense.DESC + " LIKE '%" + keyword + "%'";
                String[] selectionArgs = new String[]{keyword};
                String sortOrder = "";

                Cursor cursor = context.getContentResolver().query(
                        TallyContract.Expense.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder);
                if (cursor == null) return null;

                List<Expense> result = new ArrayList<>(cursor.getCount());
                while (cursor.moveToNext()) {
                    result.add(Expense.fromCursor(cursor));
                }
                cursor.close();

                return result;
            }

            @Override
            protected void onPostExecute(List<Expense> result) {
                if (result == null) {
                    callback.failure(new NonThrowError(ErrorCode.UNKNOWN, ""));
                } else {
                    callback.success(result);
                }
            }
        }.executeOnExecutor(AsyncTaskExecutor.executor());
    }
}
