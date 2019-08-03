package com.coderpage.mine.app.tally.persistence.sql.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * @author lc. 2019-07-18 22:54
 * @since 0.7.0
 *
 * 标签表
 */

@Entity(tableName = "tag", indices = {@Index(value = {"tag_name"})})
public class TagEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tag_id")
    private long id;

    @ColumnInfo(name = "tag_name")
    private String name;


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

}
