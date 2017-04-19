package com.coderpage.mine.app.tally.ui.activity;

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
import com.coderpage.framework.utils.LogUtils;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.data.ExpenseItem;
import com.coderpage.mine.app.tally.utils.TimeUtils;
import com.coderpage.mine.ui.widget.ButtonGroupDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.coderpage.framework.utils.LogUtils.LOGE;

/**
 * @author abner-l. 2017-04-13
 * @since 0.2.0
 */

public class MainHistoryExpenseAdapter extends RecyclerView.Adapter<MainHistoryExpenseAdapter.ExpenseItemViewHolder> {
    private static final String TAG = LogUtils.makeLogTag(MainHistoryExpenseAdapter.class);

    private Activity mActivity;
    private LayoutInflater mInflater;
    private UpdatableView.UserActionListener mUserActionListener;
    private String mAmountFormat;

    private ArrayList<ExpenseItem> mExpenseItemList = new ArrayList<>();

    public MainHistoryExpenseAdapter(Activity activity) {
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
        mAmountFormat = mActivity.getString(R.string.tally_amount_cny);
    }

    @Override
    public int getItemCount() {
        return mExpenseItemList.size();
    }

    @Override
    public ExpenseItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ExpenseItemViewHolder(mInflater.inflate(R.layout.tally_recycle_item_expense_record, null));
    }

    @Override
    public void onBindViewHolder(ExpenseItemViewHolder holder, int position) {
        ExpenseItem expenseItem = mExpenseItemList.get(position);
        holder.setExpense(expenseItem);
    }

    public void setUserActionListener(UpdatableView.UserActionListener userActionListener) {
        mUserActionListener = userActionListener;
    }

    class ExpenseItemViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        private AppCompatImageView mCategoryIcon;
        private TextView mAmountTv;
        private TextView mTimeTv;
        private TextView mCategoryNameTv;
        private TextView mDescTv;
        private ExpenseItem mExpense;

        ExpenseItemViewHolder(View view) {
            super(view);
            mAmountTv = ((TextView) view.findViewById(R.id.tvAmount));
            mTimeTv = ((TextView) view.findViewById(R.id.tvTime));
            mCategoryNameTv = ((TextView) view.findViewById(R.id.tvCategoryName));
            mDescTv = ((TextView) view.findViewById(R.id.tvRecordDec));
            mCategoryIcon = ((AppCompatImageView) view.findViewById(R.id.ivCategoryIcon));
            view.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            if (mExpense == null) return true;

            ButtonGroupDialog dialog = new ButtonGroupDialog(mActivity);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.addItem(R.string.delete, new ButtonGroupDialog.OnItemClickListener() {
                @Override
                public void onClick(DialogInterface dialog, View v) {
                    Bundle args = new Bundle(1);
                    args.putLong(MainModel.EXTRA_EXPENSE_ID, mExpense.getId());
                    mUserActionListener.onUserAction(MainModel.MainUserActionEnum.EXPENSE_DELETE, args);
                    dialog.dismiss();
                }
            });
            dialog.addItem(R.string.modify, new ButtonGroupDialog.OnItemClickListener() {
                @Override
                public void onClick(DialogInterface dialog, View v) {
                    Intent intent = new Intent(mActivity, ExpenseEditActivity.class);
                    intent.putExtra(ExpenseEditActivity.EXTRA_RECORD_ID, mExpense.getId());
                    mActivity.startActivity(intent);
                    dialog.dismiss();
                }
            });
            dialog.show();
            return true;
        }

        private void setExpense(ExpenseItem expense) {
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

    public ArrayList<ExpenseItem> getDataList() {
        return mExpenseItemList;
    }

    public void refreshData(List<ExpenseItem> items) {
        mExpenseItemList.clear();
        mExpenseItemList.addAll(items);
        notifyDataSetChanged();
    }

    public void refreshItem(ExpenseItem item) {
        if (item == null) {
            LOGE(TAG, "can't notify item changed with null value");
            return;
        }
        int position = -1;
        for (int i = 0; i < mExpenseItemList.size(); i++) {
            ExpenseItem item1 = mExpenseItemList.get(i);
            if (item.getId() == item1.getId()) {
                item1.setAmount(item.getAmount());
                item1.setCategoryId(item.getCategoryId());
                item1.setCategoryName(item.getCategoryName());
                item1.setDesc(item.getDesc());
                item1.setCategoryIconResId(item.getCategoryIconResId());
                item1.setTime(item.getTime());
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


    /***
     * 添加一个新支出记录，显示在列表顶部
     * @param item 新支出记录
     */
    public void addNewItem(ExpenseItem item) {
        if (item == null) {
            return;
        }
        if (mExpenseItemList.size() == 0) {
            mExpenseItemList.add(item);
        } else {
            mExpenseItemList.add(0, item);
        }
        notifyDataSetChanged();
    }

    public void removeItem(long expenseId) {
        int position = -1;
        for (int i = 0; i < mExpenseItemList.size(); i++) {
            if (expenseId == mExpenseItemList.get(i).getId()) {
                position = i;
                break;
            }
        }
        if (position != -1) {
            mExpenseItemList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void addHistoryItems(List<ExpenseItem> items) {
        int insertPositionStart = mExpenseItemList.size();
        mExpenseItemList.addAll(items);
        notifyItemRangeInserted(insertPositionStart, items.size());
    }
}
