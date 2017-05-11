package com.coderpage.mine.app.tally.chart;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.coderpage.common.Callback;
import com.coderpage.common.IError;
import com.coderpage.common.IResult;
import com.coderpage.common.NonThrowError;
import com.coderpage.common.Result;
import com.coderpage.concurrency.AsyncTaskExecutor;
import com.coderpage.framework.Model;
import com.coderpage.framework.QueryEnum;
import com.coderpage.framework.UserActionEnum;
import com.coderpage.mine.app.tally.chart.data.DailyExpense;
import com.coderpage.mine.app.tally.chart.data.Month;
import com.coderpage.mine.app.tally.chart.data.MonthCategoryExpense;
import com.coderpage.mine.app.tally.data.ExpenseItem;
import com.coderpage.mine.app.tally.provider.TallyContract;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.coderpage.utils.LogUtils.LOGI;
import static com.coderpage.utils.LogUtils.makeLogTag;

/**
 * @author abner-l. 2017-05-04
 */

class ChartModel implements Model<ChartModel.ChartQueryEnum, ChartModel.ChartUserActionEnum> {
    private static final String TAG = makeLogTag(ChartModel.class);

    static final String EXTRA_YEAR = "extra_year"; // bundle extra year
    static final String EXTRA_MONTH = "extra_month"; // bundle extra month

    private Context mContext;
    private Month mDisplayMonth;
    private List<Month> mMonthList = new ArrayList<>();
    private List<ExpenseItem> mMonthExpenseList = new ArrayList<>();
    private List<DailyExpense> mMonthDailyExpenseList = new ArrayList<>();
    private List<MonthCategoryExpense> mMonthCategoryExpenseList = new ArrayList<>();

    ChartModel(Context context) {
        mContext = context;
    }

    @Override
    public ChartQueryEnum[] getQueries() {
        return ChartQueryEnum.values();
    }

    @Override
    public ChartUserActionEnum[] getUserActions() {
        return ChartUserActionEnum.values();
    }

    @Override
    public void requestData(ChartQueryEnum query, DataQueryCallback callback) {
        switch (query) {
            case LOAD_CURRENT_MONTH_DATA:
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                mDisplayMonth = new Month(year, month);
                loadMonthExpense(year, month, new Callback<List<ExpenseItem>, IError>() {
                    @Override
                    public void success(List<ExpenseItem> items) {
                        mMonthExpenseList.clear();
                        mMonthExpenseList.addAll(items);
                        mMonthDailyExpenseList.clear();
                        mMonthDailyExpenseList.addAll(generateDailyExpense(items));
                        mMonthCategoryExpenseList.clear();
                        mMonthCategoryExpenseList.addAll(
                                generateMonthCategoryList(year, month, items));
                        callback.onModelUpdated(ChartModel.this, query);
                    }

                    @Override
                    public void failure(IError iError) {
                        callback.onError(query);
                    }
                });
                break;
        }
    }

    @Override
    public void deliverUserAction(ChartUserActionEnum action,
                                  @Nullable Bundle args,
                                  UserActionCallback callback) {
        switch (action) {
            case SHOW_HISTORY_MONTH_LIST:
                loadMonthArray(new Callback<List<Month>, IError>() {
                    @Override
                    public void success(List<Month> months) {
                        mMonthList.clear();
                        mMonthList.addAll(months);
                        callback.onModelUpdated(ChartModel.this, action);
                    }

                    @Override
                    public void failure(IError iError) {
                        callback.onError(action);
                    }
                });
                break;
            case SWITCH_MONTH:
                if (args == null
                        || !args.containsKey(EXTRA_YEAR)
                        || !args.containsKey(EXTRA_MONTH)) {
                    throw new IllegalStateException("miss args " + EXTRA_YEAR + " or " + EXTRA_MONTH);
                }
                int year = args.getInt(EXTRA_YEAR);
                int month = args.getInt(EXTRA_MONTH);
                loadMonthExpense(year, month, new Callback<List<ExpenseItem>, IError>() {
                    @Override
                    public void success(List<ExpenseItem> items) {
                        mMonthExpenseList.clear();
                        mMonthExpenseList.addAll(items);
                        mMonthDailyExpenseList.clear();
                        mMonthDailyExpenseList.addAll(generateDailyExpense(items));
                        mMonthCategoryExpenseList.clear();
                        mMonthCategoryExpenseList.addAll(
                                generateMonthCategoryList(year, month, items));
                        callback.onModelUpdated(ChartModel.this, action);
                    }

                    @Override
                    public void failure(IError iError) {
                        callback.onError(action);
                    }
                });
                break;
        }
    }

    List<Month> getHistoryMonthList() {
        return mMonthList;
    }

    List<DailyExpense> getMonthDailyExpenseList() {
        return mMonthDailyExpenseList;
    }

    List<ExpenseItem> getMonthExpenseList() {
        return mMonthExpenseList;
    }

    List<MonthCategoryExpense> getMonthCategoryExpenseList() {
        return mMonthCategoryExpenseList;
    }

    Month getDisplayMonth() {
        return mDisplayMonth;
    }

    private void loadMonthExpense(int year, int month, Callback<List<ExpenseItem>, IError> callback) {
        LOGI(TAG, "load " + year + "/" + month + " daily expense");
        new AsyncTask<Void, Void, List<ExpenseItem>>() {
            @Override
            protected List<ExpenseItem> doInBackground(Void... params) {
                Calendar monthStartCalendar = Calendar.getInstance();
                monthStartCalendar.set(Calendar.YEAR, year);
                // month -1 的原因是, Calendar MONTH 从 0 开始计数，即 0 是一月
                monthStartCalendar.set(Calendar.MONTH, month - 1);
                monthStartCalendar.set(Calendar.DAY_OF_MONTH, 1);
                monthStartCalendar.set(Calendar.HOUR_OF_DAY, 0);
                monthStartCalendar.set(Calendar.MINUTE, 0);
                monthStartCalendar.set(Calendar.SECOND, 0);

                long monthStartDate = monthStartCalendar.getTimeInMillis();
                monthStartCalendar.set(Calendar.MONTH, month);
                long nextMonthStartDate = monthStartCalendar.getTimeInMillis();

                String selection = TallyContract.Expense.TIME + ">=? " +
                        "AND " + TallyContract.Expense.TIME + "<?";
                String[] selectionArgs = new String[]{
                        String.valueOf(monthStartDate),
                        String.valueOf(nextMonthStartDate)};
                String order = "expense_time DESC";
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
            protected void onPostExecute(List<ExpenseItem> dailyExpenses) {
                callback.success(dailyExpenses);
            }
        }.executeOnExecutor(AsyncTaskExecutor.executor());
    }

    private List<DailyExpense> generateDailyExpense(List<ExpenseItem> list) {
        SparseArray<DailyExpense> dailyExpenseSparseArray = new SparseArray<>(31);
        Calendar calendar = Calendar.getInstance();
        for (ExpenseItem item : list) {
            calendar.setTimeInMillis(item.getTime());
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DailyExpense expense = dailyExpenseSparseArray.get(day, null);
            if (expense == null) {
                expense = new DailyExpense();
                dailyExpenseSparseArray.put(day, expense);
            }
            expense.setDayOfMonth(day);
            expense.setExpense(expense.getExpense() + item.getAmount());
        }

        List<DailyExpense> results = new ArrayList<>(31);
        for (int i = 0; i < 32; i++) {
            DailyExpense expense = dailyExpenseSparseArray.get(i, null);
            if (expense != null) {
                results.add(expense);
            }
        }
        return results;
    }

    private List<MonthCategoryExpense> generateMonthCategoryList(int year,
                                                                 int month,
                                                                 List<ExpenseItem> list) {
        List<MonthCategoryExpense> result = new ArrayList<>();

        HashMap<Long, MonthCategoryExpense> getMonthCategoryExpenseByCid = new HashMap<>();

        float monthExpenseTotal = 0.0f;
        for (ExpenseItem item : list) {
            long cid = item.getCategoryId();
            MonthCategoryExpense expense = getMonthCategoryExpenseByCid.get(cid);
            if (expense == null) {
                expense = new MonthCategoryExpense();
                getMonthCategoryExpenseByCid.put(cid, expense);

                expense.setMonth(new Month(year, month));
                expense.setCategoryId(cid);
                expense.setCategoryIcon(item.getCategoryIconResId());
                expense.setCategoryName(item.getCategoryName());
            }
            expense.setCategoryExpenseTotal(expense.getCategoryExpenseTotal() + item.getAmount());
            monthExpenseTotal += item.getAmount();
        }
        result.addAll(getMonthCategoryExpenseByCid.values());
        Collections.sort(result, (o1, o2) -> {
            if (o1.getCategoryExpenseTotal() > o2.getCategoryExpenseTotal()) {
                return -1;
            }
            if (o1.getCategoryExpenseTotal() < o2.getCategoryExpenseTotal()) {
                return 1;
            }
            return 0;
        });
        for (MonthCategoryExpense expense : result) {
            expense.setMonthExpenseTotal(monthExpenseTotal);
        }
        return result;
    }

    private void loadMonthArray(Callback<List<Month>, IError> callback) {
        new AsyncTask<Void, Void, IResult<List<Month>, IError>>() {
            @Override
            protected Result<List<Month>, IError> doInBackground(Void... params) {
                Result result = new Result();
                String order = "expense_time ASC LIMIT 1";
                Cursor cursor = mContext.getContentResolver().query(
                        TallyContract.Expense.CONTENT_URI, null, null, null, order);
                if (cursor == null) {
                    result.setError(new NonThrowError(-1, "read failed"));
                    return result;
                }
                long startTime = System.currentTimeMillis();
                if (cursor.moveToFirst()) {
                    long firstExpenseTime =
                            cursor.getLong(cursor.getColumnIndex(TallyContract.Expense.TIME));
                    startTime = firstExpenseTime;
                }
                Calendar calendar = Calendar.getInstance();
                int currentYear = calendar.get(Calendar.YEAR);
                int currentMonth = calendar.get(Calendar.MONTH) + 1;
                calendar.setTimeInMillis(startTime);
                int startYear = calendar.get(Calendar.YEAR);
                int startMonth = calendar.get(Calendar.MONTH) + 1;

                List<Month> monthList = new ArrayList<>();
                result.setData(monthList);

                if (startYear == currentYear) {
                    for (int month = startMonth; month <= currentMonth; month++) {
                        Month m = new Month();
                        m.setYear(currentYear);
                        m.setMonth(month);
                        monthList.add(m);
                    }
                    return result;
                }

                for (int year = startYear; year <= currentYear; year++) {
                    if (year == startYear) {
                        for (int month = startMonth; month <= 12; month++) {
                            Month m = new Month();
                            m.setYear(year);
                            m.setMonth(month);
                            monthList.add(m);
                        }
                    }
                    if (year > startYear && year < currentYear) {
                        for (int month = 1; month <= 12; month++) {
                            Month m = new Month();
                            m.setYear(year);
                            m.setMonth(month);
                            monthList.add(m);
                        }
                    }
                    if (year == currentYear) {
                        for (int month = startMonth; month <= currentMonth; month++) {
                            Month m = new Month();
                            m.setYear(year);
                            m.setMonth(month);
                            monthList.add(m);
                        }
                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(IResult<List<Month>, IError> result) {
                if (result.isOk()) {
                    callback.success(result.data());
                } else {
                    callback.failure(result.error());
                }
            }
        }.executeOnExecutor(AsyncTaskExecutor.executor());
    }

    @Override
    public void cleanUp() {

    }

    public enum ChartQueryEnum implements QueryEnum {
        LOAD_CURRENT_MONTH_DATA(1, null);
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

        ChartQueryEnum(int id, String[] projection) {
            this.id = id;
            this.projection = projection;
        }
    }

    public enum ChartUserActionEnum implements UserActionEnum {
        // 显示历史月份选择列表
        SHOW_HISTORY_MONTH_LIST(1),
        // 切换显示的月份
        SWITCH_MONTH(2);
        private int id;

        @Override
        public int getId() {
            return id;
        }

        ChartUserActionEnum(int id) {
            this.id = id;
        }
    }
}
