package com.coderpage.mine.app.tally.module.chart;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.coderpage.base.common.Callback;
import com.coderpage.base.common.IError;
import com.coderpage.base.common.IResult;
import com.coderpage.base.common.NonThrowError;
import com.coderpage.base.common.Result;
import com.coderpage.concurrency.AsyncTaskExecutor;
import com.coderpage.framework.Model;
import com.coderpage.framework.QueryEnum;
import com.coderpage.framework.UserActionEnum;
import com.coderpage.mine.app.tally.module.chart.data.DailyData;
import com.coderpage.mine.app.tally.module.chart.data.Month;
import com.coderpage.mine.app.tally.module.chart.data.MonthCategoryExpense;
import com.coderpage.mine.app.tally.module.chart.data.MonthlyData;
import com.coderpage.mine.app.tally.data.Expense;
import com.coderpage.mine.app.tally.provider.TallyContract;
import com.coderpage.mine.app.tally.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.coderpage.base.utils.LogUtils.LOGI;
import static com.coderpage.base.utils.LogUtils.makeLogTag;

/**
 * @author abner-l. 2017-05-04
 */

class ChartModel implements Model<ChartModel.ChartQueryEnum, ChartModel.ChartUserActionEnum, ChartModel, IError> {
    private static final String TAG = makeLogTag(ChartModel.class);

    static final String EXTRA_YEAR = "extra_year"; // bundle extra year
    static final String EXTRA_MONTH = "extra_month"; // bundle extra month

    private Context mContext;
    private Month mDisplayMonth;
    private List<Month> mMonthList = new ArrayList<>();
    private List<Expense> mMonthExpenseList = new ArrayList<>();
    private List<DailyData> mMonthDailyExpenseList = new ArrayList<>();
    private List<MonthCategoryExpense> mMonthCategoryExpenseList = new ArrayList<>();
    private List<MonthlyData> mMonthlyExpenseList = new ArrayList<>();

    ChartModel(Context context) {
        mContext = context;
    }

    List<Month> getHistoryMonthList() {
        return mMonthList;
    }

    List<DailyData> getMonthDailyExpenseList() {
        return mMonthDailyExpenseList;
    }

    List<Expense> getMonthExpenseList() {
        return mMonthExpenseList;
    }

    List<MonthCategoryExpense> getMonthCategoryExpenseList() {
        return mMonthCategoryExpenseList;
    }

    public List<MonthlyData> getMonthlyExpenseList() {
        return mMonthlyExpenseList;
    }

    Month getDisplayMonth() {
        return mDisplayMonth;
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
    public void requestData(ChartQueryEnum query,
                            DataQueryCallback<ChartModel, ChartQueryEnum, IError> callback) {
        switch (query) {
            case LOAD_CURRENT_MONTH_DATA:
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                loadMonthExpense(year, month, new Callback<List<Expense>, IError>() {
                    @Override
                    public void success(List<Expense> items) {
                        mDisplayMonth = new Month(year, month);
                        mMonthExpenseList.clear();
                        mMonthExpenseList.addAll(items);
                        mMonthDailyExpenseList.clear();
                        mMonthDailyExpenseList.addAll(generateDailyExpense(year, month, items));
                        mMonthCategoryExpenseList.clear();
                        mMonthCategoryExpenseList.addAll(
                                generateMonthCategoryList(year, month, items));
                        callback.onModelUpdated(ChartModel.this, query);
                    }

                    @Override
                    public void failure(IError iError) {
                        callback.onError(query, iError);
                    }
                });
                break;
            default:
                break;
        }
    }


    @Override
    public void deliverUserAction(ChartUserActionEnum action,
                                  @Nullable Bundle args,
                                  UserActionCallback<ChartModel, ChartUserActionEnum, IError> callback) {
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
                        callback.onError(action, iError);
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
                loadMonthExpense(year, month, new Callback<List<Expense>, IError>() {
                    @Override
                    public void success(List<Expense> items) {
                        mDisplayMonth = new Month(year, month);
                        mMonthExpenseList.clear();
                        mMonthExpenseList.addAll(items);
                        mMonthDailyExpenseList.clear();
                        mMonthDailyExpenseList.addAll(generateDailyExpense(year, month, items));
                        mMonthCategoryExpenseList.clear();
                        mMonthCategoryExpenseList.addAll(
                                generateMonthCategoryList(year, month, items));
                        callback.onModelUpdated(ChartModel.this, action);
                    }

                    @Override
                    public void failure(IError iError) {
                        callback.onError(action, iError);
                    }
                });
                break;

            // 切换到 月消费 折线图
            case SWITCH_TO_MONTHLY_DATA:
                if (!mMonthlyExpenseList.isEmpty()) {
                    callback.onModelUpdated(ChartModel.this, action);
                    break;
                }
                loadMonthlyExpenseData(new Callback<List<MonthlyData>, IError>() {
                    @Override
                    public void success(List<MonthlyData> monthlyExpenses) {
                        mMonthlyExpenseList.clear();
                        mMonthlyExpenseList.addAll(monthlyExpenses);
                        callback.onModelUpdated(ChartModel.this, action);
                    }

                    @Override
                    public void failure(IError iError) {
                        callback.onError(action, iError);
                    }
                });
                break;

            // 切换到 日消费 折线图
            case SWITCH_TO_DAILY_DATA:
                callback.onModelUpdated(ChartModel.this, action);
                break;

            default:
                break;
        }
    }


    private void loadMonthExpense(int year, int month, Callback<List<Expense>, IError> callback) {
        LOGI(TAG, "load " + year + "/" + month + " daily expense");
        new AsyncTask<Void, Void, List<Expense>>() {
            @Override
            protected List<Expense> doInBackground(Void... params) {
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
                List<Expense> items = new ArrayList<>(cursor.getCount());
                while (cursor.moveToNext()) {
                    items.add(Expense.fromCursor(cursor));
                }
                cursor.close();
                return items;
            }

            @Override
            protected void onPostExecute(List<Expense> dailyExpenses) {
                callback.success(dailyExpenses);
            }
        }.executeOnExecutor(AsyncTaskExecutor.executor());
    }

    private void loadMonthlyExpenseData(Callback<List<MonthlyData>, IError> callback) {
        new AsyncTask<Void, Void, List<MonthlyData>>() {
            @Override
            protected List<MonthlyData> doInBackground(Void... params) {
                List<MonthlyData> result = new ArrayList<>();

                // 完整查询 SQL 如下所示
                // SELECT sum(expense_amount), expense_time FROM expense group by
                // strftime('%Y-%m', datetime(expense_time/1000, 'unixepoch', 'localtime'));
                String[] projection = new String[]{
                        String.format("sum(%s)", TallyContract.Expense.AMOUNT),
                        TallyContract.Expense.TIME};
                String orderBy = TallyContract.Expense.TIME + " ASC";
                // 参考 http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2014/1103/1895.html
                String groupBy = String.format(
                        "0=0) group by (strftime('%%Y-%%m', datetime(%s/1000, 'unixepoch', 'localtime'))",
                        TallyContract.Expense.TIME);
                Cursor cursor = mContext.getContentResolver().query(
                        TallyContract.Expense.CONTENT_URI,
                        projection,
                        groupBy,
                        null,
                        orderBy);

                if (cursor == null) {
                    return result;
                }
                Calendar calendar = Calendar.getInstance();
                while (cursor.moveToNext()) {
                    float sum = cursor.getFloat(cursor.getColumnIndex(
                            String.format("sum(%s)", TallyContract.Expense.AMOUNT)));
                    long time = cursor.getLong(cursor.getColumnIndex(TallyContract.Expense.TIME));

                    calendar.setTimeInMillis(time);
                    int year = calendar.get(Calendar.YEAR);
                    int monthOfYear = calendar.get(Calendar.MONTH) + 1;
                    Month month = new Month(year, monthOfYear);
                    MonthlyData monthlyExpense = new MonthlyData();
                    monthlyExpense.setMonth(month);
                    monthlyExpense.setAmount(sum);

                    result.add(monthlyExpense);
                }

                cursor.close();
                return result;
            }

            @Override
            protected void onPostExecute(List<MonthlyData> monthlyExpenses) {
                callback.success(monthlyExpenses);
            }
        }.executeOnExecutor(AsyncTaskExecutor.executor());
    }

    private List<DailyData> generateDailyExpense(int year, int month, List<Expense> list) {
        SparseArray<DailyData> dailyExpenseSparseArray = new SparseArray<>(31);
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        for (Expense item : list) {
            calendar.setTimeInMillis(item.getTime());
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DailyData expense = dailyExpenseSparseArray.get(day, null);
            if (expense == null) {
                expense = new DailyData();
                dailyExpenseSparseArray.put(day, expense);
            }
            expense.setTimeMillis(item.getTime());
            expense.setDayOfMonth(day);
            expense.setAmount(expense.getAmount() + item.getAmount());
        }

        int daysOfMonth;
        if (currentYear == year && currentMonth == month) {
            daysOfMonth = currentDay;
        } else {
            daysOfMonth = TimeUtils.getDaysTotalOfMonth(year, month);
        }
        List<DailyData> results = new ArrayList<>(daysOfMonth);
        for (int i = 1; i <= daysOfMonth; i++) {
            DailyData expense = dailyExpenseSparseArray.get(i, null);
            if (expense == null) {
                calendar.set(Calendar.MONTH, month - 1);
                calendar.set(Calendar.DAY_OF_MONTH, i);
                expense = new DailyData();
                expense.setTimeMillis(calendar.getTimeInMillis());
                expense.setDayOfMonth(i);
                expense.setAmount(0);
            }
            results.add(expense);
        }
        return results;
    }

    private List<MonthCategoryExpense> generateMonthCategoryList(int year,
                                                                 int month,
                                                                 List<Expense> list) {
        List<MonthCategoryExpense> result = new ArrayList<>();

        HashMap<Long, MonthCategoryExpense> getMonthCategoryExpenseByCid = new HashMap<>();

        float monthExpenseTotal = 0.0f;
        for (Expense item : list) {
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
                Result<List<Month>, IError> result = new Result<>();

                // 完整查询 SQL 如下所示
                // SELECT expense_time FROM expense group by
                // strftime('%Y-%m', datetime(expense_time/1000, 'unixepoch', 'localtime'));
                String[] projection = new String[]{TallyContract.Expense.TIME};
                String orderBy = TallyContract.Expense.TIME + " ASC";
                String groupBy = String.format(
                        "0=0) group by (strftime('%%Y-%%m', datetime(%s/1000, 'unixepoch', 'localtime'))",
                        TallyContract.Expense.TIME);
                Cursor cursor = mContext.getContentResolver().query(
                        TallyContract.Expense.CONTENT_URI,
                        projection,
                        groupBy,
                        null,
                        orderBy);

                if (cursor == null) {
                    result.setError(new NonThrowError(-1, "read failed"));
                    return result;
                }

                List<Month> monthList = new ArrayList<>();
                result.setData(monthList);

                Calendar calendar = Calendar.getInstance();
                while (cursor.moveToNext()) {
                    long time = cursor.getLong(cursor.getColumnIndex(TallyContract.Expense.TIME));

                    calendar.setTimeInMillis(time);
                    int year = calendar.get(Calendar.YEAR);
                    int monthOfYear = calendar.get(Calendar.MONTH) + 1;
                    Month month = new Month(year, monthOfYear);
                    monthList.add(month);
                }

                cursor.close();
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

    enum ChartQueryEnum implements QueryEnum {
        /**
         * 读取当前月的消费数据
         */
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

    enum ChartUserActionEnum implements UserActionEnum {
        // 显示历史月份选择列表
        SHOW_HISTORY_MONTH_LIST(1),
        // 切换显示的月份
        SWITCH_MONTH(2),
        // 切换到日消费折线图
        SWITCH_TO_DAILY_DATA(3),
        // 切换到月消费折线图
        SWITCH_TO_MONTHLY_DATA(4);
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
