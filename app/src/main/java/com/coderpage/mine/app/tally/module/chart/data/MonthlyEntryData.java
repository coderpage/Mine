package com.coderpage.mine.app.tally.module.chart.data;

/**
 * @author lc. 2019-04-13 12:44
 * @since 0.6.0
 */

public class MonthlyEntryData {

    private Month month;
    private double expenseAmount;
    private double incomeAmount;

    public Month getMonth() {
        return month;
    }

    public MonthlyEntryData setMonth(Month month) {
        this.month = month;
        return this;
    }

    public double getExpenseAmount() {
        return expenseAmount;
    }

    public MonthlyEntryData setExpenseAmount(double expenseAmount) {
        this.expenseAmount = expenseAmount;
        return this;
    }

    public double getIncomeAmount() {
        return incomeAmount;
    }

    public MonthlyEntryData setIncomeAmount(double incomeAmount) {
        this.incomeAmount = incomeAmount;
        return this;
    }
}
