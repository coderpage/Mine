package com.coderpage.mine.app.tally.module.home;

import android.app.Activity;

import com.coderpage.mine.app.tally.module.home.model.HomeDisplayData;
import com.coderpage.mine.app.tally.persistence.model.Income;
import com.coderpage.mine.tally.module.home.IncomeItemBinding;

/**
 * @author lc. 2018-09-22 17:23
 * @since 0.6.0
 */

public class ViewHolderIncomeItem extends BaseViewHolder {

    private Activity mActivity;
    private HomeViewModel mViewModel;
    private IncomeItemBinding mBinding;

    ViewHolderIncomeItem(Activity activity, HomeViewModel viewModel, IncomeItemBinding binding) {
        super(binding.getRoot());
        mActivity = activity;
        mViewModel = viewModel;
        mBinding = binding;
    }

    @Override
    void bindData(HomeDisplayData data) {
        if (data != null && data.getInternal() != null && data.getInternal() instanceof Income) {
            Income income = (Income) data.getInternal();
            mBinding.setActivity(mActivity);
            mBinding.setVm(mViewModel);
            mBinding.setData(income);
            mBinding.executePendingBindings();
        }
    }
}
