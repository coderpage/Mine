package com.coderpage.mine.app.tally.chart.data;

/**
 * @author abner-l. 2017-05-07
 */

public class Month {
    private int year;
    private int month;

    public Month() {
    }

    public Month(int year, int month) {
        this.year = year;
        this.month = month;
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
}
