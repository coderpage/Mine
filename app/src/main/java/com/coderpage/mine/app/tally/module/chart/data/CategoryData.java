package com.coderpage.mine.app.tally.module.chart.data;

/**
 * @author lc. 2018-10-09 17:52
 * @since 0.6.0
 */

public class CategoryData {

    /** 开始时间 */
    private long startDate;
    /** 结束时间 */
    private long endDate;
    /** 分类 ID */
    private long categoryId;
    /** 分类图标 */
    private int categoryIcon;
    /** 分类名称 */
    private String categoryName;
    /** 时间区间内总额总额 */
    private double amountTotal;
    /** 时间区间内该分类消费总额 */
    private double amount;

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
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

    public double getAmountTotal() {
        return amountTotal;
    }

    public void setAmountTotal(double amountTotal) {
        this.amountTotal = amountTotal;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
