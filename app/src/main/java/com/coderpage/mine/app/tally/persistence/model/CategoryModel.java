package com.coderpage.mine.app.tally.persistence.model;

import android.arch.persistence.room.ColumnInfo;

/**
 * @author lc. 2018-08-29 19:37
 * @since 0.6.0
 *
 * 消费分类 ITEM
 */

public class CategoryModel {

    /** 分类记录 ID */
    @ColumnInfo(name = "category_id")
    private long id;

    /** 分类唯一不变名称 */
    @ColumnInfo(name = "category_unique_name")
    private String uniqueName = "";

    /** 分类名称 */
    @ColumnInfo(name = "category_name")
    private String name = "";

    /** 图标 */
    @ColumnInfo(name = "category_icon")
    private String icon = "";

    /** 排序 */
    @ColumnInfo(name = "category_order")
    private int order;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
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
}
