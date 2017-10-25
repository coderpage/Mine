package com.coderpage.mine.app.tally.chart.data;

import java.util.Locale;

/**
 * @author lc. 2017-10-19 20:42
 * @since 0.5.1
 */

public class MonthlyExpense {

    private Month month;
    private float total;

    public Month getMonth() {
        return month;
    }

    public void setMonth(Month month) {
        this.month = month;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "[month=%s,total=%f]", String.valueOf(month), total);
    }
}
