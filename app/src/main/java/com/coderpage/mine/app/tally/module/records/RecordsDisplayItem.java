package com.coderpage.mine.app.tally.module.records;

import com.coderpage.mine.app.tally.persistence.model.Record;

/**
 * @author lc. 2019-02-13 10:31
 * @since 0.6.0
 */

class RecordsDisplayItem {

    /** 类型：日期标题 */
    static final int TYPE_DATE_TITLE = 1;
    /** 类型：记录 */
    static final int TYPE_RECORD = 2;

    /**
     * 数据类型
     *
     * @see #TYPE_DATE_TITLE
     * @see #TYPE_RECORD
     */
    private int type;
    /** 年份 */
    private int year;
    /** 月份 */
    private int month;
    /** 当数据类型为 {@link #TYPE_RECORD} 时保存记录数据，否则为空 */
    private Record internal;

    RecordsDisplayItem(int year, int month) {
        this.year = year;
        this.month = month;
        this.type = TYPE_DATE_TITLE;
    }

    RecordsDisplayItem(Record record) {
        this.internal = record;
        this.type = TYPE_RECORD;
    }

    public int getType() {
        return type;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public Record getInternal() {
        return internal;
    }
}
