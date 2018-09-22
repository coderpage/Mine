package com.coderpage.mine.app.tally.module.backup;

import android.support.annotation.Keep;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author abner-l. 2017-06-01
 * @since 0.4.0
 *
 * 备份 JSON 文件中"分类"数据；
 */

@Keep
public class BackupModelCategory {

    /**
     * 分类的名称
     */
    @JSONField(name = "name")
    private String name;
    /**
     * 分类的图标
     */
    @JSONField(name = "icon")
    private String icon;

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
}
