package com.coderpage.mine.app.tally.module.records;

/**
 * @author lc. 2019-02-13 10:29
 * @since 0.6.0
 */

public class RecordsDateTitle {
    private int year;
    private int month;

    RecordsDateTitle(int year, int month) {
        this.year = year;
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }
}
