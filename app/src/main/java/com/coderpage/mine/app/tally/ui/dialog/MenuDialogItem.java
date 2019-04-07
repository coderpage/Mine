package com.coderpage.mine.app.tally.ui.dialog;

import android.graphics.drawable.Drawable;

/**
 * @author lc. 2019-04-07 09:47
 * @since 0.6.0
 */

public class MenuDialogItem {

    /** 菜单图标 */
    private Drawable icon;
    /** 菜单名称 */
    private String name;
    /** 路由跳转路径 */
    private String path;

    public MenuDialogItem() {

    }

    public MenuDialogItem(String name, String path, Drawable icon) {
        this.name = name;
        this.path = path;
        this.icon = icon;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
