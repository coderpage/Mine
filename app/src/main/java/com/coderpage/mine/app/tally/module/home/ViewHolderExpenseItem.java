package com.coderpage.mine.app.tally.module.home;

import android.app.Activity;

import com.coderpage.mine.app.tally.module.home.model.HomeDisplayData;
import com.coderpage.mine.app.tally.persistence.model.Record;
import com.coderpage.mine.tally.module.home.ExpenseItemBinding;

/**
 * @author lc. 2018-08-14 20:57
 * @since 0.6.0
 */
class ViewHolderExpenseItem extends BaseViewHolder {

    private Activity mActivity;
    private HomeViewModel mViewModel;
    private ExpenseItemBinding mBinding;

    ViewHolderExpenseItem(Activity activity, HomeViewModel viewModel, ExpenseItemBinding binding) {
        super(binding.getRoot());
        mActivity = activity;
        mViewModel = viewModel;
        mBinding = binding;
    }

    @Override
    void bindData(HomeDisplayData data) {
        if (data != null && data.getInternal() != null && data.getInternal() instanceof Record) {
            Record expense = (Record) data.getInternal();
            mBinding.setActivity(mActivity);
            mBinding.setVm(mViewModel);
            mBinding.setData(expense);
            mBinding.executePendingBindings();
        }
    }
}