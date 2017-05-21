package com.coderpage.mine.app.tally.records;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.coderpage.framework.Presenter;
import com.coderpage.framework.PresenterImpl;
import com.coderpage.framework.UpdatableView;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.data.ExpenseItem;
import com.coderpage.mine.app.tally.eventbus.EventRecordUpdate;
import com.coderpage.mine.app.tally.main.MainModel;
import com.coderpage.mine.ui.BaseActivity;
import com.coderpage.mine.ui.widget.DrawShadowFrameLayout;
import com.coderpage.mine.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author abner-l. 2017-05-15
 */

public class CategoryRecordsActivity extends BaseActivity implements UpdatableView<CategoryRecordsModel,
        CategoryRecordsModel.RecordsQueryEnum, CategoryRecordsModel.RecordsUserActionEnum> {

    private static final String EXTRA_YEAR = "extra_year";
    private static final String EXTRA_MONTH = "extra_month";
    private static final String EXTRA_CATEGORY_ID = "extra_category_id";

    private UserActionListener mUserActionListener;
    private Presenter mPresenter;
    private int mYear;
    private int mMonth;
    private long mCategoryId;

    private RecyclerView mHistoryRecordsRecycler;
    private SimpleRecorAdapter mHistoryRecordsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_records);
        initData();
        initView();
        initPresenter();
        EventBus.getDefault().register(this);
    }

    private void initData() {
        Intent intent = getIntent();
        mYear = intent.getIntExtra(EXTRA_YEAR, 0);
        mMonth = intent.getIntExtra(EXTRA_MONTH, 0);
        mCategoryId = intent.getLongExtra(EXTRA_CATEGORY_ID, 0);
    }

    private void initView() {
        mHistoryRecordsRecycler = ((RecyclerView) findViewById(R.id.recyclerRecord));
        mHistoryRecordsRecycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mHistoryRecordsAdapter = new SimpleRecorAdapter(this);
        mHistoryRecordsRecycler.setAdapter(mHistoryRecordsAdapter);
    }

    private void initPresenter() {
        mPresenter = new PresenterImpl(new CategoryRecordsModel(this, mYear, mMonth, mCategoryId),
                this,
                CategoryRecordsModel.RecordsUserActionEnum.values(),
                CategoryRecordsModel.RecordsQueryEnum.values());
        mPresenter.loadInitialQueries();
        mHistoryRecordsAdapter.setUserActionListener(mUserActionListener);
    }

    public static void open(Activity activity, int year, int month, long categoryId) {
        Intent intent = new Intent(activity, CategoryRecordsActivity.class);
        intent.putExtra(EXTRA_YEAR, year);
        intent.putExtra(EXTRA_MONTH, month);
        intent.putExtra(EXTRA_CATEGORY_ID, categoryId);
        activity.startActivity(intent);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setToolbarAsBack(v -> finish());
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
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRecordUpdate(EventRecordUpdate event) {
        Bundle args = new Bundle(1);
        args.putLong(CategoryRecordsModel.EXTRA_EXPENSE_ID, event.getExpenseItem().getId());
        mUserActionListener.onUserAction(CategoryRecordsModel.RecordsUserActionEnum.EXPENSE_EDITED, args);
    }

    @Override
    public void displayData(CategoryRecordsModel model, CategoryRecordsModel.RecordsQueryEnum query) {
        switch (query) {
            case INIT_DATA:
                mHistoryRecordsAdapter.refreshData(model.getInitExpenseList());
                break;
        }
    }

    @Override
    public void displayUserActionResult(CategoryRecordsModel model,
                                        Bundle args,
                                        CategoryRecordsModel.RecordsUserActionEnum userAction,
                                        boolean success) {
        switch (userAction) {
            case EXPENSE_EDITED:
                if (success) {
                    ExpenseItem editedExpenseItem = model.getEditedExpenseItem();
                    mHistoryRecordsAdapter.refreshItem(editedExpenseItem);
                }
                break;
            case EXPENSE_DELETE:
                if (success) {
                    long deletedId = args.getLong(CategoryRecordsModel.EXTRA_EXPENSE_ID);
                    mHistoryRecordsAdapter.removeItem(deletedId);
                    mUserActionListener.onUserAction(MainModel.MainUserActionEnum.RELOAD_MONTH_TOTAL, null);
                }
                break;
        }
    }

    @Override
    public void displayErrorMessage(CategoryRecordsModel.RecordsQueryEnum query) {

    }

    @Override
    public Uri getDataUri(CategoryRecordsModel.RecordsQueryEnum query) {
        return null;
    }

    @Override
    public Context getContext() {
        return CategoryRecordsActivity.this;
    }

    @Override
    public void addListener(UserActionListener listener) {
        mUserActionListener = listener;
    }


}
