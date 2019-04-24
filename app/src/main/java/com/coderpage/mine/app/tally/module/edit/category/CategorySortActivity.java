package com.coderpage.mine.app.tally.module.edit.category;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.coderpage.base.utils.UIUtils;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.persistence.model.CategoryModel;
import com.coderpage.mine.module.edit.category.CategorySortActivityBinding;
import com.coderpage.mine.module.edit.category.ItemCategorySortBinding;
import com.coderpage.mine.ui.BaseActivity;
import com.coderpage.mine.ui.widget.recyclerview.ItemMarginDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lc. 2019-04-24 23:30
 * @since 0.6.0
 *
 * 分类排序页
 */

public class CategorySortActivity extends BaseActivity {

    static final String EXTRA_CATEGORY_TYPE = "extra_category_type";

    private CategorySortActivityBinding mBinding;
    private CategorySortViewModel mViewModel;
    private CategoryListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.tally_module_edit_category_activity_sort);
        mViewModel = ViewModelProviders.of(this).get(CategorySortViewModel.class);
        getLifecycle().addObserver(mViewModel);
        initView();
        subscribeUi();
    }

    /**
     * 打开分类管理页面
     *
     * @param context      context
     * @param categoryType 分类类型
     *                     {@link CategoryModel#TYPE_EXPENSE}
     *                     {@link CategoryModel#TYPE_INCOME}
     * @param sharedRoot   转场动画渲染的 View
     */
    public static void open(Context context, int categoryType, View sharedRoot) {
        Intent intent = new Intent(context, CategorySortActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(EXTRA_CATEGORY_TYPE, categoryType);

        // api >= 21 使用过渡转场动画 其他普通方式打开
        if (!(context instanceof Activity) || sharedRoot == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            context.startActivity(intent);
            return;
        }

        Activity activity = (Activity) context;
        activity.startActivity(intent,
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity, sharedRoot, "sharedRoot").toBundle());
    }

    private void initView() {
        setToolbarAsClose(v -> onBackPressed());

        RecyclerView recyclerView = mBinding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(self(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new CategoryListAdapter();
        recyclerView.setAdapter(mAdapter);
        int padding4 = UIUtils.dp2px(self(), 4);
        int padding16 = UIUtils.dp2px(self(), 16);
        ItemMarginDecoration itemMarginDecoration = new ItemMarginDecoration(padding16, padding4, padding16, padding4);
        recyclerView.addItemDecoration(itemMarginDecoration);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void subscribeUi() {
        mViewModel.getCategoryList().observe(this, categoryList -> {
            mAdapter.setDataList(categoryList);
        });
        mViewModel.getViewReliedTask().observe(this, task -> {
            if (task != null) {
                task.execute(self());
            }
        });
        mViewModel.getViewReliedTask().observe(this, task -> {
            if (task != null) {
                task.execute(self());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_tally_category_sort, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_complete:
                mViewModel.onSaveClick();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /** 分类列表适配器 */
    private class CategoryListAdapter extends RecyclerView.Adapter<CategoryViewHolder> {

        private List<CategoryModel> mDataList = new ArrayList<>();

        void setDataList(List<CategoryModel> list) {
            if (list == null) {
                return;
            }
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mDataList.size();
                }

                @Override
                public int getNewListSize() {
                    return list.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    CategoryModel oldItem = mDataList.get(oldItemPosition);
                    CategoryModel newItem = list.get(newItemPosition);
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    CategoryModel oldItem = mDataList.get(oldItemPosition);
                    CategoryModel newItem = list.get(newItemPosition);
                    return oldItem.getId() == newItem.getId();
                }
            });
            mDataList.clear();
            mDataList.addAll(list);
            result.dispatchUpdatesTo(this);
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }

        @Override
        public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CategoryViewHolder(DataBindingUtil.inflate(LayoutInflater.from(self()),
                    R.layout.tally_module_edit_category_item_sort, parent, false));
        }

        @Override
        public void onBindViewHolder(CategoryViewHolder holder, int position) {
            holder.bind(mDataList.get(position));
        }
    }

    /** 分类 ITEM ViewHolder */
    private class CategoryViewHolder extends RecyclerView.ViewHolder {

        private ItemCategorySortBinding mBinding;

        CategoryViewHolder(ItemCategorySortBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        void bind(CategoryModel category) {
            mBinding.setData(category);
            mBinding.setVm(mViewModel);
            mBinding.executePendingBindings();
        }
    }

    ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            final int swipeFlags = 0;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView,
                              RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            mViewModel.onItemSwap(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }
    });
}
