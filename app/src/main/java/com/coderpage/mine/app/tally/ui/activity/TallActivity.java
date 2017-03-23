package com.coderpage.mine.app.tally.ui.activity;

import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.common.event.EventRecordAdd;
import com.coderpage.mine.app.tally.common.event.EventRecordDelete;
import com.coderpage.mine.app.tally.common.event.EventRecordUpdate;
import com.coderpage.mine.app.tally.data.CategoryIconHelper;
import com.coderpage.mine.app.tally.data.ExpenseItem;
import com.coderpage.mine.app.tally.provider.TallyContract;
import com.coderpage.mine.app.tally.utils.TimeUtils;
import com.coderpage.mine.ui.BaseActivity;
import com.coderpage.mine.ui.widget.ButtonGroupDialog;
import com.coderpage.mine.ui.widget.DrawShadowFrameLayout;
import com.coderpage.mine.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * @author abner-l. 2017-01-23
 * @since 0.1.0
 */

public class TallActivity extends BaseActivity {

    RecyclerView mHistoryRecordsRecycler;
    MToadyRecordAdapter mAdapter;
    TextView mSumOfMonthAmountTv;

    DecimalFormat mAmountDecimalFormat = new DecimalFormat(".00");
    String mAmountFormat;
    float mSumOfMonthAmount = 0.0F;

    private List<ExpenseItem> mTodayRecordList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tally);
        setTitle(R.string.tally_toolbar_title_main);
        mAmountFormat = getString(R.string.tally_amount_cny);

        initView();
        EventBus.getDefault().register(this);
    }

    private void initView() {
        mHistoryRecordsRecycler = ((RecyclerView) findViewById(R.id.recyclerHistoryRecord));
        mHistoryRecordsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new MToadyRecordAdapter();
        mHistoryRecordsRecycler.setAdapter(mAdapter);
        mSumOfMonthAmountTv = ((TextView) findViewById(R.id.tvMonthAmount));

        findViewById(R.id.btnAddRecord).setOnClickListener(mOnClickListener);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        queryRecordAndInitData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int actionBarSize = UIUtils.calculateActionBarSize(this);
        DrawShadowFrameLayout drawShadowFrameLayout =
                (DrawShadowFrameLayout) findViewById(R.id.main_content);
        if (drawShadowFrameLayout != null) {
            drawShadowFrameLayout.setShadowTopOffset(actionBarSize);
        }
        setContentTopClearance(actionBarSize);
    }

    private void setContentTopClearance(int clearance) {
        View view = findViewById(R.id.lyContainer);
        if (view != null) {
            view.setPadding(view.getPaddingLeft(), clearance,
                    view.getPaddingRight(), view.getPaddingBottom());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.mine_tally, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_refresh:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void queryRecordAndInitData() {
        new CursorLoader(getApplicationContext(),
                TallyContract.Expense.CONTENT_URI, null, null, null, "expense_time DESC") {

            @Override
            public void deliverResult(Cursor cursor) {
                Calendar calendar = Calendar.getInstance();
                int currentMonth = calendar.get(Calendar.MONTH);

                mSumOfMonthAmount = 0.0F;
                mTodayRecordList.clear();
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(cursor.getColumnIndex(TallyContract.Expense._ID));
                    long categoryId = cursor.getLong(cursor.getColumnIndex(TallyContract.Expense.CATEGORY_ID));
                    float amount = cursor.getFloat(cursor.getColumnIndex(TallyContract.Expense.AMOUNT));
                    String categoryName = cursor.getString(cursor.getColumnIndex(TallyContract.Expense.CATEGORY));
                    String desc = cursor.getString(cursor.getColumnIndex(TallyContract.Expense.DESC));
                    long time = cursor.getLong(cursor.getColumnIndex(TallyContract.Expense.TIME));
                    String categoryIcon = cursor.getString(cursor.getColumnIndex(TallyContract.Category.ICON));

                    ExpenseItem item = new ExpenseItem();
                    item.setId(id);
                    item.setCategoryId(categoryId);
                    item.setAmount(amount);
                    item.setCategoryName(categoryName);
                    item.setDesc(desc);
                    item.setTime(time);
                    item.setCategoryIconResId(CategoryIconHelper.resId(categoryIcon));

                    mTodayRecordList.add(item);

                    calendar.setTimeInMillis(time);
                    if (calendar.get(Calendar.MONTH) == currentMonth) {
                        mSumOfMonthAmount += amount;
                    }
                }
                cursor.close();
                mAdapter.notifyDataSetChanged();
                mSumOfMonthAmountTv.setText(
                        String.format(mAmountFormat, mAmountDecimalFormat.format(mSumOfMonthAmount)));

                super.deliverResult(cursor);
            }
        }.startLoading();
    }

    private class MToadyRecordAdapter extends RecyclerView.Adapter<MToadyRecordAdapter.MViewHolder> {
        private LayoutInflater mInflater;

        private MToadyRecordAdapter() {
            mInflater = LayoutInflater.from(TallActivity.this);
        }

        @Override
        public int getItemCount() {
            return mTodayRecordList.size();
        }

        @Override
        public MToadyRecordAdapter.MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MViewHolder(mInflater.inflate(R.layout.tally_recycle_item_expense_record, null));
        }

        @Override
        public void onBindViewHolder(MToadyRecordAdapter.MViewHolder holder, int position) {
            ExpenseItem expenseItem = mTodayRecordList.get(position);
            holder.setExpense(expenseItem);
        }

        class MViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
            private AppCompatImageView mCategoryIcon;
            private TextView mAmountTv;
            private TextView mTimeTv;
            private TextView mCategoryNameTv;
            private ExpenseItem mExpense;

            MViewHolder(View view) {
                super(view);
                mAmountTv = ((TextView) view.findViewById(R.id.tvAmount));
                mTimeTv = ((TextView) view.findViewById(R.id.tvTime));
                mCategoryNameTv = ((TextView) view.findViewById(R.id.tvCategoryName));
                mCategoryIcon = ((AppCompatImageView) view.findViewById(R.id.ivCategoryIcon));
                view.setOnLongClickListener(this);
            }

            @Override
            public boolean onLongClick(View v) {
                if (mExpense == null) return true;

                ButtonGroupDialog dialog = new ButtonGroupDialog(TallActivity.this);
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.addItem(R.string.delete, new ButtonGroupDialog.OnItemClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, View v) {
                        deleteExpense(mExpense.getId());
                        dialog.dismiss();
                    }
                });
                dialog.addItem(R.string.modify, new ButtonGroupDialog.OnItemClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, View v) {
                        Intent intent = new Intent(TallActivity.this, TallyExpenseAddActivity.class);
                        intent.putExtra(TallyExpenseAddActivity.EXTRA_RECORD_ID, mExpense.getId());
                        startActivity(intent);
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
        }
    }

    private void deleteExpense(final long id) {
        AsyncTask.execute(() -> {
            getContentResolver()
                    .delete(TallyContract.Expense.CONTENT_URI, TallyContract.Expense._ID + "=" + id, null);
            EventBus.getDefault().post(new EventRecordDelete(null));
        });
    }

    private View.OnClickListener mOnClickListener = (View v) -> {
        int id = v.getId();
        switch (id) {
            case R.id.btnAddRecord:
                Intent intent = new Intent(TallActivity.this, TallyExpenseAddActivity.class);
                startActivity(intent);
                break;
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRecordAdd(EventRecordAdd event) {
        queryRecordAndInitData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRecordUpdate(EventRecordUpdate event) {
        queryRecordAndInitData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRecordDelete(EventRecordDelete event) {
        queryRecordAndInitData();
    }
}