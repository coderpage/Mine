package com.coderpage.mine.app.tally.edit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.coderpage.framework.UpdatableView;
import com.coderpage.utils.LogUtils;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.data.CategoryIconHelper;
import com.coderpage.mine.app.tally.data.CategoryItem;
import com.coderpage.mine.app.tally.data.ExpenseItem;
import com.coderpage.mine.app.tally.eventbus.EventRecordAdd;
import com.coderpage.mine.app.tally.eventbus.EventRecordUpdate;
import com.coderpage.mine.app.tally.ui.widget.NumInputView;
import com.coderpage.mine.app.tally.utils.DatePickUtils;
import com.coderpage.mine.ui.BaseActivity;
import com.coderpage.mine.ui.widget.DrawShadowFrameLayout;
import com.coderpage.mine.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ExpenseEditActivity extends BaseActivity
        implements UpdatableView<ExpenseEditModel,
        ExpenseEditModel.EditQueryEnum, ExpenseEditModel.EditUserActionEnum> {
    private static final String TAG = LogUtils.makeLogTag(ExpenseEditActivity.class);
    public static final String EXTRA_RECORD_ID = "extra_record_id";

    TextView mAmountTv;
    TextView mCategoryName;
    TextView mDateTv;
    AppCompatImageView mCategoryIcon;
    GridView mCategoryGv;
    NumInputView mNumInputView;
    EditText mDescEt;

    private CategoryPickerAdapter mCategoryPickerAdapter;

    private Calendar mExpenseDate = Calendar.getInstance();
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
    private CategoryItem mCategory;
    private float mAmount;
    private String mAmountFormat;
    private long mExpenseId = -1;

    private UserActionListener mUserActionListener;
    private ExpenseEditPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tally_expense_add);
        setTitle(R.string.tally_toolbar_title_add_record);

        initData();
        initView();
        initPresenter();
    }

    private void initView() {
        mAmountFormat = getString(R.string.tally_amount_cny);
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
        mDescEt = (EditText) findViewById(R.id.etDesc);

        mDateTv.setText(mDateFormat.format(mExpenseDate.getTime()));
        mDateTv.setOnClickListener(mOnclickListener);
    }

    private void initPresenter() {
        mPresenter = new ExpenseEditPresenter(
                new ExpenseEditModel(getContext()),
                this, ExpenseEditModel.EditUserActionEnum.values(),
                ExpenseEditModel.EditQueryEnum.values());
        mPresenter.loadInitialQueries();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            mExpenseId = intent.getLongExtra(EXTRA_RECORD_ID, -1);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setToolbarAsClose((View v) -> finish());

        // 刷新界面
        Bundle args = new Bundle(1);
        args.putLong(ExpenseEditModel.EXTRA_EXPENSE_ID, mExpenseId);
        mUserActionListener.onUserAction(ExpenseEditModel.EditUserActionEnum.RELOAD, args);
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

    AdapterView.OnItemClickListener mCategorySelectListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mCategory = mCategoryPickerAdapter.getCategoryItems().get(position);
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
                    DatePickUtils.showDatePickDialog(ExpenseEditActivity.this,
                            new DatePickUtils.OnDatePickListener() {

                        @Override
                        public void onDatePick(DialogInterface dialog,
                                               int year,
                                               int month,
                                               int dayOfMonth) {
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
                    Bundle args = new Bundle();
                    args.putLong(ExpenseEditModel.EXTRA_EXPENSE_ID, mExpenseId);
                    args.putFloat(ExpenseEditModel.EXTRA_EXPENSE_AMOUNT, mAmount);
                    args.putLong(ExpenseEditModel.EXTRA_EXPENSE_CATEGORY_ID, mCategory.getId());
                    args.putString(ExpenseEditModel.EXTRA_EXPENSE_CATEGORY, mCategory.getName());
                    args.putString(ExpenseEditModel.EXTRA_EXPENSE_DESC, mDescEt.getText().toString());
                    args.putLong(ExpenseEditModel.EXTRA_EXPENSE_TIME, mExpenseDate.getTimeInMillis());
                    args.putString(ExpenseEditModel.EXTRA_EXPENSE_DESC, mDescEt.getText().toString());
                    mUserActionListener.onUserAction(ExpenseEditModel.EditUserActionEnum.SAVE_DATA, args);
                    break;
            }
        }

        @Override
        public void onNumChange(float newNum) {
            mAmount = newNum;
            mAmountTv.setText(String.format(mAmountFormat, mAmount));
        }
    };


    @Override
    public void displayData(ExpenseEditModel model, ExpenseEditModel.EditQueryEnum query) {
        switch (query) {
            case LOAD_CATEGORY:
                mCategoryPickerAdapter.refreshData(model.getCategoryItemList());
                break;
        }
    }

    @Override
    public void displayErrorMessage(ExpenseEditModel.EditQueryEnum query) {

    }

    @Override
    public void displayUserActionResult(ExpenseEditModel model,
                                        Bundle args,
                                        ExpenseEditModel.EditUserActionEnum userAction,
                                        boolean success) {
        switch (userAction) {
            case RELOAD:
                if (success) {
                    ExpenseItem item = model.getExpenseItem();
                    mAmount = item.getAmount();
                    if (mCategory == null) {
                        mCategory = new CategoryItem();
                    }
                    mCategory.setIcon(CategoryIconHelper.resId(item.getCategoryName()));
                    mCategory.setId(item.getCategoryId());
                    mCategory.setName(item.getCategoryName());
                    mExpenseDate = Calendar.getInstance();
                    mExpenseDate.setTimeInMillis(item.getTime());

                    mAmountTv.setText(String.format(mAmountFormat, mAmount));
                    if (mCategory != null) {
                        mCategoryIcon.setImageResource(mCategory.getIcon());
                        mCategoryName.setText(mCategory.getName());
                    }
                    mDateTv.setText(mDateFormat.format(mExpenseDate.getTime()));
                    mDescEt.setText(item.getDesc());
                }
                break;
            case SAVE_DATA:
                if (success) {
                    if (mExpenseId > 0) {
                        EventBus.getDefault().post(new EventRecordUpdate(model.getExpenseItem()));
                    } else {
                        EventBus.getDefault().post(new EventRecordAdd(model.getExpenseItem()));
                    }
                    finish();
                }
                break;
        }
    }

    @Override
    public Uri getDataUri(ExpenseEditModel.EditQueryEnum query) {
        return null;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void addListener(UserActionListener listener) {
        mUserActionListener = listener;
    }
}
