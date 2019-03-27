package com.coderpage.mine.app.tally.module.records;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import com.coderpage.base.utils.ArrayUtils;
import com.coderpage.mine.app.tally.persistence.model.Record;
import com.coderpage.mine.app.tally.persistence.sql.entity.CategoryEntity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author lc. 2018-12-20
 * @since 0.6.0
 */

public class RecordQuery implements Parcelable {

    /** 记录类型：全部 */
    public static final int TYPE_ALL = -1;
    /** 记录类型：支出 */
    public static final int TYPE_EXPENSE = Record.TYPE_EXPENSE;
    /** 记录类型：收入 */
    public static final int TYPE_INCOME =  Record.TYPE_INCOME;

    @IntDef(flag = true, value = {TYPE_ALL, TYPE_EXPENSE, TYPE_INCOME})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    public static final RecordQuery DEFAULT = new Builder().build();

    /**
     * 查询指定的记录类型
     *
     * @see #TYPE_ALL
     * @see #TYPE_EXPENSE
     * @see #TYPE_INCOME
     */
    private int type;

    /** 开始时间 */
    private long startTime;

    /** 结束时间 */
    private long endTime;

    /**
     * 指定的分类
     *
     * {@link CategoryEntity#uniqueName}
     */
    private String[] categoryUniqueNameArray;

    public int getType() {
        return type;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public String[] getCategoryUniqueNameArray() {
        return categoryUniqueNameArray;
    }

    String buildSql() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("record_time >= ").append(startTime).append(" and record_time <= ").append(endTime);
        if (type == TYPE_EXPENSE) {
            sqlBuilder.append(" and record_type = ").append(Record.TYPE_EXPENSE);
        }
        if (type == TYPE_INCOME) {
            sqlBuilder.append(" and record_type = ").append(Record.TYPE_INCOME);
        }
        if (categoryUniqueNameArray != null && categoryUniqueNameArray.length > 0) {
            String inArray = ArrayUtils.join(",", String::valueOf, categoryUniqueNameArray);
            sqlBuilder.append("and record_category_unique_name in (").append(inArray).append(")");
        }
        return sqlBuilder.toString();
    }


    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    private RecordQuery() {
    }

    private RecordQuery(Parcel src) {
        type = src.readInt();
        startTime = src.readLong();
        endTime = src.readLong();
        categoryUniqueNameArray = src.createStringArray();
    }

    public static final Creator<RecordQuery> CREATOR = new Creator<RecordQuery>() {
        @Override
        public RecordQuery createFromParcel(Parcel source) {
            return new RecordQuery(source);
        }

        @Override
        public RecordQuery[] newArray(int size) {
            return new RecordQuery[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeLong(startTime);
        dest.writeLong(endTime);
        dest.writeStringArray(categoryUniqueNameArray);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Inner class Builder
    ///////////////////////////////////////////////////////////////////////////

    /** Builder */
    public static class Builder {
        private int type;
        private long startTime;
        private long endTime;
        private String[] categoryUniqueNameArray;

        public Builder() {
            type = TYPE_ALL;
            startTime = 0;
            endTime = System.currentTimeMillis();
        }

        /**
         * 设置查询指定的类型
         *
         * @param type 类型
         * @see #TYPE_ALL
         * @see #TYPE_EXPENSE
         * @see #TYPE_INCOME
         */
        public Builder setType(@Type int type) {
            this.type = type;
            return this;
        }

        /**
         * 设置记录开始时间
         *
         * @param startTime 开始时间
         */
        public Builder setStartTime(long startTime) {
            this.startTime = startTime;
            return this;
        }

        /**
         * 设置记录结束时间
         *
         * @param endTime 结束时间
         */
        public Builder setEndTime(long endTime) {
            this.endTime = endTime;
            return this;
        }

        /**
         * 设置指定查询的所有分类
         *
         * @param categoryUniqueNameArray 分类名称数组 {@link CategoryEntity#uniqueName}
         */
        public Builder setCategoryUniqueNameArray(String[] categoryUniqueNameArray) {
            this.categoryUniqueNameArray = categoryUniqueNameArray;
            return this;
        }

        public RecordQuery build() {
            RecordQuery recordQuery = new RecordQuery();
            recordQuery.type = type;
            recordQuery.startTime = startTime;
            recordQuery.endTime = endTime;
            recordQuery.categoryUniqueNameArray = categoryUniqueNameArray;
            return recordQuery;
        }
    }
}
