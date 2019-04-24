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
     * 查询分类
     *
     * @return 分类
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select * from category where category_id = :id")
    CategoryModel queryById(long id);

    /**
     * 查询所有消费分类
     *
     * @return 所有消费分类
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select * from category where category_type = 0 order by category_order ASC")
    List<CategoryModel> allExpenseCategory();

    /**
     * 查询所有消费分类
     *
     * @return 所有消费分类
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select * from category where category_type = 1 order by category_order ASC")
    List<CategoryModel> allIncomeCategory();

    /**
     * 查询所有分类
     *
     * @return 所有分类
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select * from category order by category_order ASC")
    List<CategoryModel> allCategory();

    /**
     * 插入分类
     *
     * @param entity 分类
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(CategoryEntity... entity);

    /**
     * 更新分类
     *
     * @param categoryId 分类 ID
     * @param icon       分类图标
     * @param name       分类名称
     */
    @Query("update category set category_icon=:icon, category_name=:name where category_id = :categoryId")
    void update(long categoryId, String icon, String name);

    /**
     * 更新分类的排序
     *
     * @param categoryId 分类 ID
     * @param order      排序
     */
    @Query("update category set category_order=:order where category_id = :categoryId")
    void updateOrder(long categoryId, int order);
}
