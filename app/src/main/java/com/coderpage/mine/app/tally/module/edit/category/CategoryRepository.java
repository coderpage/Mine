package com.coderpage.mine.app.tally.module.edit.category;

import com.coderpage.base.common.IError;
import com.coderpage.base.common.NonThrowError;
import com.coderpage.base.common.Result;
import com.coderpage.base.common.SimpleCallback;
import com.coderpage.base.utils.ArrayUtils;
import com.coderpage.base.utils.CommonUtils;
import com.coderpage.base.utils.ResUtils;
import com.coderpage.concurrency.MineExecutors;
import com.coderpage.mine.MineApp;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.common.error.ErrorCode;
import com.coderpage.mine.app.tally.persistence.model.CategoryModel;
import com.coderpage.mine.app.tally.persistence.sql.TallyDatabase;
import com.coderpage.mine.app.tally.persistence.sql.dao.CategoryDao;
import com.coderpage.mine.app.tally.persistence.sql.entity.CategoryEntity;

import java.util.List;

/**
 * @author lc. 2019-04-20 16:06
 * @since 0.6.0
 */


class CategoryRepository {

    /**
     * 查询分类
     *
     * @param categoryId 分类 ID
     * @param callback   回调
     */
    void queryCategoryById(long categoryId, SimpleCallback<CategoryModel> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            CategoryModel categoryModel = TallyDatabase.getInstance().categoryDao().queryById(categoryId);
            callback.success(categoryModel);
        });
    }

    /**
     * 读取所有支出分类
     *
     * @param callback 回调
     */
    void loadAllExpenseCategory(SimpleCallback<List<CategoryModel>> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            List<CategoryModel> categoryList = TallyDatabase.getInstance().categoryDao().allExpenseCategory();
            callback.success(categoryList);
        });
    }

    /**
     * 读取所有支出分类
     *
     * @param callback 回调
     */
    void loadAllIncomeCategory(SimpleCallback<List<CategoryModel>> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            List<CategoryModel> categoryList = TallyDatabase.getInstance().categoryDao().allIncomeCategory();
            callback.success(categoryList);
        });
    }

    /**
     * 修改分类名称
     *
     * @param categoryId 分类 ID
     * @param icon       分类图标
     * @param name       分类名称
     * @param callback   回调
     */
    void updateCategory(long categoryId, String icon, String name, SimpleCallback<CategoryModel> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            TallyDatabase.getInstance().categoryDao().update(categoryId, icon, name);
            CategoryModel categoryModel = TallyDatabase.getInstance().categoryDao().queryById(categoryId);
            callback.success(categoryModel);
        });
    }

    /**
     * 添加分类
     *
     * @param type       分类类型 {@link CategoryModel#TYPE_EXPENSE} {@link CategoryModel#TYPE_INCOME}
     * @param uniqueName 分类标识
     * @param iconName   分类图标
     * @param name       分类名称
     * @param callback   回调
     */
    void addCategory(int type,
                     String uniqueName,
                     String iconName,
                     String name,
                     SimpleCallback<Result<Void, IError>> callback) {
        MineExecutors.ioExecutor().execute(() -> {
            CategoryDao categoryDao = TallyDatabase.getInstance().categoryDao();

            List<CategoryModel> categoryList = categoryDao.allCategory();
            boolean alreadyContains = ArrayUtils.contains(categoryList, item -> {
                return CommonUtils.isEqual(item.getUniqueName(), uniqueName);
            });
            // 已经存在，不重复添加
            if (alreadyContains) {
                callback.success(new Result<>(null, new NonThrowError(ErrorCode.ILLEGAL_ARGS,
                        ResUtils.getString(MineApp.getAppContext(), R.string.err_category_already_exist))));
                return;
            }

            CategoryEntity entity = new CategoryEntity();
            entity.setType(type);
            entity.setUniqueName(uniqueName);
            entity.setIcon(iconName);
            entity.setName(name);
            TallyDatabase.getInstance().categoryDao().insert(entity);

            callback.success(new Result<>(null, null));
        });
    }
}
