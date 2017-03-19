package com.coderpage.mine.app.tally;

/**
 * @author abner-l. 2017-02-03
 */

public class ExpenseItem {

    private long id;
    private long categoryId;
    private float amount;
    private String categoryName;
    private String desc;
    private long time;
    private int categoryIconResId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String category) {
        this.categoryName = category;
    }

    public int getCategoryIconResId() {
        return categoryIconResId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setCategoryIconResId(int categoryIconResId) {
        this.categoryIconResId = categoryIconResId;
    }
}
