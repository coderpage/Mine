package com.coderpage.lib.update;

/**
 * @author lc. 2017-09-23 23:43
 * @since 0.5.0
 */

public class ApkModelImpl implements ApkModel {
    /**
     * 编译构建代码
     */
    private long buildCode;

    /**
     * 应用名称
     */
    private String name;
    /**
     * 版本号
     */
    private String version;
    /**
     * 更新日志
     */
    private String changelog;
    /**
     * APK 下载地址
     */
    private String downloadUrl;

    private long apkSizeBytes;

    @Override
    public long getBuildCode() {
        return buildCode;
    }

    public void setBuildCode(long buildCode) {
        this.buildCode = buildCode;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String getChangelog() {
        return changelog;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }

    @Override
    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    @Override
    public long getApkSizeBytes() {
        return apkSizeBytes;
    }

    public void setApkSizeBytes(long apkSizeBytes) {
        this.apkSizeBytes = apkSizeBytes;
    }
}
