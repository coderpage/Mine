package com.coderpage.mine.app.tally.module.home;

import android.app.Activity;

import com.coderpage.mine.app.tally.module.home.model.HomeDisplayData;
import com.coderpage.mine.app.tally.module.home.model.HomeTodayExpenseModel;
import com.coderpage.mine.tally.module.home.TodayExpenseItemBinding;

/**
 * @author lc. 2018-08-14 20:52
 * @since 0.6.0
 */
class ViewHolderTodayExpense extends BaseViewHolder {

    private Activity mActivity;
    private HomeViewModel mViewModel;
    private TodayExpenseItemBinding mBinding;

    ViewHolderTodayExpense(Activity activity, HomeViewModel viewModel, TodayExpenseItemBinding binding) {
        super(binding.getRoot());
        mActivity = activity;
        mViewModel = viewModel;
        mBinding = binding;
    }

    @Override
    void bindData(HomeDisplayData data) {
        if (data != null && data.getInternal() != null && data.getInternal() instanceof HomeTodayExpenseModel) {
            HomeTodayExpenseModel todayModel = (HomeTodayExpenseModel) data.getInternal();
            mBinding.setActivity(mActivity);
            mBinding.setVm(mViewModel);
            mBinding.setData(todayModel);
            mBinding.executePendingBindings();
        }
    }
}
