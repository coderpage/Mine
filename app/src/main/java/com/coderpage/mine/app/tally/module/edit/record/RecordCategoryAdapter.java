package com.coderpage.mine.app.tally.module.edit.record;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.module.edit.model.Category;
import com.coderpage.mine.tally.module.edit.record.RecordCategoryItemBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * @author abner-l. 2017-04-16
 */

public class RecordCategoryAdapter extends BaseAdapter {

    private RecordViewModel mViewModel;
    private LayoutInflater mInflater;
    private List<Category> mCategories = new ArrayList<>();

    RecordCategoryAdapter(Context context, RecordViewModel viewModel) {
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
        RecordCategoryItemBinding binding;
        if (convertView == null) {
            binding = DataBindingUtil.inflate(mInflater,
                    R.layout.tally_module_edit_item_record_category, parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (RecordCategoryItemBinding) convertView.getTag();
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
