package com.coderpage.mine.app.tally.persistence.sql.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RoomWarnings;
import android.arch.persistence.room.Update;

import com.coderpage.mine.app.tally.persistence.model.Record;
import com.coderpage.mine.app.tally.persistence.model.RecordCategoryGroup;
import com.coderpage.mine.app.tally.persistence.model.RecordGroup;
import com.coderpage.mine.app.tally.persistence.sql.entity.RecordEntity;

import java.util.List;

/**
 * @author lc. 2018-05-20 15:27
 * @since 0.6.0
 */

@Dao
public interface ExpenseDao {
    /***
     * 通过 ID 查询支出记录
     *
     * @param id 支出记录 ID
     *
     * @return 查询到的记录
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select * " +
            "from record " +
            "left outer join category on record.record_category_unique_name=category.category_unique_name " +
            "where record_id = :id")
    Record queryById(long id);

    /**
     * 插入记录
     *
     * @param expense 记录
     * @return 记录 ID
     */
    @Insert
    long insert(RecordEntity expense);

    /**
     * 批量插入记录
     *
     * @param expense 记录
     * @return 记录 ID
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long[] insert(RecordEntity... expense);

    /**
     * 更新记录
     *
     * @param expense 记录
     * @return 更新数量
     */
    @Update
    int update(RecordEntity expense);

    /**
     * 删除记录
     *
     * @param expense 记录
     */
    @Delete
    void delete(RecordEntity expense);

    /***
     * 查询指定时间区间支出记录
     *
     * @param start 开始时间
     * @param end 结束时间
     *
     * @return 查询到的所有记录
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select * " +
            "from record " +
            "left outer join category on record.record_category_unique_name=category.category_unique_name " +
            "where record_time >= :start and record_time <= :end and record_type = 0 ")
    List<Record> queryBetweenTime(long start, long end);

    /***
     * 查询指定时间区间支出记录，按日期降序排序
     *
     * @param start 开始时间
     * @param end 结束时间
     *
     * @return 查询到的所有记录
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select * " +
            "from record " +
            "left outer join category on record.record_category_unique_name=category.category_unique_name " +
            "where record_time >= :start and record_time<= :end and record_type = 0 " +
            "order by record_time DESC")
    List<Record> queryBetweenTimeTimeDesc(long start, long end);

    /**
     * 查询第一笔支出记录
     *
     * @return 第一笔支出记录
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select * from record where record_type = 0 order by record_time ASC limit 1")
    Record queryFirst();

    /**
     * 查询指定时间区间内的月支出数据
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 查询到的月支出数据
     */
    @Query("select count(*),sum(record_amount),record_time " +
            "from record " +
            "where record_time >= :start and record_time<= :end and record_type = 0 " +
            "group by strftime('%Y-%m', datetime(record_time/1000, 'unixepoch', 'localtime')) " +
            "order by record_time ASC")
    List<RecordGroup> queryExpenseMonthGroup(long start, long end);

    /**
     * 查询指定时间区间内的日支出数据
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 查询到的日支出数据
     */
    @Query("select count(*),sum(record_amount),record_time " +
            "from record " +
            "where record_time >= :start and record_time<= :end and record_type = 0 " +
            "group by strftime('%Y-%m-%d', datetime(record_time/1000, 'unixepoch', 'localtime')) " +
            "order by record_time ASC")
    List<RecordGroup> queryExpenseDailyGroup(long start, long end);

    /**
     * 查询指定时间区间内的分类支出数据
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 查询到的分类支出数据
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select category.category_id,count(*),sum(record_amount),record_time,category_name,category_icon " +
            "from record " +
            "left outer join category on record.record_category_unique_name=category.category_unique_name " +
            "where record_time >= :start and record_time<= :end and record_type = 0 " +
            "group by category.category_id " +
            "order by sum(record_amount) ASC")
    List<RecordCategoryGroup> queryExpenseCategoryGroup(long start, long end);


    /***
     * 查询所有支出记录
     *
     * @return 查询到的所有记录
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select * " +
            "from record " +
            "left outer join category on record.record_category_unique_name=category.category_unique_name")
    List<Record> queryAll();
}
