package com.coderpage.mine.app.tally.module.search;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coderpage.base.utils.LogUtils;
import com.coderpage.framework.UpdatableView;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.data.Expense;
import com.coderpage.mine.app.tally.module.detail.ExpenseDetailActivity;
import com.coderpage.mine.app.tally.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author lc. 2017-09-17
 * @since 0.1.0
 */

public class SearchResultAdapter extends
        RecyclerView.Adapter<SearchResultAdapter.ExpenseViewHolder> {
    private static final String TAG = LogUtils.makeLogTag(SearchResultAdapter.class);

    private Context mContext;
    private LayoutInflater mInflater;
    private UpdatableView.UserActionListener mUserActionListener;
    private String mAmountFormat;

    private ArrayList<Expense> mExpenseList = new ArrayList<>();

    SearchResultAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mAmountFormat = mContext.getString(R.string.tally_amount_cny);
    }

    void refresh(List<Expense> list) {
        mExpenseList.clear();
        mExpenseList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mExpenseList.size();
    }

    @Override
    public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ExpenseViewHolder(mInflater.inflate(
                R.layout.tally_recycle_item_expense_record, parent, false));
    }

    @Override
    public void onBindViewHolder(ExpenseViewHolder holder, int position) {
        Expense expense = mExpenseList.get(position);
        holder.setExpense(expense);
    }

    void setUserActionListener(UpdatableView.UserActionListener userActionListener) {
        mUserActionListener = userActionListener;
    }

    class ExpenseViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private AppCompatImageView mCategoryIcon;
        private TextView mAmountTv;
        private TextView mTimeTv;
        private TextView mCategoryNameTv;
        private TextView mDescTv;
        private Expense mExpense;

        ExpenseViewHolder(View view) {
            super(view);
            mAmountTv = ((TextView) view.findViewById(R.id.tvAmount));
            mTimeTv = ((TextView) view.findViewById(R.id.tvTime));
            mCategoryNameTv = ((TextView) view.findViewById(R.id.tvCategoryName));
            mDescTv = ((TextView) view.findViewById(R.id.tvRecordDec));
            mCategoryIcon = ((AppCompatImageView) view.findViewById(R.id.ivCategoryIcon));
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ExpenseDetailActivity.open(mContext, mExpense.getId(), v);
        }

        private void setExpense(Expense expense) {
            mExpense = expense;
            if (mExpense == null) return;
            setAmount(String.valueOf(mExpense.getAmount()));
            setCategoryName(mExpense.getCategoryName());
            setTime(mExpense.getTime());
            setCategoryIcon(mExpense.getCategoryIconResId());
            setDesc(mExpense.getDesc());
        }

        private void setAmount(String amount) {
            String amountStr = String.format(Locale.getDefault(), mAmountFormat, amount);
            mAmountTv.setText(amountStr);
        }

        private void setCategoryIcon(@DrawableRes int iconResId) {
            mCategoryIcon.setImageResource(iconResId);
        }

        private void setTime(long time) {
            mTimeTv.setText(TimeUtils.getRecordDisplayDate(time));
        }

        private void setCategoryName(String categoryName) {
            mCategoryNameTv.setText(categoryName);
        }

        private void setDesc(String desc) {
            if (TextUtils.isEmpty(desc)) {
                mDescTv.setVisibility(View.GONE);
            } else {
                mDescTv.setVisibility(View.VISIBLE);
                mDescTv.setText(desc);
            }
        }
    }
}
