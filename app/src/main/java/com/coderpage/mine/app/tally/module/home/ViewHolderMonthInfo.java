package com.coderpage.mine.app.tally.module.home;

import android.app.Activity;
import android.content.res.Resources;

import com.coderpage.base.utils.UIUtils;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.module.home.model.HomeDisplayData;
import com.coderpage.mine.app.tally.module.home.model.HomeMonthModel;
import com.coderpage.mine.tally.module.home.MonthInfoItemBinding;

/**
 * @author lc. 2018-07-21 10:59
 * @since 0.6.0
 */

class ViewHolderMonthInfo extends BaseViewHolder {

    private Activity mActivity;
    private HomeMonthInfoViewModel mViewModel;
    private MonthInfoItemBinding mBinding;

    ViewHolderMonthInfo(Activity activity, HomeMonthInfoViewModel viewModel, MonthInfoItemBinding binding) {
        super(binding.getRoot());
        mActivity = activity;
        mViewModel = viewModel;
        mBinding = binding;
    }

    @Override
    void bindData(HomeDisplayData data) {
        if (data != null && data.getInternal() != null && data.getInternal() instanceof HomeMonthModel) {

            Resources resources = mActivity.getResources();
            HomeMonthModel monthModel = (HomeMonthModel) data.getInternal();
            mBinding.setActivity(mActivity);
            mBinding.setVm(mViewModel);
            mBinding.setData(monthModel);
            mViewModel.setData(monthModel);

            mBinding.topCategoryView.setData(monthModel.getMonthCategoryExpenseData());
            mBinding.topCategoryView.setDrawTopCount(3);
            mBinding.topCategoryView.setLabelTextColor(resources.getColor(R.color.appTextColorLabel));
            mBinding.topCategoryView.setLabelTextSize(UIUtils.dp2px(mActivity, 9));
            mBinding.topCategoryView.setColorArray(
                    resources.getColor(R.color.categoryColor1),
                    resources.getColor(R.color.categoryColor2),
                    resources.getColor(R.color.categoryColor3),
                    resources.getColor(R.color.categoryColor4));

            mBinding.executePendingBindings();
        }
    }
}
