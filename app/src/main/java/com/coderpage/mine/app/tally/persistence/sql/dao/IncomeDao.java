package com.coderpage.mine.app.tally.persistence.sql.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RoomWarnings;
import android.arch.persistence.room.Update;

import com.coderpage.mine.app.tally.persistence.model.Record;
import com.coderpage.mine.app.tally.persistence.model.RecordCategoryGroup;
import com.coderpage.mine.app.tally.persistence.model.RecordGroup;
import com.coderpage.mine.app.tally.persistence.sql.entity.RecordEntity;

import java.util.List;

/**
 * @author lc. 2018-05-27 14:43
 * @since 0.6.0
 */
@Dao
public interface IncomeDao {

    /***
     * 通过 ID 查询收入记录
     *
     * @param id 收入记录 ID
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
     * @param income 记录
     * @return 记录 ID
     */
    @Insert
    long insert(RecordEntity income);

    /**
     * 更新记录
     *
     * @param income 记录
     * @return 更新数量
     */
    @Update
    int update(RecordEntity income);

    /**
     * 删除记录
     *
     * @param income 记录
     */
    @Delete
    void delete(RecordEntity income);

    /***
     * 查询指定时间区间的收入记录
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
            "where record_time >= :start and record_time <= :end and record_type = 1")
    List<Record> queryBetweenTime(long start, long end);


    /***
     * 查询指定时间区间收入记录，按日期降序排序
     *
     * @param start 开始时间
     * @param end 结束时间
     *
     * @return 查询到的所有记录
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select * " +
            "from record left outer join category on record.record_category_unique_name=category.category_unique_name " +
            "where record_time >= :start and record_time<= :end and record_type = 1 " +
            "order by record_time DESC")
    List<Record> queryBetweenTimeTimeDesc(long start, long end);

    /**
     * 查询第一笔收入
     *
     * @return 第一笔收入
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select * from record where record_type = 1 order by record_time ASC limit 1")
    Record queryFirst();

    /**
     * 查询指定时间区间内的月收入数据
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 查询到的月收入数据
     */
    @Query("select count(*),sum(record_amount),record_time " +
            "from record " +
            "where record_time >= :start and record_time<= :end and record_type = 1 " +
            "group by strftime('%Y-%m', datetime(record_time/1000, 'unixepoch', 'localtime')) " +
            "order by record_time ASC")
    List<RecordGroup> queryIncomeMonthGroup(long start, long end);

    /**
     * 查询指定时间区间内的日收入数据
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 查询到的日收入数据
     */
    @Query("select count(*),sum(record_amount),record_time " +
            "from record " +
            "where record_time >= :start and record_time<= :end and record_type = 1 " +
            "group by strftime('%Y-%m-%d', datetime(record_time/1000, 'unixepoch', 'localtime')) " +
            "order by record_time ASC")
    List<RecordGroup> queryIncomeDailyGroup(long start, long end);

    /**
     * 查询指定时间区间内的分类收入数据
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 查询到的分类收入数据
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select category.category_id,count(*),sum(record_amount),record_time,category_name,category_icon " +
            "from record " +
            "left outer join category on record.record_category_unique_name=category.category_unique_name " +
            "where record_time >= :start and record_time<= :end and record_type = 1 " +
            "group by category.category_id " +
            "order by sum(record_amount) ASC")
    List<RecordCategoryGroup> queryIncomeCategoryGroup(long start, long end);
}
