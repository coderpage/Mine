package com.coderpage.mine.app.tally.data;

import android.database.Cursor;

import com.coderpage.mine.app.tally.provider.TallyContract;

/**
 * @author abner-l. 2017-03-05
 */

public class Category {

    /** 分类 ID，{@link com.coderpage.mine.app.tally.provider.TallyContract.Category#_ID} */
    private long id;
    /** 分类名称，{@link com.coderpage.mine.app.tally.provider.TallyContract.Category#NAME} */
    private String name;
    /** 分类 */
    private int icon;
    private int order;

    public static Category fromCursor(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(TallyContract.Category._ID));
        String name = cursor.getString(cursor.getColumnIndex(TallyContract.Category.NAME));
        String icon = cursor.getString(cursor.getColumnIndex(TallyContract.Category.ICON));
        int order = cursor.getInt(cursor.getColumnIndex(TallyContract.Category.ORDER));
        Category item = new Category();
        item.setIcon(CategoryIconHelper.resId(icon));
        item.setId(id);
        item.setName(name);
        item.setOrder(order);
        return item;
    }

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

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
