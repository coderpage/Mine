package com.coderpage.mine.app.tally.chart;

import android.app.Activity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.chart.data.MonthCategoryExpense;
import com.coderpage.utils.ResUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author abner-l. 2017-05-11
 */

class MonthCategoryExpenseAdapter
        extends RecyclerView.Adapter<MonthCategoryExpenseAdapter.MViewHolder> {

    private Activity mActivity;
    private LayoutInflater mInflater;
    private List<MonthCategoryExpense> mDataList = new ArrayList<>();
    private DecimalFormat mDecimalFormat = new DecimalFormat("0.0");

    MonthCategoryExpenseAdapter(Activity activity) {
        mActivity = activity;
        mInflater = LayoutInflater.from(mActivity);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MViewHolder(mInflater.inflate(
                R.layout.tally_recycle_item_month_category_expense, null));
    }

    @Override
    public void onBindViewHolder(MViewHolder holder, int position) {
        holder.setData(mDataList.get(position));
    }

    void refreshData(List<MonthCategoryExpense> list) {
        mDataList.clear();
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

    class MViewHolder extends RecyclerView.ViewHolder {
        private MonthCategoryExpense mExpense;

        private AppCompatImageView mIconIv;
        private TextView mCategoryNameTv;
        private TextView mPercentTv;
        private TextView mAmountTv;

        MViewHolder(View view) {
            super(view);
            mIconIv = (AppCompatImageView) view.findViewById(R.id.ivCategoryIcon);
            mCategoryNameTv = (TextView) view.findViewById(R.id.tvCategoryName);
            mPercentTv = (TextView) view.findViewById(R.id.tvCategoryPercent);
            mAmountTv = (TextView) view.findViewById(R.id.tvCategoryExpenseTotal);
        }

        void setData(MonthCategoryExpense expense) {
            mExpense = expense;
            mIconIv.setImageResource(mExpense.getCategoryIcon());
            mCategoryNameTv.setText(mExpense.getCategoryName());

            float percent = 0.0f;
            if (mExpense.getMonthExpenseTotal() != 0) {
                percent = (mExpense.getCategoryExpenseTotal() / mExpense.getMonthExpenseTotal()) * 100;
            }
            String percentFormat = mDecimalFormat.format(percent) + "%";
            mPercentTv.setText(percentFormat);
            mAmountTv.setText(ResUtils.getString(mActivity,
                    R.string.tally_amount_cny, mDecimalFormat.format(mExpense.getCategoryExpenseTotal())));
        }
    }
}
