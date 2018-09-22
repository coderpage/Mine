package com.coderpage.mine.app.tally.persistence.sql.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RoomWarnings;
import android.arch.persistence.room.Update;

import com.coderpage.mine.app.tally.persistence.model.Income;
import com.coderpage.mine.app.tally.persistence.sql.entity.InComeEntity;

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
    @Query("select * from income left outer join category on income.income_category_id=category.category_id " +
            "where income_id = :id")
    Income queryById(long id);

    /**
     * 插入记录
     *
     * @param income 记录
     * @return 记录 ID
     */
    @Insert
    long insert(InComeEntity income);

    /**
     * 更新记录
     *
     * @param income 记录
     * @return 更新数量
     */
    @Update
    int update(InComeEntity income);

    /**
     * 删除记录
     *
     * @param income 记录
     */
    @Delete
    void delete(InComeEntity income);

    /***
     * 查询指定时间区间的收入记录
     *
     * @param start 开始时间
     * @param end 结束时间
     *
     * @return 查询到的所有记录
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select * from income left outer join category on income.income_category_unique_name=category.category_unique_name where income_time >= :start and income_time <= :end")
    List<Income> queryBetweenTime(long start, long end);


    /***
     * 查询指定时间区间收入记录，按日期降序排序
     *
     * @param start 开始时间
     * @param end 结束时间
     *
     * @return 查询到的所有记录
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select * from income left outer join category on income.income_category_unique_name=category.category_unique_name " +
            "where income_time >= :start and income_time<= :end order by income_time DESC")
    List<Income> queryBetweenTimeTimeDesc(long start, long end);
}
