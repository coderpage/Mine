package com.coderpage.mine.app.tally.update;

import com.alibaba.fastjson.annotation.JSONField;
import com.coderpage.lib.update.ApkModel;

/**
 * @author lc. 2017-10-04 13:34
 * @since 0.5.0
 */

public class LatestVersion implements ApkModel {

    @JSONField(name = "appName")
    private String appName;
    @JSONField(name = "packageName")
    private String packageName;
    @JSONField(name = "changeLog")
    private String changeLog;
    @JSONField(name = "versionCode")
    private int versionCode;
    @JSONField(name = "versionName")
    private String versionName;
    @JSONField(name = "downloadUrl")
    private String downloadUrl;
    @JSONField(name = "fileSize")
    private long fileSize;
    @JSONField(name = "isRelease")
    private boolean isRelease;
    @JSONField(name = "uploader")
    private String uploader;
    @JSONField(name = "updateDate")
    private long updateDate;
    @JSONField(name = "uploadAvatar")
    private String uploadAvatar;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getChangeLog() {
        return changeLog;
    }

    public void setChangeLog(String changeLog) {
        this.changeLog = changeLog;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isRelease() {
        return isRelease;
    }

    public void setRelease(boolean release) {
        isRelease = release;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public String getUploadAvatar() {
        return uploadAvatar;
    }

    public void setUploadAvatar(String uploadAvatar) {
        this.uploadAvatar = uploadAvatar;
    }

    @Override
    public long getBuildCode() {
        return versionCode;
    }

    @Override
    public String getVersion() {
        return versionName;
    }

    @Override
    public String getName() {
        return appName;
    }

    @Override
    public String getChangelog() {
        return changeLog;
    }

    @Override
    public String getDownloadUrl() {
        return downloadUrl;
    }

    @Override
    public long getApkSizeBytes() {
        return fileSize;
    }
}
