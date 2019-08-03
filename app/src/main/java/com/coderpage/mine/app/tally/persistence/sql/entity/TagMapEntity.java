package com.coderpage.mine.app.tally.persistence.sql.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * @author lc. 2019-07-18 22:34
 * @since 0.7.0
 *
 * RECORD-TAG 对应表
 */

@Entity(tableName = "tag_map", indices = {@Index(value = {"tag_map_record_uuid"})})
public class TagMapEntity {

    @PrimaryKey()
    @ColumnInfo(name = "tag_map_id")
    private String id;

    @ColumnInfo(name = "tag_map_record_uuid")
    private String recordUuid;

    @ColumnInfo(name = "tag_map_tag_name")
    private String tagName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getRecordUuid() {
        return recordUuid;
    }

    public void setRecordUuid(String recordUuid) {
        this.recordUuid = recordUuid;
    }

}
