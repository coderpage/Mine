package com.coderpage.mine.app.tally.module.search;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.coderpage.base.utils.CommonUtils;
import com.coderpage.mine.R;
import com.coderpage.mine.module.search.ItemClearAllBinding;
import com.coderpage.mine.module.search.ItemSearchHistoryBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lc. 2017-09-22 23:41
 * @since 0.5.0
 *
 * 搜索历史记录适配器
 */

class SearchHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_TYPE_NORMAL = 1;
    private static final int ITEM_TYPE_BOTTOM = 2;
    private static final String ITEM_CLEAR_ALL = "ITEM_CLEAR_ALL";

    private LayoutInflater mInflater;
    private SearchViewModel mViewModel;
    private List<String> mItems = new ArrayList<>();

    SearchHistoryAdapter(Context context, SearchViewModel viewModel) {
        mInflater = LayoutInflater.from(context);
        mViewModel = viewModel;
    }

    void refresh(List<String> items) {
        mItems.clear();
        mItems.addAll(items);
        if (!mItems.isEmpty()) {
            mItems.add(ITEM_CLEAR_ALL);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return CommonUtils.isEqual(mItems.get(position), ITEM_CLEAR_ALL) ? ITEM_TYPE_BOTTOM : ITEM_TYPE_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_NORMAL) {
            return new ItemHistoryVh(DataBindingUtil.inflate(mInflater,
                    R.layout.tally_module_search_item_history, parent, false));
        } else {
            return new ItemClearAllVh(DataBindingUtil.inflate(mInflater,
                    R.layout.tally_module_search_item_clear_all, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHistoryVh) {
            ((ItemHistoryVh) holder).bind(mItems.get(position));
        } else if (holder instanceof ItemClearAllVh) {
            ((ItemClearAllVh) holder).bind();
        }
    }

    class ItemHistoryVh extends RecyclerView.ViewHolder {
        private ItemSearchHistoryBinding mBinding;

        ItemHistoryVh(ItemSearchHistoryBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        void bind(String history) {
            mBinding.setText(history);
            mBinding.setVm(mViewModel);
            mBinding.executePendingBindings();
        }
    }

    class ItemClearAllVh extends RecyclerView.ViewHolder {
        private ItemClearAllBinding mBinding;

        ItemClearAllVh(ItemClearAllBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        void bind() {
            mBinding.setVm(mViewModel);
            mBinding.executePendingBindings();
        }
    }
}
