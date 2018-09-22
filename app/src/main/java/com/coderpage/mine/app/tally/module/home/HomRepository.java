package com.coderpage.mine.app.tally.module.home;

import android.util.Pair;

import com.coderpage.base.common.Callback;
import com.coderpage.base.common.IError;
import com.coderpage.base.common.NonThrowError;
import com.coderpage.base.common.Result;
import com.coderpage.base.common.SimpleCallback;
import com.coderpage.base.error.ErrorCode;
import com.coderpage.concurrency.MineExecutors;
import com.coderpage.mine.app.tally.persistence.model.Expense;
import com.coderpage.mine.app.tally.persistence.model.Income;
import com.coderpage.mine.app.tally.persistence.sql.TallyDatabase;
import com.coderpage.mine.app.tally.persistence.sql.entity.ExpenseEntity;
import com.coderpage.mine.app.tally.persistence.sql.entity.InComeEntity;
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
    /** 今日消费记录 */
    private List<Expense> mTodayExpenseList = new ArrayList<>();
    /** 今日支出记录 */
    private List<Income> mTodayInComeList = new ArrayList<>();

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

    List<Expense> getTodayExpenseList() {
        return mTodayExpenseList;
    }

    List<Income> getTodayInComeList() {
        return mTodayInComeList;
    }

    /** 读取本月消费数据 */
    void loadCurrentMonthExpenseData(SimpleCallback<Result<Boolean, IError>> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            // 今日开始时间&结束时间
            long todayStartTime = DateUtils.todayStartUnixTime();
            long todayEndTime = DateUtils.todayEndUnixTime();
            // 本月开始时间&结束时间
            long monthStartTime = DateUtils.currentMonthStartUnixTime();
            long monthEndTime = System.currentTimeMillis();

            // 本月消费记录列表
            List<Expense> expenseList =
                    TallyDatabase.getInstance().expenseDao().queryBetweenTimeTimeDesc(monthStartTime, monthEndTime);
            // 本月支出记录
            List<Income> incomeList =
                    TallyDatabase.getInstance().incomeDao().queryBetweenTimeTimeDesc(monthStartTime, monthEndTime);

            if (expenseList != null) {
                // 今日消费记录
                List<Expense> todayExpenseList = new ArrayList<>();
                // 分类消费总额缓存
                Map<String, Double> getAmountByCategoryName = new HashMap<>();
                // 月消费总额
                double monthTotalAmount = 0;
                // 今日消费金额
                double todayTotalAmount = 0;

                for (Expense expense : expenseList) {
                    // 统计月消费总额
                    monthTotalAmount += expense.getAmount();
                    // 统计今日消费记录及总额
                    if (expense.getTime() >= todayStartTime && expense.getTime() <= todayEndTime) {
                        todayExpenseList.add(expense);
                        todayTotalAmount += expense.getAmount();
                    }
                    // 统计分类消费记录
                    Double categoryAmountTotal = getAmountByCategoryName.get(expense.getCategoryName());
                    if (categoryAmountTotal == null) {
                        categoryAmountTotal = 0.0D;
                    }
                    categoryAmountTotal += expense.getAmount();
                    getAmountByCategoryName.put(expense.getCategoryName(), categoryAmountTotal);
                }

                mCurrentMonthExpenseTotalAmount = monthTotalAmount;
                mTodayExpenseTotalAmount = todayTotalAmount;
                mTodayExpenseList.clear();
                mTodayExpenseList.addAll(todayExpenseList);

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
            }

            if (incomeList != null) {
                // 今日支出记录
                List<Income> todayIncomeList = new ArrayList<>();
                // 月消费总额
                double monthTotalAmount = 0;
                // 今日消费金额
                double todayTotalAmount = 0;

                for (Income income : incomeList) {
                    // 统计月消费总额
                    monthTotalAmount += income.getAmount();
                    // 统计今日消费记录及总额
                    if (income.getTime() >= todayStartTime && income.getTime() <= todayEndTime) {
                        todayIncomeList.add(income);
                        todayTotalAmount += income.getAmount();
                    }
                }

                mCurrentMonthInComeTotalAmount = monthTotalAmount;
                mTodayInComeTotalAmount = todayTotalAmount;
                mTodayInComeList.clear();
                mTodayInComeList.addAll(todayIncomeList);
            }

            Result<Boolean, IError> result = new Result<>();
            result.setData(true);
            MineExecutors.executeOnUiThread(() -> callback.success(result));
        });
    }

    /** 删除消费记录 */
    void deleteExpense(long expenseId, Callback<Void, IError> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            try {
                ExpenseEntity entity = new ExpenseEntity();
                entity.setId(expenseId);
                TallyDatabase.getInstance().expenseDao().delete(entity);
                MineExecutors.executeOnUiThread(() -> callback.success(null));
            } catch (Exception e) {
                MineExecutors.executeOnUiThread(() -> callback.failure(new NonThrowError(ErrorCode.SQL_ERR, "SQL ERR")));
            }
        });
    }

    /** 删除收入记录 */
    void deleteInCome(long incomeId, Callback<Void, IError> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            try {
                InComeEntity entity = new InComeEntity();
                entity.setId(incomeId);
                TallyDatabase.getInstance().incomeDao().delete(entity);
                MineExecutors.executeOnUiThread(() -> callback.success(null));
            } catch (Exception e) {
                MineExecutors.executeOnUiThread(() -> callback.failure(new NonThrowError(ErrorCode.SQL_ERR, "SQL ERR")));
            }
        });
    }
}
