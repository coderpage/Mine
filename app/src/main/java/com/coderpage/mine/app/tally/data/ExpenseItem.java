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
    private long accountId;
    private String syncId;
    private boolean synced;

    public static ExpenseItem fromCursor(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(TallyContract.Expense._ID));
        long categoryId = cursor.getLong(cursor.getColumnIndex(TallyContract.Expense.CATEGORY_ID));
        float amount = cursor.getFloat(cursor.getColumnIndex(TallyContract.Expense.AMOUNT));
        String categoryName = cursor.getString(cursor.getColumnIndex(TallyContract.Expense.CATEGORY));
        String desc = cursor.getString(cursor.getColumnIndex(TallyContract.Expense.DESC));
        long time = cursor.getLong(cursor.getColumnIndex(TallyContract.Expense.TIME));
        String categoryIcon = cursor.getString(cursor.getColumnIndex(TallyContract.Category.ICON));
        long accountId = cursor.getLong(cursor.getColumnIndex(TallyContract.Expense.ACCOUNT_ID));
        String syncId = cursor.getString(cursor.getColumnIndex(TallyContract.Expense.SYNC_ID));
        int synced = cursor.getInt(cursor.getColumnIndex(TallyContract.Expense.SYNCED));

        ExpenseItem item = new ExpenseItem();
        item.setId(id);
        item.setCategoryId(categoryId);
        item.setAmount(amount);
        item.setCategoryName(categoryName);
        item.setDesc(desc);
        item.setTime(time);
        item.setCategoryIconResId(CategoryIconHelper.resId(categoryIcon));
        item.setAccountId(accountId);
        item.setSyncId(syncId);
        item.setSynced(synced == 1);
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

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getSyncId() {
        return syncId;
    }

    public void setSyncId(String syncId) {
        this.syncId = syncId;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }
}
