package com.coderpage.mine.app.tally.backup;

import android.support.annotation.Keep;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author abner-l. 2017-06-01
 */
@Keep
public class BackupModelExpense {

    @JSONField(name = "1")
    private float amount;
    @JSONField(name = "2")
    private String desc;
    @JSONField(name = "3")
    private String category;
    @JSONField(name = "4")
    private long time;
    @JSONField(name = "5")
    private String syncId;
    @JSONField(name = "6")
    private long accountId;

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
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
}
