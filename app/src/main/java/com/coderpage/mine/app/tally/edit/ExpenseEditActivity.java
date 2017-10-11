package com.coderpage.mine.app.tally.edit;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.coderpage.base.common.IError;
import com.coderpage.base.utils.LogUtils;
import com.coderpage.framework.PresenterImpl;
import com.coderpage.framework.UpdatableView;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.data.Category;
import com.coderpage.mine.app.tally.eventbus.EventRecordAdd;
import com.coderpage.mine.app.tally.eventbus.EventRecordUpdate;
import com.coderpage.mine.app.tally.ui.widget.NumInputView;
import com.coderpage.mine.app.tally.utils.DatePickUtils;
import com.coderpage.mine.ui.BaseActivity;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.coderpage.mine.app.tally.edit.ExpenseEditModel.EXTRA_EXPENSE_AMOUNT;
import static com.coderpage.mine.app.tally.edit.ExpenseEditModel.EXTRA_EXPENSE_CATEGORY;
import static com.coderpage.mine.app.tally.edit.ExpenseEditModel.EXTRA_EXPENSE_CATEGORY_ICON_RES_ID;
import static com.coderpage.mine.app.tally.edit.ExpenseEditModel.EXTRA_EXPENSE_CATEGORY_ID;
import static com.coderpage.mine.app.tally.edit.ExpenseEditModel.EXTRA_EXPENSE_DESC;
import static com.coderpage.mine.app.tally.edit.ExpenseEditModel.EXTRA_EXPENSE_TIME;
import static com.coderpage.mine.app.tally.edit.ExpenseEditModel.EditUserActionEnum;
import static com.coderpage.mine.app.tally.edit.ExpenseEditModel.EditUserActionEnum.DATE_CHANGED;

public class ExpenseEditActivity extends BaseActivity
        implements UpdatableView<ExpenseEditModel,
        ExpenseEditModel.EditQueryEnum, ExpenseEditModel.EditUserActionEnum, IError> {

    private static final String TAG = LogUtils.makeLogTag(ExpenseEditActivity.class);
    public static final String EXTRA_RECORD_ID = "extra_record_id";

    TextView mAmountTv;
    TextView mCategoryName;
    TextView mDateTv;
    AppCompatImageView mCategoryIcon;
    GridView mCategoryGv;
    NumInputView mNumInputView;
    TextView mDescTv;

    private CategoryPickerAdapter mCategoryPickerAdapter;

    private Calendar mExpenseDate = Calendar.getInstance();
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
    private String mAmountFormat;
    private long mExpenseId = 0;

    private UserActionListener mUserActionListener;
    private PresenterImpl mPresenter;
    private ExpenseEditModel mModel;

    public static void open(Context context, long expenseId) {
        Intent intent = new Intent(context, ExpenseEditActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(EXTRA_RECORD_ID, expenseId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tally_expense_editor);
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
        mDescTv = (TextView) findViewById(R.id.tvDesc);
        mDescTv.setOnClickListener(mOnclickListener);

        mDateTv.setText(mDateFormat.format(mExpenseDate.getTime()));
        mDateTv.setOnClickListener(mOnclickListener);
    }

    private void initPresenter() {
        mModel = new ExpenseEditModel(getContext(), mExpenseId);
        mPresenter = new PresenterImpl<>(
                mModel,
                this, ExpenseEditModel.EditUserActionEnum.values(),
                ExpenseEditModel.EditQueryEnum.values());
        mPresenter.loadInitialQueries();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            mExpenseId = intent.getLongExtra(EXTRA_RECORD_ID, 0);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setToolbarAsClose((View v) -> finish());
    }

    @Override
    public void displayData(ExpenseEditModel model, ExpenseEditModel.EditQueryEnum query) {
        switch (query) {
            case LOAD_CATEGORY:
                mCategoryPickerAdapter.refreshData(model.getCategoryItemList());
                break;
            case LOAD_EXPENSE:
                refreshSelectedCategory(model);
                refreshDate(model);
                refreshAmountText(model);
                refreshDesc(model);
                break;
        }
    }

    @Override
    public void displayErrorMessage(ExpenseEditModel.EditQueryEnum query, IError error) {

    }

    @Override
    public void displayUserActionResult(ExpenseEditModel model,
                                        Bundle args,
                                        ExpenseEditModel.EditUserActionEnum userAction,
                                        boolean success,
                                        IError error) {
        switch (userAction) {
            case CATEGORY_CHANGED:
                if (success) {
                    refreshSelectedCategory(model);
                }
                break;
            case DATE_CHANGED:
                if (success) {
                    refreshDate(model);
                }
                break;
            case AMOUNT_CHANGED:
                if (success) {
                    refreshAmountText(model);
                }
                break;
            case DESC_CHANGED:
                if (success) {
                    refreshDesc(model);
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

    private void refreshAmountText(ExpenseEditModel model) {
        mAmountTv.setText(String.format(mAmountFormat, model.getExpenseItem().getAmount()));
    }

    private void refreshSelectedCategory(ExpenseEditModel model) {
        mCategoryIcon.setImageResource(model.getExpenseItem().getCategoryIconResId());
        mCategoryName.setText(model.getExpenseItem().getCategoryName());
    }

    private void refreshDesc(ExpenseEditModel model) {
        String desc = model.getExpenseItem().getDesc();
        if (TextUtils.isEmpty(desc)) {
            mDescTv.setText(R.string.tally_add_expense_note);
            return;
        }
        mDescTv.setText(desc);
    }

    private void refreshDate(ExpenseEditModel model) {
        mDateTv.setText(mDateFormat.format(model.getExpenseItem().getTime()));
    }

    AdapterView.OnItemClickListener mCategorySelectListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Category category = mCategoryPickerAdapter.getCategoryItems().get(position);

            Bundle bundle = new Bundle();
            bundle.putLong(EXTRA_EXPENSE_CATEGORY_ID, category.getId());
            bundle.putString(EXTRA_EXPENSE_CATEGORY, category.getName());
            bundle.putInt(EXTRA_EXPENSE_CATEGORY_ICON_RES_ID, category.getIcon());
            mUserActionListener.onUserAction(EditUserActionEnum.CATEGORY_CHANGED, bundle);
        }
    };

    View.OnClickListener mOnclickListener = (v) -> {
        int id = v.getId();
        switch (id) {
            case R.id.tvDate:
                showDatePicker();
                break;
            case R.id.tvDesc:
                showDescEditDialog();
                break;

        }
    };

    private NumInputView.InputListener mNumInputListener = new NumInputView.InputListener() {
        @Override
        public void onKeyClick(int code) {
            switch (code) {
                case KeyEvent.KEYCODE_ENTER:
                    Bundle args = new Bundle();
                    mUserActionListener.onUserAction(
                            ExpenseEditModel.EditUserActionEnum.SAVE_DATA, args);
                    break;
            }
        }

        @Override
        public void onNumChange(float newNum) {
            Bundle args = new Bundle();
            args.putFloat(EXTRA_EXPENSE_AMOUNT, newNum);
            mUserActionListener.onUserAction(EditUserActionEnum.AMOUNT_CHANGED, args);
        }
    };

    private void showDatePicker() {
        DatePickUtils.showDatePickDialog(ExpenseEditActivity.this,
                new DatePickUtils.OnDatePickListener() {
                    Calendar mCalendar;

                    @Override
                    public void onDatePick(DialogInterface dialog,
                                           int year,
                                           int month,
                                           int dayOfMonth) {
                        mCalendar = Calendar.getInstance();
                        mCalendar.set(year, month, dayOfMonth);
                    }

                    @Override
                    public void onConfirmClick(DialogInterface dialog) {
                        if (mCalendar == null) {
                            return;
                        }
                        Bundle bundle = new Bundle();
                        bundle.putLong(EXTRA_EXPENSE_TIME, mCalendar.getTimeInMillis());
                        mUserActionListener.onUserAction(
                                DATE_CHANGED, bundle);
                    }
                });
    }

    private void showDescEditDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_tally_expense_desc_edit, null);
        EditText editText = (EditText) view.findViewById(R.id.etText);
        String desc = mModel.getExpenseItem().getDesc();
        if (!TextUtils.isEmpty(desc)) {
            editText.setText(desc);
            editText.setSelection(desc.length());
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setTitle(R.string.tally_add_expense_note)
                .setView(view)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.confirm, (dialogInterface, which) -> {
                    Bundle bundle = new Bundle();
                    bundle.putString(EXTRA_EXPENSE_DESC, editText.getText().toString());
                    mUserActionListener.onUserAction(EditUserActionEnum.DESC_CHANGED, bundle);
                })
                .create();
        dialog.setCanceledOnTouchOutside(false);
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                dialog.getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });
        dialog.show();

        editText.setFocusable(true);
        editText.requestFocus();
    }
}
