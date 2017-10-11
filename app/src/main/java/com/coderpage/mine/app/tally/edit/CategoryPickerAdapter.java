package com.coderpage.mine.app.tally.edit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.data.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * @author abner-l. 2017-04-16
 */

public class CategoryPickerAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<Category> mCategories = new ArrayList<>();

    CategoryPickerAdapter(Context context) {
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
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.tally_grid_item_category, null);
        }
        Category item = mCategories.get(position);

        ImageView icon = ((ImageView) convertView.findViewById(R.id.ivIcon));
        TextView name = ((TextView) convertView.findViewById(R.id.tvName));

        icon.setImageResource(item.getIcon());

        name.setText(item.getName());

        return convertView;
    }

    public void refreshData(List<Category> items) {
        mCategories.clear();
        mCategories.addAll(items);
        notifyDataSetChanged();
    }

    public List<Category> getCategoryItems() {
        return mCategories;
    }
}
