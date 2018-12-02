package com.coderpage.mine.app.tally.module.chart.data;

/**
 * @author abner-l. 2017-05-11
 */

public class MonthCategoryExpense {

    /** 月份 */
    private Month month;
    /** 分类 ID */
    private long categoryId;
    /** 分类图标 */
    private int categoryIcon;
    /** 分类名称 */
    private String categoryName;
    /** 月总额 */
    private float monthExpenseTotal;
    /** 该分类消费总额 */
    private float categoryExpenseTotal;

    public Month getMonth() {
        return month;
    }

    public void setMonth(Month month) {
        this.month = month;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public int getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(int categoryIcon) {
        this.categoryIcon = categoryIcon;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public float getMonthExpenseTotal() {
        return monthExpenseTotal;
    }

    public void setMonthExpenseTotal(float monthExpenseTotal) {
        this.monthExpenseTotal = monthExpenseTotal;
    }

    public float getCategoryExpenseTotal() {
        return categoryExpenseTotal;
    }

    public void setCategoryExpenseTotal(float categoryExpenseTotal) {
        this.categoryExpenseTotal = categoryExpenseTotal;
    }
}
