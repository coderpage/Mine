package com.coderpage.mine.app.tally.module.edit.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.coderpage.mine.BR;
import com.coderpage.mine.app.tally.persistence.model.CategoryModel;

/**
 * @author lc. 2018-09-03 22:43
 * @since 0.6.0
 */

public class Category extends BaseObservable {

    private boolean isSelect;

    private CategoryModel internal;

    public Category(CategoryModel category) {
        this.internal = category;
    }

    @Bindable
    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
        notifyPropertyChanged(BR.select);
    }

    public CategoryModel getInternal() {
        return internal;
    }

    public void setInternal(CategoryModel category) {
        this.internal = category;
    }
}
