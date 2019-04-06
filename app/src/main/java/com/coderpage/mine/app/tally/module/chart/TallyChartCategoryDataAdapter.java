package com.coderpage.mine.app.tally.module.chart;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.module.chart.data.CategoryData;
import com.coderpage.mine.module.chart.ItemCategoryDataBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lc. 2019-04-03 20:09
 * @since 0.6.0
 */

class TallyChartCategoryDataAdapter extends RecyclerView.Adapter<TallyChartCategoryDataAdapter.Vh> {

    private List<CategoryData> mDataList = new ArrayList<>();
    private TallyChartViewModel mViewModel;

    TallyChartCategoryDataAdapter(TallyChartViewModel viewModel) {
        mViewModel = viewModel;
    }

    void setDataList(List<CategoryData> list) {
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
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Vh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.tally_module_chart_item_category_data, parent, false));
    }

    @Override
    public void onBindViewHolder(Vh holder, int position) {
        holder.bind(position, mDataList.get(position));
    }

    class Vh extends RecyclerView.ViewHolder {

        private ItemCategoryDataBinding mBinding;

        Vh(ItemCategoryDataBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        void bind(int position, CategoryData categoryData) {
            mBinding.setData(categoryData);
            mBinding.setVm(mViewModel);
            mBinding.setShowDivider(position != mDataList.size() - 1);
            mBinding.executePendingBindings();
        }
    }
}
