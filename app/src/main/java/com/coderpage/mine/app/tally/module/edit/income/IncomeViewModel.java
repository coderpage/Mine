package com.coderpage.mine.app.tally.module.edit.income;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.DialogInterface;
import android.databinding.ObservableField;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.coderpage.base.utils.ArrayUtils;
import com.coderpage.base.utils.CommonUtils;
import com.coderpage.framework.ViewReliedTask;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.eventbus.EventIncomeAdd;
import com.coderpage.mine.app.tally.eventbus.EventIncomeUpdate;
import com.coderpage.mine.app.tally.module.edit.model.Category;
import com.coderpage.mine.app.tally.persistence.model.CategoryModel;
import com.coderpage.mine.app.tally.persistence.model.Income;
import com.coderpage.mine.app.tally.ui.widget.NumInputView;
import com.coderpage.mine.app.tally.utils.DatePickUtils;
import com.coderpage.mine.utils.AndroidUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author lc. 2018-09-18 23:12
 * @since 0.6.0
 */

public class IncomeViewModel extends AndroidViewModel {

    private DecimalFormat mAmountFormat = new DecimalFormat("0.00");
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

    private long mIncomeId;
    private long mDate;
    private double mAmount;
    private Income mIncome;
    private IncomeRepository mRepository;

    /** 金额 */
    private ObservableField<String> mAmountText = new ObservableField<>("");
    /** 说明信息 */
    private ObservableField<String> mDesc = new ObservableField<>("");
    /** 时间 */
    private ObservableField<String> mDateText = new ObservableField<>("");
    /** 当前选择的分类 */
    private MutableLiveData<Category> mCurrentSelectCategory = new MutableLiveData<>();
    /** 支付分类列表 */
    private MutableLiveData<List<Category>> mCategoryList = new MutableLiveData<>();

    private MutableLiveData<ViewReliedTask<Activity>> mActivityRelayTask = new MutableLiveData<>();

    public IncomeViewModel(Application application) {
        super(application);
        mRepository = new IncomeRepository();
        mAmountText.set("0.00");
        mDate = System.currentTimeMillis();
        mDateText.set(mDateFormat.format(new Date(mDate)));
        initData();
    }

    public ObservableField<String> getAmountText() {
        return mAmountText;
    }

    public ObservableField<String> getDesc() {
        return mDesc;
    }

    public ObservableField<String> getDateText() {
        return mDateText;
    }

    LiveData<Category> getCurrentSelectCategory() {
        return mCurrentSelectCategory;
    }

    LiveData<List<Category>> getCategoryList() {
        return mCategoryList;
    }

    LiveData<ViewReliedTask<Activity>> getActivityRelayTask() {
        return mActivityRelayTask;
    }

    /** 消费分类点击 */
    public void onCategoryClick(Category category) {
        makeCategorySelect(mCategoryList.getValue(), category.getInternal().getUniqueName());
    }

    /** 消费说明点击 */
    public void onDescClick(Activity activity) {
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_tally_record_desc_edit, null);
        EditText editText = view.findViewById(R.id.etText);
        String desc = mDesc.get();
        if (!TextUtils.isEmpty(desc)) {
            editText.setText(desc);
            editText.setSelection(desc.length());
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        AlertDialog dialog = builder.setTitle(R.string.tally_add_record_note)
                .setView(view)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.confirm, (dialogInterface, which) -> {
                    mDesc.set(editText.getText().toString());
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

    /** 消费记录时间点击 */
    public void onDateClick(Activity activity) {
        DatePickUtils.showDatePickDialog(activity,
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
                        mDate = mCalendar.getTimeInMillis();
                        mDateText.set(mDateFormat.format(new Date(mDate)));
                    }
                });
    }

    /** 记账键盘数据监听 */
    NumInputView.InputListener getInputListener() {
        return new NumInputView.InputListener() {
            @Override
            public void onKeyClick(int code) {
                if (code == KeyEvent.KEYCODE_ENTER) {
                    saveData();
                }
            }

            @Override
            public void onNumChange(float newNum) {
                mAmount = newNum;
                mAmountText.set(mAmountFormat.format(mAmount));
            }
        };
    }

    void setIncomeId(long incomeId) {
        if (incomeId <= 0) {
            return;
        }
        mIncomeId = incomeId;
        mRepository.queryIncomeById(mIncomeId, expense -> {
            if (expense == null) {
                return;
            }
            mIncome = expense;
            mAmount = mIncome.getAmount();
            mDate = mIncome.getTime();
            mDesc.set(mIncome.getDesc());
            mDateText.set(mDateFormat.format(new Date(mDate)));
            mAmountText.set(mAmountFormat.format(mAmount));

            String  categoryUniqueName = mIncome.getCategoryUniqueName();
            Category category = ArrayUtils.query(mCategoryList.getValue(),
                    c -> CommonUtils.isEqual(c.getInternal().getUniqueName(), categoryUniqueName));
            if (category != null) {
                mCurrentSelectCategory.setValue(category);
            }
        });
    }

    private void initData() {
        mRepository.queryAllCategory(categoryList -> {

            if (categoryList == null || categoryList.isEmpty()) {
                return;
            }
            List<Category> displayList = new ArrayList<>(categoryList.size());
            for (CategoryModel category : categoryList) {
                displayList.add(new Category(category));
            }
            mCategoryList.setValue(displayList);

            if (mIncome == null) {
                Category select = displayList.get(0);
                makeCategorySelect(displayList, select.getInternal().getUniqueName());
            } else {
                makeCategorySelect(displayList, mIncome.getCategoryUniqueName());
            }
        });
    }

    /** 保存数据 */
    private void saveData() {
        Category category = mCurrentSelectCategory.getValue();
        if (category == null) {
            Toast.makeText(getApplication(), "no category", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isNewRecord = mIncome == null;
        Income income;
        if (mIncome != null) {
            income = mIncome;
        } else {
            income = new Income();
            income.setSyncId(AndroidUtils.generateUUID());
        }
        income.setAmount(mAmount);
        income.setTime(System.currentTimeMillis());
        income.setDesc(TextUtils.isEmpty(mDesc.get()) ? "" : mDesc.get());
        income.setCategoryId(category.getInternal().getId());
        income.setCategoryIcon(category.getInternal().getIcon());
        income.setCategoryName(category.getInternal().getName());
        income.setCategoryUniqueName(category.getInternal().getUniqueName());

        if (isNewRecord) {
            mRepository.saveIncome(income, result -> {
                if (result.isOk()) {
                    income.setId(result.data());
                    EventBus.getDefault().post(new EventIncomeAdd(income));
                    mActivityRelayTask.setValue(Activity::finish);
                }
            });
        } else {
            mRepository.updateIncome(income, result -> {
                if (result.isOk()) {
                    EventBus.getDefault().post(new EventIncomeUpdate(income));
                    mActivityRelayTask.setValue(Activity::finish);
                }
            });
        }
    }

    private void makeCategorySelect(List<Category> list, String categoryUniqueName) {
        if (list == null) {
            return;
        }
        Category select = null;
        for (Category category : list) {
            category.setSelect(CommonUtils.isEqual(category.getInternal().getUniqueName(), categoryUniqueName));
            if (CommonUtils.isEqual(category.getInternal().getUniqueName(), categoryUniqueName)) {
                select = category;
            }
        }
        if (select != null) {
            select.setSelect(true);
            mCurrentSelectCategory.setValue(select);
        }
    }

}
