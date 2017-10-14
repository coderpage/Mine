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
