package com.coderpage.mine.app.tally.module.edit.category;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.OnLifecycleEvent;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import com.coderpage.base.utils.ResUtils;
import com.coderpage.framework.BaseViewModel;
import com.coderpage.framework.ViewReliedTask;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.data.CategoryIconHelper;
import com.coderpage.mine.app.tally.eventbus.EventCategoryAdd;
import com.coderpage.mine.app.tally.eventbus.EventCategoryUpdate;
import com.coderpage.mine.app.tally.persistence.model.CategoryModel;
import com.coderpage.mine.utils.AndroidUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * @author lc. 2019-04-20 18:48
 * @since 0.6.0
 *
 * 分类编辑页 VM
 */

public class CategoryEditViewModel extends BaseViewModel implements LifecycleObserver {

    /** 分类类型 {@link CategoryModel#TYPE_EXPENSE} {@link CategoryModel#TYPE_EXPENSE} */
    private ObservableInt mType = new ObservableInt();
    /** 分类 ID。添加分类时值为 0，修改分类时为该分类ID */
    private long mCategoryId = 0;
    private CategoryRepository mRepository;

    /** 分类名称 */
    private ObservableField<String> mCategoryName = new ObservableField<>();
    /** 分类图标 */
    private ObservableField<String> mCategoryIcon = new ObservableField<>();
    /** 所有分类 ICON */
    private MutableLiveData<List<String>> mCategoryIconList = new MutableLiveData<>();
    /** 导航栏标题 */
    private MutableLiveData<String> mToolbarTitle = new MutableLiveData<>();
    private MutableLiveData<ViewReliedTask<Activity>> mViewReliedTask = new MutableLiveData<>();

    public ObservableInt getType() {
        return mType;
    }

    public ObservableField<String> getCategoryName() {
        return mCategoryName;
    }

    public ObservableField<String> getCategoryIcon() {
        return mCategoryIcon;
    }

    LiveData<String> getToolbarTitle() {
        return mToolbarTitle;
    }

    LiveData<List<String>> getCategoryIconList() {
        return mCategoryIconList;
    }

    LiveData<ViewReliedTask<Activity>> getViewReliedTask() {
        return mViewReliedTask;
    }

    public CategoryEditViewModel(Application application) {
        super(application);
        mRepository = new CategoryRepository();
    }

    /** 图标选中点击 */
    public void onIconSelect(String icon) {
        mCategoryIcon.set(icon);
    }

    /** 确定保存点击 */
    public void onSubmitClick() {
        String icon = mCategoryIcon.get();
        String name = mCategoryName.get();

        boolean isUpdateCategory = mCategoryId > 0;
        if (isUpdateCategory) {
            updateCategory(mCategoryId, icon, name);
        } else {
            String uniqueName = AndroidUtils.generateUUID();
            addCategory(uniqueName, icon, name);
        }
    }


    /**
     * 更新分类
     *
     * @param categoryId 分类 ID
     * @param icon       分类 icon
     * @param name       分类名称
     */
    private void updateCategory(long categoryId, String icon, String name) {
        mRepository.updateCategory(categoryId, icon, name, categoryModel -> {
            EventBus.getDefault().post(new EventCategoryUpdate(categoryModel));
            mViewReliedTask.postValue(Activity::finish);
        });
    }

    /**
     * 添加分类
     *
     * @param uniqueName 分类标识
     * @param icon       分类 icon
     * @param name       分类名称
     */
    private void addCategory(String uniqueName, String icon, String name) {
        mRepository.addCategory(mType.get(), uniqueName, icon, name, result -> {
            if (result.isOk()) {
                CategoryModel categoryModel = new CategoryModel();
                categoryModel.setType(mType.get());
                categoryModel.setUniqueName(uniqueName);
                categoryModel.setIcon(icon);
                categoryModel.setName(name);
                EventBus.getDefault().post(new EventCategoryAdd(categoryModel));
                mViewReliedTask.postValue(Activity::finish);
            } else {
                showToastShort(result.error().msg());
            }
        });
    }

    private void init() {
        mCategoryIconList.setValue(CategoryIconHelper.ALL_ICON);

        // 修改分类
        if (mCategoryId > 0) {
            mRepository.queryCategoryById(mCategoryId, category -> {
                mType.set(category.getType());
                mCategoryName.set(category.getName());
                mCategoryIcon.set(category.getIcon());
            });
        }
        // 添加分类
        else {
            mCategoryName.set("");
            mCategoryIcon.set(CategoryIconHelper.ALL_ICON.get(0));
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate(LifecycleOwner owner) {
        Activity activity = (Activity) owner;
        mCategoryId = activity.getIntent().getLongExtra(CategoryEditActivity.EXTRA_CATEGORY_ID, 0);
        int categoryType = activity.getIntent().getIntExtra(CategoryEditActivity.EXTRA_CATEGORY_TYPE, CategoryModel.TYPE_EXPENSE);
        mType.set(categoryType);
        mToolbarTitle.setValue(ResUtils.getString(getApplication(),
                mCategoryId > 0 ? R.string.edit_category : R.string.add_category));
        init();
    }
}
