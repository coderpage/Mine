package com.coderpage.mine.app.tally.module.detail;

/**
 * @author lc. 2018-09-22 22:38
 * @since 0.6.0
 */

public class RecordData {

    private int type;
    /** 记录 id */
    private long recordId;
    /** 记录 金额 */
    private String amount;
    /** 分类图标 */
    private String categoryIcon;
    /** 分类名称 */
    private String categoryName;
    /** 记录说明 */
    private String desc;
    /** 记录时间 */
    private String time;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getRecordId() {
        return recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(String categoryIcon) {
        this.categoryIcon = categoryIcon;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
