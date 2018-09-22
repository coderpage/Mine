package com.coderpage.mine.app.tally.persistence.sql.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RoomWarnings;
import android.arch.persistence.room.Update;

import com.coderpage.mine.app.tally.persistence.model.Expense;
import com.coderpage.mine.app.tally.persistence.sql.entity.ExpenseEntity;

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
    @Query("select * from expense left outer join category on expense.expense_category_id=category.category_id " +
            "where expense_id = :id")
    Expense queryById(long id);

    /**
     * 插入记录
     *
     * @param expense 记录
     * @return 记录 ID
     */
    @Insert
    long insert(ExpenseEntity expense);

    /**
     * 更新记录
     *
     * @param expense 记录
     * @return 更新数量
     */
    @Update
    int update(ExpenseEntity expense);

    /**
     * 删除记录
     *
     * @param expense 记录
     */
    @Delete
    void delete(ExpenseEntity expense);

    /***
     * 查询指定时间区间支出记录
     *
     * @param start 开始时间
     * @param end 结束时间
     *
     * @return 查询到的所有记录
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select * from expense left outer join category on expense.expense_category_unique_name=category.category_unique_name " +
            "where expense_time >= :start and expense_time<= :end")
    List<Expense> queryBetweenTime(long start, long end);

    /***
     * 查询指定时间区间支出记录，按日期降序排序
     *
     * @param start 开始时间
     * @param end 结束时间
     *
     * @return 查询到的所有记录
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select * from expense left outer join category on expense.expense_category_unique_name=category.category_unique_name " +
            "where expense_time >= :start and expense_time<= :end order by expense_time DESC")
    List<Expense> queryBetweenTimeTimeDesc(long start, long end);
}
