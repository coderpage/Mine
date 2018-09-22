package com.coderpage.mine.app.tally.module.chart.data;

import java.util.Locale;

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

    /**
     * 返回前一月
     */
    public Month pre() {
        int year = this.year;
        int month = this.month - 1;
        if (this.month == 1) {
            year = year - 1;
            month = 12;
        }
        return new Month(year, month);
    }

    /**
     * 返回下一月
     */
    public Month next() {
        int year = this.year;
        int month = this.month + 1;
        if (this.month == 12) {
            year = year + 1;
            month = 1;
        }
        return new Month(year, month);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Month month1 = (Month) o;

        if (year != month1.year) return false;
        return month == month1.month;

    }

    @Override
    public int hashCode() {
        int result = year;
        result = 31 * result + month;
        return result;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "[year=%d,month=%d]", year, month);
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
