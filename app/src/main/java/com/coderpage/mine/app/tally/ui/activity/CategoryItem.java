package com.coderpage.mine.app.tally.ui.activity;

import android.graphics.drawable.Drawable;

/**
 * @author abner-l. 2017-03-05
 */

public class CategoryItem {

    private long id;
    private String name;
    private Drawable icon;
    private int order;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
