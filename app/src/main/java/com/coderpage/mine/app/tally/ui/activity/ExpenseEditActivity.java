package com.coderpage.mine.app.tally.ui.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.coderpage.framework.utils.LogUtils;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.eventbus.EventRecordAdd;
import com.coderpage.mine.app.tally.eventbus.EventRecordUpdate;
import com.coderpage.mine.app.tally.data.CategoryIconHelper;
import com.coderpage.mine.app.tally.data.CategoryItem;
import com.coderpage.mine.app.tally.data.ExpenseItem;
import com.coderpage.mine.app.tally.provider.ProviderUtils;
import com.coderpage.mine.app.tally.provider.TallyContract;
import com.coderpage.mine.app.tally.ui.widget.NumInputView;
import com.coderpage.mine.app.tally.utils.DatePickUtils;
import com.coderpage.mine.ui.BaseActivity;
import com.coderpage.mine.ui.widget.DrawShadowFrameLayout;
import com.coderpage.mine.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ExpenseEditActivity extends BaseActivity {
    private static final String TAG = LogUtils.makeLogTag(ExpenseEditActivity.class);
    public static final String EXTRA_RECORD_ID = "extraRecordId";

    TextView mAmountTv;
    TextView mCategoryName;
    TextView mDateTv;
    AppCompatImageView mCategoryIcon;
    GridView mCategoryGv;
    NumInputView mNumInputView;

    private CategoryPickerAdapter mCategoryPickerAdapter;

    private final List<CategoryItem> mCategoryItems = new ArrayList<>();
    private Calendar mExpenseDate = Calendar.getInstance();
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
    private CategoryItem mCategory;
    private float mAmount;
    private String mAmountFormat;
    private long mExpenseId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tally_expense_add);
        setTitle(R.string.tally_toolbar_title_add_record);
        mAmountFormat = getString(R.string.tally_amount_cny);
        initView();
    }

    private void initView() {

        mAmountTv = ((TextView) findViewById(R.id.tvAmount));

        mCategoryIcon = ((AppCompatImageView) findViewById(R.id.ivCategoryIcon));
        mCategoryName = ((TextView) findViewById(R.id.tvCategoryName));

        mCategoryGv = ((GridView) findViewById(R.id.gvCategoryIcon));
        mCategoryGv.setOnItemClickListener(mCategorySelectListener);

        mNumInputView = ((NumInputView) findViewById(R.id.numInputView));
        mNumInputView.setInputListener(mNumInputListener);

        mCategoryPickerAdapter = new CategoryPickerAdapter(getApplicationContext());
        mCategoryGv.setAdapter(mCategoryPickerAdapter);

        mDateTv = ((TextView) findViewById(R.id.tvDate));

        mDateTv.setText(mDateFormat.format(mExpenseDate.getTime()));
        mDateTv.setOnClickListener(mOnclickListener);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setToolbarAsClose((View v) -> finish());
        new DataInitTask().execute();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
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

        Intent intent = getIntent();
        if (intent != null) {
            mExpenseId = intent.getLongExtra(EXTRA_RECORD_ID, -1);
        }
    }

    private void setContentTopClearance(int clearance) {
        View view = findViewById(R.id.lyContainer);
        if (view != null) {
            view.setPadding(view.getPaddingLeft(), clearance,
                    view.getPaddingRight(), view.getPaddingBottom());
        }
    }

    private class DataInitTask extends AsyncTask<Object, Object, Object> {
        @Override
        protected Object doInBackground(Object[] params) {
            loadCategoryData();
            loadDefaultData();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            mCategoryPickerAdapter.notifyDataSetChanged();
            mAmountTv.setText(String.format(mAmountFormat, mAmount));
            if (mCategory != null) {
                mCategoryIcon.setImageResource(mCategory.getIcon());
                mCategoryName.setText(mCategory.getName());
            }
            mDateTv.setText(mDateFormat.format(mExpenseDate.getTime()));
        }
    }

    private void loadDefaultData() {
        if (mExpenseId == -1) {
            mAmount = 0.0F;
            if (!mCategoryItems.isEmpty()) {
                mCategory = mCategoryItems.get(0);
            }
        } else {
            Cursor cursor = getContentResolver().query(
                    TallyContract.Expense.CONTENT_URI, null,
                    TallyContract.Expense._ID + "=" + mExpenseId, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                long categoryId = cursor.getLong(cursor.getColumnIndex(TallyContract.Expense.CATEGORY_ID));
                float amount = cursor.getFloat(cursor.getColumnIndex(TallyContract.Expense.AMOUNT));
                String categoryName = cursor.getString(cursor.getColumnIndex(TallyContract.Expense.CATEGORY));
                String desc = cursor.getString(cursor.getColumnIndex(TallyContract.Expense.DESC));
                long time = cursor.getLong(cursor.getColumnIndex(TallyContract.Expense.TIME));
                String categoryIcon = cursor.getString(cursor.getColumnIndex(TallyContract.Category.ICON));

                mAmount = amount;
                mCategory = new CategoryItem();
                mCategory.setIcon(CategoryIconHelper.resId(categoryIcon));
                mCategory.setId(categoryId);
                mCategory.setName(categoryName);
                mExpenseDate = Calendar.getInstance();
                mExpenseDate.setTimeInMillis(time);

                cursor.close();
            }
        }

    }

    private void loadCategoryData() {
        Cursor cursor = getContentResolver().query(TallyContract.Category.CONTENT_URI, null, null, null, "category_order DESC");
        if (cursor == null) return;
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(TallyContract.Category._ID));
            String name = cursor.getString(cursor.getColumnIndex(TallyContract.Category.NAME));
            String icon = cursor.getString(cursor.getColumnIndex(TallyContract.Category.ICON));
            int order = cursor.getInt(cursor.getColumnIndex(TallyContract.Category.ORDER));
            CategoryItem item = new CategoryItem();
            item.setIcon(CategoryIconHelper.resId(icon));
            item.setId(id);
            item.setName(name);
            item.setOrder(order);
            mCategoryItems.add(item);
        }
        cursor.close();
    }


    private void insertRecord(final ExpenseItem item) {
        AsyncTask.execute(() -> {
            ContentValues values = new ContentValues();
            values.put(TallyContract.Expense.AMOUNT, item.getAmount());
            values.put(TallyContract.Expense.CATEGORY_ID, item.getCategoryId());
            values.put(TallyContract.Expense.CATEGORY, item.getCategoryName());
            values.put(TallyContract.Expense.DESC, "");
            values.put(TallyContract.Expense.TIME, item.getTime());

            Uri uri = getContentResolver().insert(TallyContract.Expense.CONTENT_URI, values);
            item.setId(ProviderUtils.parseIdFromUri(uri));
            EventBus.getDefault().post(new EventRecordAdd(item));
        });
    }

    private void updateRecord(final ExpenseItem item) {
        AsyncTask.execute(() -> {
            ContentValues values = new ContentValues();
            values.put(TallyContract.Expense.AMOUNT, item.getAmount());
            values.put(TallyContract.Expense.CATEGORY_ID, item.getCategoryId());
            values.put(TallyContract.Expense.CATEGORY, item.getCategoryName());
            values.put(TallyContract.Expense.DESC, "");
            values.put(TallyContract.Expense.TIME, item.getTime());

            getContentResolver().update(TallyContract.Expense.CONTENT_URI, values, TallyContract.Expense._ID + "=" + item.getId(), null);
            EventBus.getDefault().post(new EventRecordUpdate(item));
        });
    }

    private class CategoryPickerAdapter extends BaseAdapter {

        LayoutInflater mInflater;

        CategoryPickerAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mCategoryItems.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return mCategoryItems.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.tally_grid_item_category, null);
            }
            CategoryItem item = mCategoryItems.get(position);

            ImageView icon = ((ImageView) convertView.findViewById(R.id.ivIcon));
            TextView name = ((TextView) convertView.findViewById(R.id.tvName));

            icon.setImageResource(item.getIcon());

            name.setText(item.getName());

            return convertView;
        }
    }

    AdapterView.OnItemClickListener mCategorySelectListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mCategory = mCategoryItems.get(position);
            mCategoryIcon.setImageResource(mCategory.getIcon());
            mCategoryName.setText(mCategory.getName());
        }
    };

    View.OnClickListener mOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            int id = v.getId();
            switch (id) {
                case R.id.tvDate:
                    DatePickUtils.showDatePickDialog(ExpenseEditActivity.this, new DatePickUtils.OnDatePickListener() {

                        @Override
                        public void onDatePick(DialogInterface dialog, int year, int month, int dayOfMonth) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year, month, dayOfMonth);
                            mExpenseDate = calendar;
                        }

                        @Override
                        public void onConfirmClick(DialogInterface dialog) {
                            mDateTv.setText(mDateFormat.format(mExpenseDate.getTime()));
                        }
                    });
                    break;
            }
        }
    };

    private NumInputView.InputListener mNumInputListener = new NumInputView.InputListener() {
        @Override
        public void onKeyClick(int code) {
            switch (code) {
                case KeyEvent.KEYCODE_ENTER:
                    ExpenseItem item = new ExpenseItem();
                    item.setId(mExpenseId);
                    item.setAmount(mAmount);
                    item.setCategoryName(mCategory.getName());
                    item.setCategoryId(mCategory.getId());
                    item.setTime(mExpenseDate.getTimeInMillis());
                    if (mExpenseId == -1) {
                        insertRecord(item);
                    } else {
                        updateRecord(item);
                    }

                    finish();
                    break;
            }
        }

        @Override
        public void onNumChange(float newNum) {
            mAmount = newNum;
            mAmountTv.setText(String.format(mAmountFormat, mAmount));
        }
    };
}
