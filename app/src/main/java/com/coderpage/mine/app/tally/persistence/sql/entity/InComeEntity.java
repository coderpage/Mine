package com.coderpage.mine.app.tally.persistence.sql.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * @author : liuchao
 *         created on 2018/5/27 上午9:19
 *         description : 收入表
 */
@Entity(tableName = "income", indices = {@Index(value = {"income_sync_id"}, unique = true)})
public class InComeEntity {

    /** 数据表自增 ID */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "income_id")
    private long id;

    /** 分类 ID */
    @ColumnInfo(name = "income_category_id")
    private long categoryId;

    /** 账户 ID */
    @ColumnInfo(name = "income_account_id")
    private long accountId;

    /** 记录时间（UNIX TIME） */
    @ColumnInfo(name = "income_time")
    private long time;

    /** 金额 */
    @ColumnInfo(name = "income_amount")
    private double amount;

    /** 分类唯一名称，不可变 */
    @ColumnInfo(name = "income_category_unique_name")
    private String categoryUniqueName;

    /** 备注 */
    @NonNull
    @ColumnInfo(name = "income_desc")
    private String desc = "";

    /** 同步 ID */
    @NonNull
    @ColumnInfo(name = "income_sync_id")
    private String syncId = "";

    /** 同步状态 */
    @ColumnInfo(name = "income_sync_status")
    private int syncStatus;

    /** 是否删除 */
    @ColumnInfo(name = "income_delete")
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
