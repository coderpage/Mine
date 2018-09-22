package com.coderpage.mine.app.tally.persistence.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.coderpage.mine.app.tally.persistence.sql.entity.InComeEntity;

/**
 * @author lc. 2018-05-27 14:23
 * @since 0.6.0
 */
public class Income {

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

    /** 分类唯一不变名称 */
    @ColumnInfo(name = "income_category_unique_name")
    private String categoryUniqueName;

    @ColumnInfo(name = "category_name")
    private String categoryName;

    @ColumnInfo(name = "category_icon")
    private String categoryIcon;

    public InComeEntity createEntity(){
        InComeEntity entity = new InComeEntity();
        entity.setId(getId());
        entity.setAccountId(getAccountId());
        entity.setAmount(getAmount());
        entity.setCategoryId(getCategoryId());
        entity.setDesc(getDesc());
        entity.setSyncId(getSyncId());
        entity.setSyncStatus(getSyncStatus());
        entity.setTime(getTime());
        entity.setCategoryUniqueName(getCategoryUniqueName());
        return entity;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(String categoryIcon) {
        this.categoryIcon = categoryIcon;
    }
}
