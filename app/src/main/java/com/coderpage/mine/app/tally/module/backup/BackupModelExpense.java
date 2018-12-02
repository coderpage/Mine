package com.coderpage.mine.app.tally.module.backup;

import android.support.annotation.Keep;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author abner-l. 2017-06-01
 */
@Keep
public class BackupModelExpense {

    /** 金额 */
    @JSONField(name = "1")
    private double amount;
    /** 备注 */
    @JSONField(name = "2")
    private String desc;
    /** 分类名称 */
    @JSONField(name = "3")
    private String category;
    /** 记录时间 */
    @JSONField(name = "4")
    private long time;
    /** 同步 ID */
    @JSONField(name = "5")
    private String syncId;
    /** 用户 ID */
    @JSONField(name = "6")
    private long accountId;
    /** 分类唯一名称 */
    @JSONField(name = "7")
    private String categoryUniqueName;
    /** 同步状态 */
    @JSONField(name = "8")
    private int syncStatus;

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getSyncId() {
        return syncId;
    }

    public void setSyncId(String syncId) {
        this.syncId = syncId;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getCategoryUniqueName() {
        return categoryUniqueName;
    }

    public void setCategoryUniqueName(String categoryUniqueName) {
        this.categoryUniqueName = categoryUniqueName;
    }

    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }
}
