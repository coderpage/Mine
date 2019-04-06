package com.coderpage.mine.app.tally.module.chart;

import com.coderpage.base.common.Callback;
import com.coderpage.base.common.IError;
import com.coderpage.concurrency.MineExecutors;
import com.coderpage.mine.app.tally.module.chart.data.CategoryData;
import com.coderpage.mine.app.tally.module.chart.data.DailyData;
import com.coderpage.mine.app.tally.module.chart.data.Month;
import com.coderpage.mine.app.tally.module.chart.data.MonthlyData;
import com.coderpage.mine.app.tally.persistence.model.Record;
import com.coderpage.mine.app.tally.persistence.model.RecordCategoryGroup;
import com.coderpage.mine.app.tally.persistence.model.RecordGroup;
import com.coderpage.mine.app.tally.persistence.sql.TallyDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * @author lc. 2018-09-24 15:35
 * @since 0.6.0
 */

class TallyChartRepository {

    /**
     * 查询第一个记录时间
     *
     * @param callback 回调
     */
    void queryFirstRecordTime(Callback<Long, IError> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            long firstTime = System.currentTimeMillis();
            Record recordFirst = TallyDatabase.getInstance().recordDao().queryFirst();
            if (recordFirst != null) {
                firstTime = recordFirst.getTime();
            }
            callback.success(firstTime);
        });
    }

    /**
     * 查询指定时间区间内的日消费数据
     *
     * @param start    开始时间
     * @param end      结束时间
     * @param callback 回调
     */
    void queryDailyExpense(long start, long end, Callback<List<DailyData>, IError> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            List<RecordGroup> expenseList = TallyDatabase.getInstance().recordDao()
                    .queryExpenseDailyGroup(start, end);

            Calendar calendar = Calendar.getInstance();
            List<DailyData> dailyList = new ArrayList<>(expenseList.size());
            for (RecordGroup dailyGroup : expenseList) {
                calendar.setTimeInMillis(dailyGroup.getTime());
                DailyData dailyData = new DailyData();
                dailyData.setAmount(dailyGroup.getAmount());
                dailyData.setYear(calendar.get(Calendar.YEAR));
                dailyData.setMonth(calendar.get(Calendar.MONTH) + 1);
                dailyData.setDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
                dailyData.setTimeMillis(dailyGroup.getTime());
                dailyList.add(dailyData);
            }
            callback.success(dailyList);
        });
    }

    /**
     * 查询指定时间区间内的日收入数据
     *
     * @param start    开始时间
     * @param end      结束时间
     * @param callback 回调
     */
    void queryDailyInCome(long start, long end, Callback<List<DailyData>, IError> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            List<RecordGroup> incomeGroupList = TallyDatabase.getInstance().recordDao()
                    .queryIncomeDailyGroup(start, end);

            Calendar calendar = Calendar.getInstance();
            List<DailyData> dailyList = new ArrayList<>(incomeGroupList.size());
            for (RecordGroup dailyGroup : incomeGroupList) {
                calendar.setTimeInMillis(dailyGroup.getTime());
                DailyData dailyData = new DailyData();
                dailyData.setAmount(dailyGroup.getAmount());
                dailyData.setYear(calendar.get(Calendar.YEAR));
                dailyData.setMonth(calendar.get(Calendar.MONTH) + 1);
                dailyData.setDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
                dailyData.setTimeMillis(dailyGroup.getTime());
                dailyList.add(dailyData);
            }

            callback.success(dailyList);
        });
    }

    /**
     * 查询指定区间内月单位支出数据
     *
     * @param callback 回调
     */
    void queryMonthlyExpense(long start, long end, Callback<List<MonthlyData>, IError> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            List<MonthlyData> result = new ArrayList<>();
            List<RecordGroup> expenseMonthGroups = TallyDatabase.getInstance()
                    .recordDao().queryExpenseMonthGroup(start, end);

            Calendar calendar = Calendar.getInstance();
            for (RecordGroup group : expenseMonthGroups) {
                long time = group.getTime();
                float sum = group.getAmount();

                calendar.setTimeInMillis(time);
                int year = calendar.get(Calendar.YEAR);
                int monthOfYear = calendar.get(Calendar.MONTH) + 1;
                Month month = new Month(year, monthOfYear);
                MonthlyData monthlyExpense = new MonthlyData();
                monthlyExpense.setMonth(month);
                monthlyExpense.setAmount(sum);

                result.add(monthlyExpense);
            }
            callback.success(result);
        });
    }

    /**
     * 查询指定区间内月单位收入数据
     *
     * @param callback 回调
     */
    void queryMonthlyIncome(long start, long end, Callback<List<MonthlyData>, IError> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            List<MonthlyData> result = new ArrayList<>();
            List<RecordGroup> incomeMonthGroups = TallyDatabase.getInstance()
                    .recordDao().queryIncomeMonthGroup(start, end);

            Calendar calendar = Calendar.getInstance();
            for (RecordGroup group : incomeMonthGroups) {
                long time = group.getTime();
                float sum = group.getAmount();

                calendar.setTimeInMillis(time);
                int year = calendar.get(Calendar.YEAR);
                int monthOfYear = calendar.get(Calendar.MONTH) + 1;
                Month month = new Month(year, monthOfYear);
                MonthlyData monthlyExpense = new MonthlyData();
                monthlyExpense.setMonth(month);
                monthlyExpense.setAmount(sum);

                result.add(monthlyExpense);
            }
            callback.success(result);
        });
    }

    /**
     * 查询消费分类消费数据
     *
     * @param start    查询开始时间
     * @param end      查询结束时间
     * @param callback 回调
     */
    void queryCategoryExpense(long start, long end, Callback<List<CategoryData>, IError> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            List<CategoryData> result = new ArrayList<>();

            List<RecordCategoryGroup> expenseCategoryGroups = TallyDatabase.getInstance()
                    .recordDao().queryExpenseCategoryGroup(start, end);

            double amountTotal = 0;
            for (RecordCategoryGroup group : expenseCategoryGroups) {
                CategoryData categoryData = new CategoryData();
                categoryData.setType(group.getType());
                categoryData.setStartDate(start);
                categoryData.setEndDate(end);
                categoryData.setCategoryId(group.getCategoryId());
                categoryData.setCategoryIconName(group.getIcon());
                categoryData.setCategoryName(group.getName());
                categoryData.setCategoryUniqueName(group.getUniqueName());
                categoryData.setAmount(group.getAmount());
                result.add(categoryData);

                amountTotal += group.getAmount();
            }

            for (CategoryData data : result) {
                data.setAmountTotal(amountTotal);
            }
            Collections.sort(result, (c1, c2) -> {
                if (c1.getAmountTotal() == c2.getAmountTotal()) {
                    return 0;
                }
                return c1.getAmountTotal() > c2.getAmountTotal() ? -1 : 1;
            });

            callback.success(result);
        });
    }

    /**
     * 查询收入分类消费数据
     *
     * @param start    查询开始时间
     * @param end      查询结束时间
     * @param callback 回调
     */
    void queryCategoryIncome(long start, long end, Callback<List<CategoryData>, IError> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            List<CategoryData> result = new ArrayList<>();

            List<RecordCategoryGroup> incomeCategoryGroups = TallyDatabase.getInstance()
                    .recordDao().queryIncomeCategoryGroup(start, end);

            double amountTotal = 0;
            for (RecordCategoryGroup group : incomeCategoryGroups) {
                CategoryData categoryData = new CategoryData();
                categoryData.setType(group.getType());
                categoryData.setStartDate(start);
                categoryData.setEndDate(end);
                categoryData.setCategoryId(group.getCategoryId());
                categoryData.setCategoryIconName(group.getIcon());
                categoryData.setCategoryName(group.getName());
                categoryData.setCategoryUniqueName(group.getUniqueName());
                categoryData.setAmount(group.getAmount());
                result.add(categoryData);

                amountTotal += group.getAmount();
            }

            for (CategoryData data : result) {
                data.setAmountTotal(amountTotal);
            }
            Collections.sort(result, (c1, c2) -> {
                if (c1.getAmountTotal() == c2.getAmountTotal()) {
                    return 0;
                }
                return c1.getAmountTotal() > c2.getAmountTotal() ? -1 : 1;
            });

            callback.success(result);
        });
    }

    void queryMonthList(Callback<List<Month>, IError> callback) {
        MineExecutors.ioExecutor().execute(() -> {

        });
    }
}
