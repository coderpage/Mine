package com.coderpage.mine.app.tally.module.home;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.module.home.model.HomeDisplayData;
import com.coderpage.mine.app.tally.persistence.model.Expense;
import com.coderpage.mine.app.tally.persistence.model.Income;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lc. 2018-07-21 10:48
 * @since 0.6.0
 */

class HomeAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Activity mActivity;
    private HomeViewModel mViewModel;
    private LayoutInflater mInflater;
    private List<HomeDisplayData> mDataList = new ArrayList<>();

    HomeAdapter(Activity activity, HomeViewModel viewModel) {
        mActivity = activity;
        mViewModel = viewModel;
        mInflater = LayoutInflater.from(mActivity);
    }

    void setDataList(List<HomeDisplayData> dataList) {
        if (dataList == null) {
            return;
        }
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return mDataList.size();
            }

            @Override
            public int getNewListSize() {
                return dataList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                HomeDisplayData oldData = mDataList.get(oldItemPosition);
                HomeDisplayData newData = dataList.get(newItemPosition);

                if (oldData.getType() != newData.getType()) {
                    return false;
                }

                switch (newData.getType()) {
                    case HomeDisplayData.TYPE_MONTH_INFO:
                    case HomeDisplayData.TYPE_TODAY_EXPENSE:
                        return true;

                    case HomeDisplayData.TYPE_EXPENSE_ITEM:
                        Expense oldExpense = (Expense) oldData.getInternal();
                        Expense newExpense = (Expense) newData.getInternal();
                        return oldExpense.getId() == newExpense.getId();

                    case HomeDisplayData.TYPE_IN_COME_ITEM:
                        Income oldIncome = (Income) oldData.getInternal();
                        Income newIncome = (Income) newData.getInternal();
                        return oldIncome.getId() == newIncome.getId();

                    default:
                        break;
                }
                return false;
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return false;
            }
        });
        mDataList.clear();
        mDataList.addAll(dataList);
        result.dispatchUpdatesTo(this);
    }

    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {

            // 月消费、月收入信息模块
            case HomeDisplayData.TYPE_MONTH_INFO:
                return new ViewHolderMonthInfo(mActivity, mViewModel,
                        DataBindingUtil.inflate(mInflater, R.layout.tally_module_home_item_month_info, parent, false));

            // 今日消费数据
            case HomeDisplayData.TYPE_TODAY_EXPENSE:
                return new ViewHolderTodayExpense(mActivity, mViewModel,
                        DataBindingUtil.inflate(mInflater, R.layout.tally_module_home_item_today_expense, parent, false));

            // 消费记录 ITEM
            case HomeDisplayData.TYPE_EXPENSE_ITEM:
                return new ViewHolderExpenseItem(mActivity, mViewModel,
                        DataBindingUtil.inflate(mInflater, R.layout.tally_module_home_expense_item, parent, false));

            // 支出记录 ITEM
            case HomeDisplayData.TYPE_IN_COME_ITEM:
                return new ViewHolderIncomeItem(mActivity, mViewModel,
                        DataBindingUtil.inflate(mInflater, R.layout.tally_module_home_income_item, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.bindData(mDataList.get(position));
    }
}
