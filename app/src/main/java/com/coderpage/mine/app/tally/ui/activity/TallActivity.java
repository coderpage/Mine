package com.coderpage.mine.app.tally.ui.activity;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.ExpenseItem;
import com.coderpage.mine.app.tally.common.event.EventRecordAdd;
import com.coderpage.mine.app.tally.provider.TallyContract;
import com.coderpage.mine.app.tally.utils.CategoryPicUtils;
import com.coderpage.mine.app.tally.utils.TimeUtils;
import com.coderpage.mine.ui.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * @author abner-l. 2017-01-23
 * @since 0.1.0
 */

public class TallActivity extends BaseActivity {

    RecyclerView mToadyRecordsRecycler;
    MToadyRecordAdapter mAdapter;
    TextView mSumOfMonthAmountTv;

    DecimalFormat mAmountDecimalFormat = new DecimalFormat(".00");
    String mAmountFormat;
    float mSumOfMonthAmount = 0.0F;

    private List<ExpenseItem> mTodayRecordList = new ArrayList<>();
    private HashMap<String, Integer> mCategoryIconResMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tally);
        mCategoryIconResMap = CategoryPicUtils.getCategoryIconResMap(getApplicationContext());
        mAmountFormat = getString(R.string.tally_amount_cny);

        initView();
        EventBus.getDefault().register(this);
    }

    private void initView() {
        mToadyRecordsRecycler = ((RecyclerView) findViewById(R.id.recyclerHistoryRecord));
        mToadyRecordsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new MToadyRecordAdapter();
        mToadyRecordsRecycler.setAdapter(mAdapter);
        mSumOfMonthAmountTv = ((TextView) findViewById(R.id.tvMonthAmount));

        findViewById(R.id.btnAddRecord).setOnClickListener(mOnClickListener);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        queryRecordAndInitData();
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
                    item.setCategoryIconResId(mCategoryIconResMap.get(categoryIcon));

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
            holder.setAmount(String.valueOf(expenseItem.getAmount()));
            holder.setCategoryName(expenseItem.getCategoryName());
            holder.setTime(expenseItem.getTime());
            holder.setCategoryIcon(expenseItem.getCategoryIconResId());
        }

        class MViewHolder extends RecyclerView.ViewHolder {
            private ImageView mCategoryIcon;
            private TextView mAmountTv;
            private TextView mTimeTv;
            private TextView mCategoryNameTv;

            MViewHolder(View view) {
                super(view);
                mAmountTv = ((TextView) view.findViewById(R.id.tvAmount));
                mTimeTv = ((TextView) view.findViewById(R.id.tvTime));
                mCategoryNameTv = ((TextView) view.findViewById(R.id.tvCategoryName));
                mCategoryIcon = ((ImageView) view.findViewById(R.id.ivCategoryIcon));
            }

            private void setAmount(String amount) {
                String amountStr = String.format(Locale.getDefault(), mAmountFormat, amount);
                mAmountTv.setText(amountStr);
            }

            private void setCategoryIcon(@DrawableRes int iconResId) {
                mCategoryIcon.setBackgroundResource(iconResId);
            }

            private void setTime(long time) {
                mTimeTv.setText(TimeUtils.getRecordDisplayDate(time));
            }

            private void setCategoryName(String categoryName) {
                mCategoryNameTv.setText(categoryName);
            }
        }
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int left;
        private int top;
        private int right;
        private int bottom;


        public SpacesItemDecoration(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = left;
            outRect.right = right;
            outRect.bottom = bottom;

            if (parent.getChildLayoutPosition(view) == 0)
                outRect.top = top;
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.btnAddRecord:
                    Intent intent = new Intent(TallActivity.this, TallyExpenseAddActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRecordAdd(EventRecordAdd event) {
        queryRecordAndInitData();
    }

}
