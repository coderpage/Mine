package com.coderpage.mine.app.tally.module.edit.record;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.coderpage.mine.app.tally.module.edit.model.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lc. 2018-09-16 22:01
 * @since 0.6.0
 */

class RecordCategoryPageAdapter extends PagerAdapter {

    private final int PAGE_ITEM_COUNT = 10;

    private List<Category> mCategoryList = new ArrayList<>();
    private List<RecordCategoryPage> mPageList = new ArrayList<>();
    private Activity mActivity;
    private RecordViewModel mViewModel;

    RecordCategoryPageAdapter(Activity activity, RecordViewModel viewModel) {
        mActivity = activity;
        mViewModel = viewModel;
    }

    void setCategoryList(List<Category> categoryList) {
        if (categoryList == null) {
            return;
        }
        mCategoryList.clear();
        mCategoryList.addAll(categoryList);

        mPageList.clear();

        int cursor = 0;
        while (cursor < mCategoryList.size()) {
            int next = cursor + PAGE_ITEM_COUNT > mCategoryList.size() ? mCategoryList.size() : cursor + PAGE_ITEM_COUNT;
            List<Category> pageList = mCategoryList.subList(cursor, next);
            if (!pageList.isEmpty()) {
                RecordCategoryPage page = new RecordCategoryPage(mActivity);
                page.setCategoryList(pageList, mViewModel);
                mPageList.add(page);
            }
            cursor = next;
        }

        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mPageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        RecordCategoryPage page = mPageList.get(position);
        container.addView(page);
        return page;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
