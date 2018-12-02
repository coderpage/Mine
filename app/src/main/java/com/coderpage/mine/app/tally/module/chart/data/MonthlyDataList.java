package com.coderpage.mine.app.tally.module.chart.data;

import java.util.List;

/**
 * @author lc. 2018-10-20 22:46
 * @since 0.6.0
 */

public class MonthlyDataList {

    private List<MonthlyData> expenseList;
    private List<MonthlyData> incomeList;

    public List<MonthlyData> getExpenseList() {
        return expenseList;
    }

    public void setExpenseList(List<MonthlyData> expenseList) {
        this.expenseList = expenseList;
    }

    public List<MonthlyData> getIncomeList() {
        return incomeList;
    }

    public void setIncomeList(List<MonthlyData> incomeList) {
        this.incomeList = incomeList;
    }
}
