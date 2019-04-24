package com.coderpage.mine.app.tally.module.edit.category;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.OnLifecycleEvent;

import com.coderpage.base.utils.ArrayUtils;
import com.coderpage.framework.BaseViewModel;
import com.coderpage.framework.ViewReliedTask;
import com.coderpage.mine.app.tally.eventbus.EventCategoryOrderChange;
import com.coderpage.mine.app.tally.persistence.model.CategoryModel;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.List;

/**
 * @author lc. 2019-04-24 23:35
 * @since 0.6.0
 */


public class CategorySortViewModel extends BaseViewModel implements LifecycleObserver {

    private int mType = -1;
    private CategoryRepository mRepository;

    private MutableLiveData<List<CategoryModel>> mCategoryList = new MutableLiveData<>();
    private MutableLiveData<ViewReliedTask<Activity>> mViewReliedTask = new MutableLiveData<>();

    public CategorySortViewModel(Application application) {
        super(application);
        mRepository = new CategoryRepository();
    }

    LiveData<List<CategoryModel>> getCategoryList() {
        return mCategoryList;
    }

    LiveData<ViewReliedTask<Activity>> getViewReliedTask() {
        return mViewReliedTask;
    }

    /** 保存点击 */
    void onSaveClick() {
        List<CategoryModel> categoryList = mCategoryList.getValue();
        ArrayUtils.forEach(categoryList, (count, index, item) -> {
            item.setOrder(index);
        });
        mRepository.updateCategoryOrder(categoryList, v -> {
            // 分发分类排序修改事件
            EventBus.getDefault().post(new EventCategoryOrderChange(mType));
            // 关闭页面
            mViewReliedTask.postValue(Activity::onBackPressed);
        });
    }

    /** 交换 ITEM */
    void onItemSwap(int fromPosition, int toPosition) {
        List<CategoryModel> categoryList = mCategoryList.getValue();
        if (categoryList == null || categoryList.isEmpty()) {
            return;
        }
        Collections.swap(categoryList, fromPosition, toPosition);
        mCategoryList.setValue(categoryList);
    }

    /** 拖拽排序、完成排序点击 */
    public void onDrag2OrderClick() {
        List<CategoryModel> categoryList = mCategoryList.getValue();
        ArrayUtils.forEach(categoryList, (count, index, item) -> {
            item.setOrder(index);
        });
        mRepository.updateCategoryOrder(categoryList, v -> {
            // 分发分类排序修改事件
            EventBus.getDefault().post(new EventCategoryOrderChange(mType));
        });
    }

    private void refreshData() {
        if (mType == CategoryModel.TYPE_EXPENSE) {
            mRepository.loadAllExpenseCategory(mCategoryList::postValue);
        }
        if (mType == CategoryModel.TYPE_INCOME) {
            mRepository.loadAllIncomeCategory(mCategoryList::postValue);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate(LifecycleOwner owner) {
        mType = ((Activity) owner).getIntent().getIntExtra(
                CategorySortActivity.EXTRA_CATEGORY_TYPE, CategoryModel.TYPE_EXPENSE);
        refreshData();
    }
}
