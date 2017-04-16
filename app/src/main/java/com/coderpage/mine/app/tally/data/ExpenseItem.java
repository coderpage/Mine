package com.coderpage.mine.app.tally.data;

import android.database.Cursor;

import com.coderpage.mine.app.tally.provider.TallyContract;

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

    public static ExpenseItem fromCursor(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(TallyContract.Expense._ID));
        long categoryId = cursor.getLong(cursor.getColumnIndex(TallyContract.Expense.CATEGORY_ID));
        float amount = cursor.getFloat(cursor.getColumnIndex(TallyContract.Expense.AMOUNT));
        String categoryName = cursor.getString(cursor.getColumnIndex(TallyContract.Expense.CATEGORY));
        String desc = cursor.getString(cursor.getColumnIndex(TallyContract.Expense.DESC));
        long time = cursor.getLong(cursor.getColumnIndex(TallyContract.Expense.TIME));
        String categoryIcon = cursor.getString(cursor.getColumnIndex(TallyContract.Category.ICON));

        ExpenseItem item = new ExpenseItem();
        item.setId(id);
        item.setCategoryId(categoryId);
        item.setAmount(amount);
        item.setCategoryName(categoryName);
        item.setDesc(desc);
        item.setTime(time);
        item.setCategoryIconResId(CategoryIconHelper.resId(categoryIcon));
        return item;
    }

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
