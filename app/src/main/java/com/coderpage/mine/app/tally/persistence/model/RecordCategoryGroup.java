package com.coderpage.mine.app.tally.persistence.model;

import android.arch.persistence.room.ColumnInfo;

/**
 * @author lc.
 * @since 0.6.0
 */

public class RecordCategoryGroup {

    /**
     * 该分类类型
     *
     * @see com.coderpage.mine.app.tally.persistence.sql.entity.CategoryEntity#TYPE_EXPENSE
     * @see com.coderpage.mine.app.tally.persistence.sql.entity.CategoryEntity#TYPE_INCOME
     */
    @ColumnInfo(name = "category_type")
    private int type;

    /** 该分类 ID */
    @ColumnInfo(name = "category_id")
    private long categoryId;

    /** 该分类记录数量 */
    @ColumnInfo(name = "count(*)")
    private long count;

    /** 总金额 */
    @ColumnInfo(name = "sum(record_amount)")
    private double amount;

    /** 分类唯一不变名称 */
    @ColumnInfo(name = "category_unique_name")
    private String uniqueName;

    /** 分类名称 */
    @ColumnInfo(name = "category_name")
    private String name;

    /** 分类图标 */
    @ColumnInfo(name = "category_icon")
    private String icon;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
