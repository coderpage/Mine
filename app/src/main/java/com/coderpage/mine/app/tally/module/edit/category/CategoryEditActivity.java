package com.coderpage.mine.app.tally.module.edit.category;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.coderpage.base.utils.UIUtils;
import com.coderpage.mine.R;
import com.coderpage.mine.module.edit.category.CategoryEditActivityBinding;
import com.coderpage.mine.module.edit.category.ItemCategoryIconBinding;
import com.coderpage.mine.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lc. 2019-04-20 18:48
 * @since 0.6.0
 *
 * 分类编辑页
 */

public class CategoryEditActivity extends BaseActivity {

    static final String EXTRA_CATEGORY_TYPE = "extra_category_type";
    static final String EXTRA_CATEGORY_ID = "extra_category_id";

    private CategoryEditActivityBinding mBinding;
    private CategoryEditViewModel mViewModel;
    private IconListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.tally_module_edit_category_activity_edit);
        mViewModel = ViewModelProviders.of(this).get(CategoryEditViewModel.class);
        getLifecycle().addObserver(mViewModel);

        initView();
        subscribeUi();
    }

    /**
     * 编辑分类
     *
     * @param context    {@link Context}
     *                   {@link com.coderpage.mine.app.tally.persistence.model.CategoryModel#TYPE_EXPENSE}
     *                   {@link com.coderpage.mine.app.tally.persistence.model.CategoryModel#TYPE_INCOME}
     * @param categoryId 分类 ID
     */
    public static void openAsEdit(Context context, long categoryId) {
        Intent intent = new Intent(context, CategoryEditActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(EXTRA_CATEGORY_ID, categoryId);
        context.startActivity(intent);
    }

    /**
     * 添加分类
     *
     * @param context {@link Context}
     * @param type    分类类型
     *                {@link com.coderpage.mine.app.tally.persistence.model.CategoryModel#TYPE_EXPENSE}
     *                {@link com.coderpage.mine.app.tally.persistence.model.CategoryModel#TYPE_INCOME}
     */
    public static void openAsAddNew(Context context, int type) {
        Intent intent = new Intent(context, CategoryEditActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(EXTRA_CATEGORY_TYPE, type);
        intent.putExtra(EXTRA_CATEGORY_ID, 0);
        context.startActivity(intent);
    }

    private void initView() {
        setToolbarAsBack(v -> {
            UIUtils.hideSoftKeyboard(self());
            onBackPressed();
        });

        mAdapter = new IconListAdapter();
        RecyclerView recyclerView = mBinding.recyclerView;
        recyclerView.setLayoutManager(new GridLayoutManager(self(), 5));
        recyclerView.setAdapter(mAdapter);
    }

    private void subscribeUi() {
        mBinding.setVm(mViewModel);
        mViewModel.getToolbarTitle().observe(self(), toolbarTitle -> {
            if (!TextUtils.isEmpty(toolbarTitle)) {
                setTitle(toolbarTitle);
            }
        });
        mViewModel.getCategoryIconList().observe(this, iconList -> {
            mAdapter.setDataList(iconList);
        });
        mViewModel.getViewReliedTask().observe(this, task -> {
            if (task != null) {
                task.execute(self());
            }
        });
    }

    /** 图标适配器 */
    private class IconListAdapter extends RecyclerView.Adapter<IconViewHolder> {

        private List<String> mDataList = new ArrayList<>();

        void setDataList(List<String> list) {
            if (list == null) {
                return;
            }
            mDataList.clear();
            mDataList.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }

        @Override
        public IconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new IconViewHolder(DataBindingUtil.inflate(LayoutInflater.from(self()),
                    R.layout.tally_module_edit_category_item_category_icon, parent, false));
        }

        @Override
        public void onBindViewHolder(IconViewHolder holder, int position) {
            holder.bind(mDataList.get(position));
        }
    }

    private class IconViewHolder extends RecyclerView.ViewHolder {

        private ItemCategoryIconBinding mBinding;

        IconViewHolder(ItemCategoryIconBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        void bind(String icon) {
            mBinding.setCategoryIcon(icon);
            mBinding.setVm(mViewModel);
            mBinding.executePendingBindings();
        }
    }
}
