package com.coderpage.mine.app.tally.module.chart.data;

import com.coderpage.mine.app.tally.persistence.model.CategoryModel;

import java.text.DecimalFormat;

/**
 * @author lc. 2018-10-09 17:52
 * @since 0.6.0
 */

public class CategoryData {

    public static final int TYPE_INCOME = CategoryModel.TYPE_INCOME;
    public static final int TYPE_EXPENSE = CategoryModel.TYPE_EXPENSE;

    private DecimalFormat mPercentFormat = new DecimalFormat("0.00");

    /**
     * 分类类型「支出 or 收入」
     *
     * @see #TYPE_INCOME
     * @see #TYPE_EXPENSE
     */
    private int type;

    /** 开始时间 */
    private long startDate;
    /** 结束时间 */
    private long endDate;
    /** 分类 ID */
    private long categoryId;
    /** 分类名称 */
    private String categoryName;
    /** 分类唯一标识 */
    private String categoryUniqueName;
    /** 时间区间内总额总额 */
    private double amountTotal;
    /** 时间区间内该分类消费总额 */
    private double amount;
    /** 分类图标名称 */
    private String categoryIconName;

    /** 返回该分类金额 */
    public double getPercentRate() {
        if (amountTotal == 0) {
            return 0;
        }
        return amount / amountTotal * 100;
    }

    public String getPercentRateString() {
        return mPercentFormat.format(getPercentRate());
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

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

    public String getCategoryIconName() {
        return categoryIconName;
    }

    public void setCategoryIconName(String categoryIconName) {
        this.categoryIconName = categoryIconName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryUniqueName() {
        return categoryUniqueName;
    }

    public void setCategoryUniqueName(String categoryUniqueName) {
        this.categoryUniqueName = categoryUniqueName;
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
