package com.coderpage.mine.app.tally.module.edit.income;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.module.edit.model.Category;
import com.coderpage.mine.tally.module.edit.income.IncomeCategoryItemBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lc. 2018-09-18 23:37
 * @since 0.6.0
 */

public class IncomeCategoryAdapter extends BaseAdapter {

    private IncomeViewModel mViewModel;
    private LayoutInflater mInflater;
    private List<Category> mCategories = new ArrayList<>();

    IncomeCategoryAdapter(Context context, IncomeViewModel viewModel) {
        mViewModel = viewModel;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mCategories.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return mCategories.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        IncomeCategoryItemBinding binding;
        if (convertView == null) {
            binding = DataBindingUtil.inflate(mInflater,
                    R.layout.tally_module_edit_item_income_category, parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (IncomeCategoryItemBinding) convertView.getTag();
        }
        Category item = mCategories.get(position);
        binding.setCategory(item);
        binding.setVm(mViewModel);
        binding.executePendingBindings();

        return convertView;
    }

    public void refreshData(List<Category> items) {
        mCategories.clear();
        mCategories.addAll(items);
        notifyDataSetChanged();
    }
}
