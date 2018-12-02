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

    /** 分类类型 */
    @JSONField(name = "type")
    private int type;

    /** 分类的名称 */
    @JSONField(name = "name")
    private String name;

    /** 分类的图标 */
    @JSONField(name = "icon")
    private String icon;

    /** 分类唯一名称 不可修改 */
    @JSONField(name = "uniqueName")
    private String uniqueName;

    /** 用户 ID */
    @JSONField(name = "accountId")
    private long accountId;

    /** 同步状态 */
    @JSONField(name = "syncStatus")
    private int syncStatus;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }
}
