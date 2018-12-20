package com.coderpage.mine.app.tally.persistence.sql.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RoomWarnings;

import com.coderpage.mine.app.tally.persistence.model.CategoryModel;
import com.coderpage.mine.app.tally.persistence.sql.entity.CategoryEntity;

import java.util.List;

/**
 * @author lc. 2018-05-20 15:53
 * @since 0.6.0
 */

@Dao
public interface CategoryDao {

    @Query("select count(*) from category")
    int categorySize();

    /**
     * 查询所有消费分类
     *
     * @return 所有消费分类
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select * from category where category_type = 0 order by category_order DESC")
    List<CategoryModel> allExpenseCategory();

    /**
     * 查询所有消费分类
     *
     * @return 所有消费分类
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select * from category where category_type = 1 order by category_order DESC")
    List<CategoryModel> allIncomeCategory();

    /**
     * 查询所有分类
     *
     * @return 所有分类
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select * from category order by category_order DESC")
    List<CategoryModel> allCategory();

    /**
     * 插入分类
     *
     * @param entity 分类
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(CategoryEntity... entity);
}
