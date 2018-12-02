package com.coderpage.mine.app.tally.persistence.sql.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * @author lc. 2018-05-20 14:27
 * @since 0.6.0
 */

@Entity(tableName = "category", indices = {@Index(value = {"category_unique_name"}, unique = true)})
public class CategoryEntity {

    public static final int TYPE_EXPENSE = 0;
    public static final int TYPE_INCOME = 1;

    /** 分类记录 ID */
    @ColumnInfo(name = "category_id")
    @PrimaryKey(autoGenerate = true)
    private long id;

    /** 分类唯一名称 不可修改 */
    @NonNull
    @ColumnInfo(name = "category_unique_name")
    private String uniqueName;

    /** 分类名称 */
    @ColumnInfo(name = "category_name")
    private String name = "";

    /** 图标 */
    @NonNull
    @ColumnInfo(name = "category_icon")
    private String icon = "";

    /** 排序 */
    @ColumnInfo(name = "category_order")
    private int order;

    /** 分类类型 */
    @ColumnInfo(name = "category_type")
    private int type;

    /** 用户 ID */
    @ColumnInfo(name = "category_account_id")
    private long accountId;

    /** 同步状态 */
    @ColumnInfo(name = "category_sync_status")
    private int syncStatus;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @NonNull
    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(@NonNull String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }
}
