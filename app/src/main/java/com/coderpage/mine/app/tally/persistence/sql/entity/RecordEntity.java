package com.coderpage.mine.app.tally.persistence.sql.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * @author lc.
 * @since 0.6.0
 */
@Entity(tableName = "record", indices = {@Index(value = {"record_sync_id"}, unique = true)})
public class RecordEntity {

    /** 记录类型: 支出 */
    public static final int TYPE_EXPENSE = 0;
    /** 记录类型: 收入 */
    public static final int TYPE_INCOME = 1;

    /** 数据表自增 ID */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "record_id")
    private long id;

    /** 账户 ID */
    @ColumnInfo(name = "record_account_id")
    private long accountId;

    /** 记录时间（UNIX TIME） */
    @ColumnInfo(name = "record_time")
    private long time;

    /** 金额 */
    @ColumnInfo(name = "record_amount")
    private double amount;

    /** 分类唯一名称，不可变 */
    @ColumnInfo(name = "record_category_unique_name")
    private String categoryUniqueName;

    /** 备注 */
    @NonNull
    @ColumnInfo(name = "record_desc")
    private String desc = "";

    /** 同步 ID */
    @NonNull
    @ColumnInfo(name = "record_sync_id")
    private String syncId = "";

    /** 同步状态 */
    @ColumnInfo(name = "record_sync_status")
    private int syncStatus;

    /** 是否删除 */
    @ColumnInfo(name = "record_delete")
    private int delete;

    /** 记录类型 */
    @ColumnInfo(name = "record_type")
    private int type;

    /** TAG 标签 */
    @ColumnInfo(name = "record_tag_array")
    private String tagArrayStr;

    /** 修改时间 */
    @ColumnInfo(name = "record_update_time")
    private long updateTime;

    /** 同步版本 */
    @ColumnInfo(name = "record_sync_version")
    private long syncVersion;

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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategoryUniqueName() {
        return categoryUniqueName;
    }

    public void setCategoryUniqueName(String categoryUniqueName) {
        this.categoryUniqueName = categoryUniqueName;
    }

    @NonNull
    public String getDesc() {
        return desc;
    }

    public void setDesc(@NonNull String desc) {
        this.desc = desc;
    }

    @NonNull
    public String getSyncId() {
        return syncId;
    }

    public void setSyncId(@NonNull String syncId) {
        this.syncId = syncId;
    }

    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }

    public int getDelete() {
        return delete;
    }

    public void setDelete(int delete) {
        this.delete = delete;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTagArrayStr() {
        return tagArrayStr;
    }

    public void setTagArrayStr(String tagArrayStr) {
        this.tagArrayStr = tagArrayStr;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public long getSyncVersion() {
        return syncVersion;
    }

    public void setSyncVersion(long syncVersion) {
        this.syncVersion = syncVersion;
    }
}
