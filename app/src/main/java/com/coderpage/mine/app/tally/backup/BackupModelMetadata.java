package com.coderpage.mine.app.tally.backup;

import android.support.annotation.Keep;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author abner-l. 2017-06-01
 * @since 0.4.0
 *
 * 备份 JSON 文件元数据。
 */

@Keep
public class BackupModelMetadata {

    /**
     * 执行备份操作时客户端应用版本；
     */
    @JSONField(name = "client_version")
    private String clientVersion;
    /**
     * 应用的名称
     */
    @JSONField(name = "device_name")
    private String deviceName;
    /**
     * 备份日期
     */
    @JSONField(name = "backup_date")
    private long backupDate;
    /**
     * 包含的消费记录数量
     */
    @JSONField(name = "expense_number")
    private long expenseNumber;

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public long getBackupDate() {
        return backupDate;
    }

    public void setBackupDate(long backupDate) {
        this.backupDate = backupDate;
    }

    public long getExpenseNumber() {
        return expenseNumber;
    }

    public void setExpenseNumber(long expenseNumber) {
        this.expenseNumber = expenseNumber;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
