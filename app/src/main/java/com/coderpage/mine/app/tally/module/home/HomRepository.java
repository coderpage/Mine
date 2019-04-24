package com.coderpage.mine.app.tally.module.home;

import android.util.Pair;

import com.coderpage.base.common.IError;
import com.coderpage.base.common.Result;
import com.coderpage.base.common.SimpleCallback;
import com.coderpage.concurrency.MineExecutors;
import com.coderpage.mine.app.tally.persistence.model.Record;
import com.coderpage.mine.app.tally.persistence.sql.TallyDatabase;
import com.coderpage.mine.app.tally.utils.DateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lc. 2018-07-08 15:25
 * @since 0.6.0
 */

class HomRepository {

    /** 近3日账单数量 */
    private int mRecent3DayRecordCount;
    /** 今日消费总额 */
    private double mTodayExpenseTotalAmount;
    /** 今日收入总额 */
    private double mTodayInComeTotalAmount;
    /** 本月消费总额 */
    private double mCurrentMonthExpenseTotalAmount;
    /** 本月收入总额 */
    private double mCurrentMonthInComeTotalAmount;
    /** 本月各个分类消费数据 */
    private List<Pair<String, Double>> mCategoryExpenseTotal = new ArrayList<>();
    /** 近3日账单记录 */
    private List<Record> mRecent3DayRecordList = new ArrayList<>();
    /** 今日账单记录 */
    private List<Record> mTodayRecordList = new ArrayList<>();

    public int getRecent3DayRecordCount() {
        return mRecent3DayRecordCount;
    }

    double getTodayExpenseTotalAmount() {
        return mTodayExpenseTotalAmount;
    }

    double getTodayInComeTotalAmount() {
        return mTodayInComeTotalAmount;
    }

    double getCurrentMonthExpenseTotalAmount() {
        return mCurrentMonthExpenseTotalAmount;
    }

    double getCurrentMonthInComeTotalAmount() {
        return mCurrentMonthInComeTotalAmount;
    }

    List<Pair<String, Double>> getCategoryExpenseTotal() {
        return mCategoryExpenseTotal;
    }

    List<Record> getRecent3DayRecordList() {
        return mRecent3DayRecordList;
    }

    List<Record> getTodayRecordList() {
        return mTodayRecordList;
    }

    /** 读取本月消费数据 */
    void loadCurrentMonthExpenseData(SimpleCallback<Result<Boolean, IError>> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            // 近3日账单数量
            int recent3DayRecordCount = 0;
            // 今日开始时间&结束时间
            long todayStartTime = DateUtils.todayStartUnixTime();
            long todayEndTime = DateUtils.todayEndUnixTime();
            // 前天开始时间
            long day3AgoStartTime = todayStartTime - (1000 * 60 * 60 * 24 * 2);
            // 本月开始时间&结束时间
            long monthStartTime = DateUtils.currentMonthStartUnixTime();
            long monthEndTime = System.currentTimeMillis();
            // 月消费总额
            double monthExpenseTotalAmount = 0;
            // 月收入总额
            double monthIncomeTotalAmount = 0;
            // 今日消费金额
            double todayExpenseTotalAmount = 0;
            // 今日收入金额
            double todayIncomeTotalAmount = 0;

            // 近3日账单记录
            List<Record> recent3DayList = new ArrayList<>();
            // 今日账单记录
            List<Record> todayRecordList = new ArrayList<>();
            Map<String, Double> getAmountByCategoryName = new HashMap<>();
            // 本月消费记录列表
            List<Record> currentMonthList =
                    TallyDatabase.getInstance().recordDao().queryAll(monthStartTime, monthEndTime, Integer.MAX_VALUE, 0);
            for (Record record : currentMonthList) {
                boolean isTodayRecord = record.getTime() >= todayStartTime && record.getTime() < todayEndTime;
                boolean isRecent3DayRecord = record.getTime() >= day3AgoStartTime && record.getTime() < todayEndTime;

                // 今日消费
                if (isTodayRecord) {
                    if (record.getType() == Record.TYPE_EXPENSE) {
                        todayExpenseTotalAmount += record.getAmount();
                    }
                    if (record.getType() == Record.TYPE_INCOME) {
                        todayIncomeTotalAmount += record.getAmount();
                    }
                    todayRecordList.add(record);
                }

                // 近3日账单数据
                if (isRecent3DayRecord) {
                    recent3DayRecordCount++;
                    recent3DayList.add(record);
                }

                // 本月账单总额
                if (record.getType() == Record.TYPE_EXPENSE) {
                    monthExpenseTotalAmount += record.getAmount();
                }
                if (record.getType() == Record.TYPE_INCOME) {
                    monthIncomeTotalAmount += record.getAmount();
                }

                // 统计分类消费记录
                if (record.getType() == Record.TYPE_EXPENSE) {
                    Double categoryAmountTotal = getAmountByCategoryName.get(record.getCategoryName());
                    if (categoryAmountTotal == null) {
                        categoryAmountTotal = 0.0D;
                    }
                    categoryAmountTotal += record.getAmount();
                    getAmountByCategoryName.put(record.getCategoryName(), categoryAmountTotal);
                }
            }

            mRecent3DayRecordCount = recent3DayRecordCount;
            mTodayExpenseTotalAmount = todayExpenseTotalAmount;
            mTodayInComeTotalAmount = todayIncomeTotalAmount;
            mCurrentMonthExpenseTotalAmount = monthExpenseTotalAmount;
            mCurrentMonthInComeTotalAmount = monthIncomeTotalAmount;
            mRecent3DayRecordList.clear();
            mRecent3DayRecordList.addAll(recent3DayList);
            mTodayRecordList.clear();
            mTodayRecordList.addAll(todayRecordList);

            List<Pair<String, Double>> categoryExpenseTotal = new ArrayList<>(getAmountByCategoryName.size());
            for (Map.Entry<String, Double> entry : getAmountByCategoryName.entrySet()) {
                categoryExpenseTotal.add(new Pair<>(entry.getKey(), entry.getValue()));
            }
            Collections.sort(categoryExpenseTotal, (entry1, entry2) -> {
                double v1 = entry1.second == null ? 0 : entry1.second;
                double v2 = entry2.second == null ? 0 : entry2.second;
                if (v1 == v2) {
                    return 0;
                }
                return v1 > v2 ? -1 : 1;
            });
            mCategoryExpenseTotal.clear();
            mCategoryExpenseTotal.addAll(categoryExpenseTotal);

            Result<Boolean, IError> result = new Result<>();
            result.setData(true);
            MineExecutors.executeOnUiThread(() -> callback.success(result));
        });
    }
}
