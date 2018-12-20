package com.coderpage.mine.app.tally.persistence.model;

import android.arch.persistence.room.ColumnInfo;

import com.coderpage.mine.app.tally.persistence.sql.entity.RecordEntity;

/**
 * @author lc.
 * @since 0.6.0
 */

public class Record {

    /** 记录类型: 支出 */
    public static final int TYPE_EXPENSE = RecordEntity.TYPE_EXPENSE;
    /** 记录类型: 收入 */
    public static final int TYPE_INCOME = RecordEntity.TYPE_INCOME;

    public Record() {
    }

    @ColumnInfo(name = "record_id")
    private long id;

    /** 账户 ID */
    @ColumnInfo(name = "record_account_id")
    private long accountId;

    /** 记录时间（UNIX TIME） */
    @ColumnInfo(name = "record_time")
    private long time;

    /** 分类唯一不变名称 */
    @ColumnInfo(name = "record_category_unique_name")
    private String categoryUniqueName;

    /** 分类名称 */
    @ColumnInfo(name = "category_name")
    private String categoryName;

    /** 分类图标名称 */
    @ColumnInfo(name = "category_icon")
    private String categoryIcon;

    /** 金额 */
    @ColumnInfo(name = "record_amount")
    private double amount;

    /** 备注 */
    @ColumnInfo(name = "record_desc")
    private String desc;

    /** 同步 ID */
    @ColumnInfo(name = "record_sync_id")
    private String syncId;

    /** 同步状态 */
    @ColumnInfo(name = "record_sync_status")
    private int syncStatus;

    /** 记录类型 */
    @ColumnInfo(name = "record_type")
    private int type;

    public RecordEntity createEntity() {
        RecordEntity entity = new RecordEntity();
        entity.setId(getId());
        entity.setAccountId(getAccountId());
        entity.setAmount(getAmount());
        entity.setDesc(getDesc());
        entity.setSyncId(getSyncId());
        entity.setSyncStatus(getSyncStatus());
        entity.setTime(getTime());
        entity.setCategoryUniqueName(getCategoryUniqueName());
        entity.setType(getType());
        return entity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
