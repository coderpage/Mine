package com.coderpage.mine.app.tally.module.backup;

import java.io.File;

/**
 * @author lc. 2019-05-19 22:34
 * @since 0.6.2
 */

public class BackupFileManagerItem {

    private long createTime;
    private long size;
    private String name;
    private String path;
    private File file;

    public BackupFileManagerItem(File file) {
        this.file = file;
        if (file == null) {
            return;
        }
        this.size = file.length();
        this.name = file.getName();
        this.createTime = file.lastModified();
        this.path = file.getPath();
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

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
