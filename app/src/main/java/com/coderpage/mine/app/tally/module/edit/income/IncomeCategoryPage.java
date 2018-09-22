package com.coderpage.mine.app.tally.module.edit.income;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.module.edit.model.Category;

import java.util.List;

/**
 * @author lc. 2018-09-18 23:49
 * @since 0.6.0
 */

public class IncomeCategoryPage extends FrameLayout {

    private GridView mGridView;

    private IncomeCategoryAdapter mAdapter;

    public IncomeCategoryPage(@NonNull Context context) {
        this(context, null);
    }

    public IncomeCategoryPage(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IncomeCategoryPage(@NonNull Context context, @Nullable AttributeSet attrs,
                               @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.tally_module_edit_widget_category_page, this);
        mGridView = findViewById(R.id.gvCategoryIcon);
    }

    void setCategoryList(List<Category> categoryList, IncomeViewModel viewModel) {
        if (mAdapter == null) {
            mAdapter = new IncomeCategoryAdapter(getContext(), viewModel);
            mGridView.setAdapter(mAdapter);
        }
        mAdapter.refreshData(categoryList);
    }
}
