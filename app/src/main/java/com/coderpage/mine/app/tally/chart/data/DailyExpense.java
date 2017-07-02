package com.coderpage.mine.app.tally.chart.data;

/**
 * @author abner-l. 2017-05-07
 */

public class DailyExpense {

    private long timeMillis;
    private int dayOfMonth;
    private float expense;

    public DailyExpense() {
    }

    public DailyExpense(int dayOfMonth, float expense) {
        this.dayOfMonth = dayOfMonth;
        this.expense = expense;
    }

    public long getTimeMillis() {
        return timeMillis;
    }

    public void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
    }

    public float getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public float getExpense() {
        return expense;
    }

    public void setExpense(float expense) {
        this.expense = expense;
    }
}
