package com.coderpage.mine.app.tally.records;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coderpage.framework.UpdatableView;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.chart.ChartActivity;
import com.coderpage.mine.app.tally.data.Expense;
import com.coderpage.mine.app.tally.detail.ExpenseDetailActivity;
import com.coderpage.mine.app.tally.edit.ExpenseEditActivity;
import com.coderpage.mine.app.tally.utils.TimeUtils;
import com.coderpage.mine.ui.widget.ButtonGroupDialog;
import com.coderpage.utils.LogUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.coderpage.utils.LogUtils.LOGE;

/**
 * @author abner-l. 2017-04-13
 * @since 0.2.0
 */

class HistoryRecordsAdapter extends RecyclerView.Adapter<HistoryRecordsAdapter.ExpenseItemViewHolder> {
    private static final String TAG = LogUtils.makeLogTag(HistoryRecordsAdapter.class);

    private Activity mActivity;
    private LayoutInflater mInflater;
    private UpdatableView.UserActionListener mUserActionListener;
    private String mAmountFormat;

    private ArrayList<RecyclerItem> mExpenseItemList = new ArrayList<>();

    HistoryRecordsAdapter(Activity activity) {
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
        mAmountFormat = mActivity.getString(R.string.tally_amount_cny);
    }

    @Override
    public int getItemCount() {
        return mExpenseItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mExpenseItemList.get(position).getType();
    }

    @Override
    public ExpenseItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == RecyclerItem.TYPE_EXPENSE_ITEM) {
            return new ExpenseItemViewHolder(
                    mInflater.inflate(R.layout.tally_recycle_item_expense_record, parent, false), viewType);
        } else {
            return new ExpenseItemViewHolder(
                    mInflater.inflate(
                            R.layout.tally_recycler_item_expense_month_title, parent, false), viewType);
        }
    }

    @Override
    public void onBindViewHolder(ExpenseItemViewHolder holder, int position) {
        holder.setExpense(mExpenseItemList.get(position));
    }

    void setUserActionListener(UpdatableView.UserActionListener userActionListener) {
        mUserActionListener = userActionListener;
    }

    class ExpenseItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnLongClickListener, View.OnClickListener {
        private AppCompatImageView mCategoryIcon;
        private TextView mAmountTv;
        private TextView mTimeTv;
        private TextView mCategoryNameTv;
        private TextView mDescTv;
        private TextView mMonthTv;
        private RecyclerItem mRecyclerItem;

        ExpenseItemViewHolder(View view, int type) {
            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            if (type == RecyclerItem.TYPE_EXPENSE_ITEM) {
                mAmountTv = ((TextView) view.findViewById(R.id.tvAmount));
                mTimeTv = ((TextView) view.findViewById(R.id.tvTime));
                mCategoryNameTv = ((TextView) view.findViewById(R.id.tvCategoryName));
                mDescTv = ((TextView) view.findViewById(R.id.tvRecordDec));
                mCategoryIcon = ((AppCompatImageView) view.findViewById(R.id.ivCategoryIcon));
            } else {
                mMonthTv = (TextView) view.findViewById(R.id.tvMonth);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mRecyclerItem == null || mRecyclerItem.getExpense() == null) return true;
            if (mRecyclerItem.getType() == RecyclerItem.TYPE_MONTH_TITLE) return true;
            ButtonGroupDialog dialog = new ButtonGroupDialog(mActivity);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.addItem(R.string.delete, new ButtonGroupDialog.OnItemClickListener() {
                @Override
                public void onClick(DialogInterface dialog, View v) {
                    Bundle args = new Bundle(1);
                    args.putLong(RecordsModel.EXTRA_EXPENSE_ID, mRecyclerItem.getExpense().getId());
                    mUserActionListener.onUserAction(RecordsModel.RecordsUserActionEnum.EXPENSE_DELETE, args);
                    dialog.dismiss();
                }
            });
            dialog.addItem(R.string.modify, new ButtonGroupDialog.OnItemClickListener() {
                @Override
                public void onClick(DialogInterface dialog, View v) {
                    Intent intent = new Intent(mActivity, ExpenseEditActivity.class);
                    intent.putExtra(ExpenseEditActivity.EXTRA_RECORD_ID, mRecyclerItem.getExpense().getId());
                    mActivity.startActivity(intent);
                    dialog.dismiss();
                }
            });
            dialog.show();
            return true;
        }

        @Override
        public void onClick(View v) {
            if (mRecyclerItem == null) return;
            if (mRecyclerItem.getType() == RecyclerItem.TYPE_EXPENSE_ITEM) {
                ExpenseDetailActivity.open(mActivity, mRecyclerItem.getExpense().getId(), v);
            }
            if (mRecyclerItem.getType() == RecyclerItem.TYPE_MONTH_TITLE) {
                ChartActivity.open(mActivity, mRecyclerItem.getYear(), mRecyclerItem.getMonth());
            }
        }

        private void setExpense(RecyclerItem recyclerItem) {
            mRecyclerItem = recyclerItem;
            if (mRecyclerItem == null) return;
            if (recyclerItem.getType() == RecyclerItem.TYPE_EXPENSE_ITEM) {
                setAmount(String.valueOf(mRecyclerItem.getExpense().getAmount()));
                setCategoryName(mRecyclerItem.getExpense().getCategoryName());
                setTime(mRecyclerItem.getExpense().getTime());
                setCategoryIcon(mRecyclerItem.getExpense().getCategoryIconResId());
                setDesc(mRecyclerItem.getExpense().getDesc());
            } else {
                setMonthTitle(mRecyclerItem.getYear() + "/" + mRecyclerItem.getMonth());
            }
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

        private void setMonthTitle(String monthTitle) {
            mMonthTv.setText(monthTitle);
        }
    }

    Expense getLastExpenseShow() {
        if (mExpenseItemList.isEmpty()) {
            return null;
        }
        for (int i = mExpenseItemList.size() - 1; i >= 0; i--) {
            RecyclerItem item = mExpenseItemList.get(i);
            if (item.getType() == RecyclerItem.TYPE_EXPENSE_ITEM) {
                return item.getExpense();
            }
        }
        return null;
    }

    void refreshData(List<Expense> items) {
        mExpenseItemList.clear();
        mExpenseItemList.addAll(formatToRecyclerItemList(items, true));
        notifyDataSetChanged();
    }

    void refreshItem(Expense item) {
        if (item == null) {
            LOGE(TAG, "can't notify item changed with null value");
            return;
        }
        int position = -1;
        for (int i = 0; i < mExpenseItemList.size(); i++) {
            RecyclerItem item1 = mExpenseItemList.get(i);

            if (item1.getType() == RecyclerItem.TYPE_EXPENSE_ITEM
                    && item.getId() == item1.getExpense().getId()) {
                item1.getExpense().setAmount(item.getAmount());
                item1.getExpense().setCategoryId(item.getCategoryId());
                item1.getExpense().setCategoryName(item.getCategoryName());
                item1.getExpense().setDesc(item.getDesc());
                item1.getExpense().setCategoryIconResId(item.getCategoryIconResId());
                item1.getExpense().setTime(item.getTime());
                position = i;
                break;
            }
        }
        if (position != -1) {
            notifyItemChanged(position);
        } else {
            LOGE(TAG, "can't notify item changed: id=" + item.getId()
                    + " category=" + item.getCategoryName() + " amount=" + item.getAmount());
        }
    }

    void removeItem(long expenseId) {
        int position = -1;
        for (int i = 0; i < mExpenseItemList.size(); i++) {
            RecyclerItem item = mExpenseItemList.get(i);
            if (item.getType() == RecyclerItem.TYPE_EXPENSE_ITEM
                    && expenseId == item.getExpense().getId()) {
                position = i;
                break;
            }
        }
        if (position != -1) {
            mExpenseItemList.remove(position);
            notifyItemRemoved(position);
        }
    }

    void addHistoryItems(List<Expense> items) {
        int insertPositionStart = mExpenseItemList.size();
        mExpenseItemList.addAll(formatToRecyclerItemList(items, false));
        notifyItemRangeInserted(insertPositionStart, items.size());
    }

    private List<RecyclerItem> formatToRecyclerItemList(List<Expense> items, boolean refreshAll) {
        List<RecyclerItem> result = new ArrayList<>(items.size());
        Calendar calendar = Calendar.getInstance();
        int yearFrontItem = -1;
        int monthFrontItem = -1;
        if (!refreshAll) {
            Expense lastExpenseShow = getLastExpenseShow();
            if (lastExpenseShow != null) {
                calendar.setTimeInMillis(lastExpenseShow.getTime());
                yearFrontItem = calendar.get(Calendar.YEAR);
                monthFrontItem = calendar.get(Calendar.MONTH) + 1;
            }
        }
        for (Expense expense : items) {
            long time = expense.getTime();
            calendar.setTimeInMillis(time);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            if (year != yearFrontItem || month != monthFrontItem) {
                result.add(new RecyclerItem(year, month));
                yearFrontItem = year;
                monthFrontItem = month;
            }
            result.add(new RecyclerItem(expense));
        }
        return result;
    }

    private class RecyclerItem {
        static final int TYPE_MONTH_TITLE = 1;
        static final int TYPE_EXPENSE_ITEM = 2;

        int type;
        private int year;
        private int month;
        private Expense expense;

        RecyclerItem(int year, int month) {
            this.year = year;
            this.month = month;
            this.type = TYPE_MONTH_TITLE;
        }

        RecyclerItem(Expense item) {
            this.expense = item;
            this.type = TYPE_EXPENSE_ITEM;
        }

        int getType() {
            return type;
        }

        public int getYear() {
            return year;
        }

        public int getMonth() {
            return month;
        }

        public Expense getExpense() {
            return expense;
        }
    }
}
