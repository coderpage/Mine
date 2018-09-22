package com.coderpage.mine.app.tally.module.home;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.coderpage.mine.app.tally.module.home.model.HomeDisplayData;

/**
 * @author lc. 2018-07-21 10:48
 * @since 0.6.0
 */

abstract class BaseViewHolder extends RecyclerView.ViewHolder {

    BaseViewHolder(View view) {
        super(view);
    }

    /**
     * 绑定数据
     *
     * @param data 数据
     */
    abstract void bindData(HomeDisplayData data);
}
