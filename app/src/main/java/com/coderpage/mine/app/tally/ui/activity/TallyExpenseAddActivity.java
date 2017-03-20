package com.coderpage.mine.app.tally.ui.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CalendarView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.coderpage.framework.utils.LogUtils;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.ExpenseItem;
import com.coderpage.mine.app.tally.common.event.EventRecordAdd;
import com.coderpage.mine.app.tally.provider.TallyContract;
import com.coderpage.mine.app.tally.ui.widget.NumInputView;
import com.coderpage.mine.app.tally.utils.CategoryPicUtils;
import com.coderpage.mine.app.tally.utils.DatePickUtils;
import com.coderpage.mine.ui.BaseActivity;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TallyExpenseAddActivity extends BaseActivity {
    private static final String TAG = LogUtils.makeLogTag(TallyExpenseAddActivity.class);
    private static final String EXTRA_RECORD_ID = "extraRecordId";

    TextView mAmountTv;
    TextView mCategoryName;
    TextView mDateTv;
    AppCompatImageView mCategoryIcon;
    GridView mCategoryGv;
    NumInputView mNumInputView;

    private CategoryPickerAdapter mCategoryPickerAdapter;

    private HashMap<String, Integer> mCategoryIconMap;
    private final List<CategoryItem> mCategoryItems = new ArrayList<>();
    private Calendar mExpenseDate = Calendar.getInstance();
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
    private CategoryItem mCategory;
    private float mAmount;
    private String mAmountFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tally_expense_add);
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
        findViewById(R.id.ivClose).setOnClickListener(mOnclickListener);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        new DataInitTask().execute();
    }

    private class DataInitTask extends AsyncTask {
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

        }
    }

    private void loadDefaultData() {
        mAmount = 0.0F;
        if (!mCategoryItems.isEmpty()) {
            mCategory = mCategoryItems.get(0);
        }
    }

    private void loadCategoryData() {
        mCategoryIconMap = CategoryPicUtils.getCategoryIconResMap(getApplicationContext());
        Cursor cursor = getContentResolver().query(TallyContract.Category.CONTENT_URI, null, null, null, "category_order DESC");
        if (cursor == null) return;
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(TallyContract.Category._ID));
            String name = cursor.getString(cursor.getColumnIndex(TallyContract.Category.NAME));
            String icon = cursor.getString(cursor.getColumnIndex(TallyContract.Category.ICON));
            int order = cursor.getInt(cursor.getColumnIndex(TallyContract.Category.ORDER));
            CategoryItem item = new CategoryItem();
            item.setIcon(mCategoryIconMap.get(icon));
            item.setId(id);
            item.setName(name);
            item.setOrder(order);
            mCategoryItems.add(item);
        }
        cursor.close();
    }

    private Drawable getDrawableSelf(@DrawableRes int res) {
        return getResources().getDrawable(res);
    }

    private void insertRecord(final ExpenseItem item) {
        Runnable insertTask = new Runnable() {
            @Override
            public void run() {
                ContentValues values = new ContentValues();
                values.put(TallyContract.Expense.AMOUNT, item.getAmount());
                values.put(TallyContract.Expense.CATEGORY_ID, item.getCategoryId());
                values.put(TallyContract.Expense.CATEGORY, item.getCategoryName());
                values.put(TallyContract.Expense.DESC, "");
                values.put(TallyContract.Expense.TIME, item.getTime());

                getContentResolver().insert(TallyContract.Expense.CONTENT_URI, values);
                EventBus.getDefault().post(new EventRecordAdd(item));
            }
        };
        AsyncTask.execute(insertTask);
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
                    DatePickUtils.showDatePickDialog(TallyExpenseAddActivity.this, new DatePickUtils.OnDatePickListener() {

                        @Override
                        public void onDatePick(DialogInterface dialog, @NonNull CalendarView view, int year, int month, int dayOfMonth) {
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
                case R.id.ivClose:
                    finish();
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
                    item.setAmount(mAmount);
                    item.setCategoryName(mCategory.getName());
                    item.setCategoryId(mCategory.getId());
                    item.setTime(mExpenseDate.getTimeInMillis());
                    insertRecord(item);
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
