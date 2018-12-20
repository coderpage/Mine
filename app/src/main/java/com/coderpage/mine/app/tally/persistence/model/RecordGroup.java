package com.coderpage.mine.app.tally.persistence.model;

import android.arch.persistence.room.ColumnInfo;

/**
 * @author lc.
 * @since 0.6.0
 */

public class RecordGroup {

    /** 记录数量 */
    @ColumnInfo(name = "count(*)")
    private long count;

    /** 时间 */
    @ColumnInfo(name = "record_time")
    private long time;

    /** 总金额 */
    @ColumnInfo(name = "sum(record_amount)")
    private float amount;

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
