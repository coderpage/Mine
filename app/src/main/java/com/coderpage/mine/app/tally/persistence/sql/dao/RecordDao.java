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
 * @author lc. 2018-12-20
 * @since 0.6.0
 */
@Dao
public interface RecordDao {

    /***
     * 通过 ID 查询记录
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
     * 查询记录
     *
     * @param type      记录类型 {@link Record#TYPE_EXPENSE} {@link Record#TYPE_INCOME}
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param limit     最大数量
     * @param offset    分页偏移
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select * " +
            "from record " +
            "left outer join category on record.record_category_unique_name=category.category_unique_name " +
            "where record_type == :type and record_time >= :startTime and record_time <= :endTime order by record_time desc limit :limit offset :offset")
    List<Record> query(int type, long startTime, long endTime, long limit, long offset);

    /**
     * 查询记录
     *
     * @param type              记录类型 {@link Record#TYPE_EXPENSE} {@link Record#TYPE_INCOME}
     * @param startTime         开始时间
     * @param endTime           结束时间
     * @param limit             最大数量
     * @param offset            分页偏移
     * @param categoryNameArray 分类
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select * " +
            "from record " +
            "left outer join category on record.record_category_unique_name=category.category_unique_name " +
            "where record_type == :type and record_time >= :startTime and record_time <= :endTime and category_unique_name in (:categoryNameArray) " +
            "order by record_time desc limit :limit offset :offset")
    List<Record> query(int type, long startTime, long endTime, long limit, long offset, String[] categoryNameArray);

    /**
     * 查询所有类型记录
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param limit     最大数量
     * @param offset    分页偏移
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select * " +
            "from record " +
            "left outer join category on record.record_category_unique_name=category.category_unique_name " +
            "where record_time >= :startTime and record_time <= :endTime order by record_time desc limit :limit offset :offset")
    List<Record> queryAll(long startTime, long endTime, long limit, long offset);

    /**
     * 查询记录
     *
     * @param startTime         开始时间
     * @param endTime           结束时间
     * @param limit             最大数量
     * @param offset            分页偏移
     * @param categoryNameArray 分类
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select * " +
            "from record " +
            "left outer join category on record.record_category_unique_name=category.category_unique_name " +
            "where record_time >= :startTime and record_time <= :endTime and category_unique_name in (:categoryNameArray) " +
            "order by record_time desc limit :limit offset :offset")
    List<Record> queryAll(long startTime, long endTime, long limit, long offset, String[] categoryNameArray);

    /**
     * 插入记录
     *
     * @param record 记录
     * @return 记录 ID
     */
    @Insert
    long insert(RecordEntity record);

    /**
     * 批量插入记录
     *
     * @param records 记录
     * @return 记录 ID
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long[] insert(RecordEntity... records);

    /**
     * 更新记录
     *
     * @param record 记录
     * @return 更新数量
     */
    @Update
    int update(RecordEntity record);

    /**
     * 删除记录
     *
     * @param expense 记录
     */
    @Delete
    void delete(RecordEntity expense);

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
    List<Record> queryExpenseBetweenTimeTimeDesc(long start, long end);

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
            "from record " +
            "left outer join category on record.record_category_unique_name=category.category_unique_name " +
            "where record_time >= :start and record_time<= :end and record_type = 1 " +
            "order by record_time DESC")
    List<Record> queryIncomeBetweenTimeTimeDesc(long start, long end);

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

    /**
     * 查询第一笔记录
     *
     * @return 第一笔记录
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select * from record order by record_time ASC limit 1")
    Record queryFirst();

    /***
     * 查询所有记录
     *
     * @return 查询到的所有记录
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select * " +
            "from record " +
            "left outer join category on record.record_category_unique_name=category.category_unique_name")
    List<Record> queryAll();

}
