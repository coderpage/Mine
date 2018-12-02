package com.coderpage.mine.app.tally.module.chart.data;

/**
 * @author abner-l. 2017-05-07
 */

public class DailyData {

    /** 时间戳 unix millis */
    private long timeMillis;
    /** 年 */
    private int year;
    /** 月 1~12*/
    private int month;
    /** 当月的第几天 */
    private int dayOfMonth;
    /** 日总金额 */
    private float amount;

    public DailyData() {
    }

    public long getTimeMillis() {
        return timeMillis;
    }

    public void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
