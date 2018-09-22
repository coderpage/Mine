package com.coderpage.mine.app.tally.persistence.sql.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * @author lc 2018-05-01 16:15
 * @since 0.6.0
 */
@Entity(tableName = "expense", indices = {@Index(value = {"expense_sync_id"}, unique = true)})
public class ExpenseEntity {

    /** 数据表自增 ID */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "expense_id")
    private long id;

    /** 分类 ID */
    @ColumnInfo(name = "expense_category_id")
    private long categoryId;

    /** 账户 ID */
    @ColumnInfo(name = "expense_account_id")
    private long accountId;

    /** 记录时间（UNIX TIME） */
    @ColumnInfo(name = "expense_time")
    private long time;

    /** 金额 */
    @ColumnInfo(name = "expense_amount")
    private double amount;

    /** 分类唯一名称，不可变 */
    @ColumnInfo(name = "expense_category_unique_name")
    private String categoryUniqueName;

    /** 备注 */
    @NonNull
    @ColumnInfo(name = "expense_desc")
    private String desc = "";

    /** 同步 ID */
    @NonNull
    @ColumnInfo(name = "expense_sync_id")
    private String syncId = "";

    /** 同步状态 */
    @ColumnInfo(name = "expense_sync_status")
    private int syncStatus;

    /** 是否删除 */
    @ColumnInfo(name = "expense_delete")
    private int delete;

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

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSyncId() {
        return syncId;
    }

    public void setSyncId(String syncId) {
        this.syncId = syncId;
    }

    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getCategoryUniqueName() {
        return categoryUniqueName;
    }

    public void setCategoryUniqueName(String categoryUniqueName) {
        this.categoryUniqueName = categoryUniqueName;
    }

    public int getDelete() {
        return delete;
    }

    public void setDelete(int delete) {
        this.delete = delete;
    }
}
