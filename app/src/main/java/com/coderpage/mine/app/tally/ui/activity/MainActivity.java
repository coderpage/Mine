package com.coderpage.mine.app.tally.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.coderpage.framework.UpdatableView;
import com.coderpage.framework.utils.LogUtils;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.data.ExpenseItem;
import com.coderpage.mine.app.tally.eventbus.EventRecordAdd;
import com.coderpage.mine.app.tally.eventbus.EventRecordUpdate;
import com.coderpage.mine.app.tally.ui.widget.LoadMoreRecyclerView;
import com.coderpage.mine.ui.BaseActivity;
import com.coderpage.mine.ui.widget.DrawShadowFrameLayout;
import com.coderpage.mine.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.coderpage.framework.utils.LogUtils.LOGI;

/**
 * @author abner-l. 2017-01-23
 * @since 0.1.0
 */

public class MainActivity extends BaseActivity
        implements UpdatableView<MainModel, MainModel.MainQueryEnum,
        MainModel.MainUserActionEnum> {

    private static final String TAG = LogUtils.makeLogTag(MainActivity.class);

    DecimalFormat mAmountDecimalFormat = new DecimalFormat(".00");
    String mAmountFormat;
    MainPresenter mPresenter;
    UserActionListener mUserActionListener;

    LoadMoreRecyclerView mHistoryRecordsRecycler;
    TextView mSumOfMonthAmountTv;
    MainHistoryExpenseAdapter mAllExpenseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tally);
        setTitle(R.string.tally_toolbar_title_main);
        mAmountFormat = getString(R.string.tally_amount_cny);
        initView();
        initPresenter();
        EventBus.getDefault().register(this);
    }

    private void initView() {
        mHistoryRecordsRecycler = ((LoadMoreRecyclerView) findViewById(R.id.recyclerHistoryRecord));
        mHistoryRecordsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAllExpenseAdapter = new MainHistoryExpenseAdapter(this);
        mHistoryRecordsRecycler.setAdapter(mAllExpenseAdapter);
        mHistoryRecordsRecycler.setPullActionListener(new LoadMoreRecyclerView.PullActionListener() {

            @Override
            public void onPullUpLoadMore() {
                ArrayList<ExpenseItem> dataList = mAllExpenseAdapter.getDataList();
                long loadMoreStartDate = System.currentTimeMillis();
                if (!dataList.isEmpty()) {
                    loadMoreStartDate = dataList.get(dataList.size() - 1).getTime();
                }
                Bundle args = new Bundle(1);
                args.putLong(MainModel.EXTRA_LOAD_MORE_START_DATE, loadMoreStartDate);
                mUserActionListener.onUserAction(MainModel.MainUserActionEnum.LOAD_MORE, args);
            }
        });
        mSumOfMonthAmountTv = ((TextView) findViewById(R.id.tvMonthAmount));

        findViewById(R.id.btnAddRecord).setOnClickListener(mOnClickListener);
    }

    private void initPresenter() {
        mPresenter = new MainPresenter(
                new MainModel(this),
                this,
                MainModel.MainUserActionEnum.values(),
                MainModel.MainQueryEnum.values());
        mPresenter.loadInitialQueries();
        mAllExpenseAdapter.setUserActionListener(mUserActionListener);
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

    private View.OnClickListener mOnClickListener = (View v) -> {
        int id = v.getId();
        switch (id) {
            case R.id.btnAddRecord:
                Intent intent = new Intent(MainActivity.this, ExpenseEditActivity.class);
                startActivity(intent);
                break;
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRecordAdd(EventRecordAdd event) {
        Bundle args = new Bundle(1);
        args.putLong(MainModel.EXTRA_EXPENSE_ID, event.getExpenseItem().getId());
        mUserActionListener.onUserAction(MainModel.MainUserActionEnum.NEW_EXPENSE_CREATED, args);
        mUserActionListener.onUserAction(MainModel.MainUserActionEnum.RELOAD_MONTH_TOTAL, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRecordUpdate(EventRecordUpdate event) {
        Bundle args = new Bundle(1);
        args.putLong(MainModel.EXTRA_EXPENSE_ID, event.getExpenseItem().getId());
        mUserActionListener.onUserAction(MainModel.MainUserActionEnum.EXPENSE_EDITED, args);
        mUserActionListener.onUserAction(MainModel.MainUserActionEnum.RELOAD_MONTH_TOTAL, null);
    }

    @Override
    public void displayData(MainModel model, MainModel.MainQueryEnum query) {
        switch (query) {
            case MONTH_TOTAL:
                String format = String.format(
                        mAmountFormat, mAmountDecimalFormat.format(model.getMonthTotal()));
                mSumOfMonthAmountTv.setText(format);
                break;
            case EXPENSE_INIT:
                mAllExpenseAdapter.refreshData(model.getInitExpenseItemList());
                break;
        }
    }

    @Override
    public void displayErrorMessage(MainModel.MainQueryEnum query) {

    }

    @Override
    public void displayUserActionResult(MainModel model,
                                        Bundle args, MainModel.MainUserActionEnum userAction,
                                        boolean success) {
        LOGI(TAG, "displayUserActionResult-> action=" + userAction.getId());
        switch (userAction) {
            case NEW_EXPENSE_CREATED:
                if (success) {
                    ExpenseItem newAddExpenseItem = model.getNewAddExpenseItem();
                    mAllExpenseAdapter.addNewItem(newAddExpenseItem);
                }
                break;
            case RELOAD_MONTH_TOTAL:
                if (success) {
                    String format = String.format(
                            mAmountFormat, mAmountDecimalFormat.format(model.getMonthTotal()));
                    mSumOfMonthAmountTv.setText(format);
                }
                break;
            case EXPENSE_EDITED:
                if (success) {
                    ExpenseItem editedExpenseItem = model.getEditedExpenseItem();
                    mAllExpenseAdapter.refreshItem(editedExpenseItem);
                }
                break;
            case EXPENSE_DELETE:
                if (success) {
                    long deletedId = args.getLong(MainModel.EXTRA_EXPENSE_ID);
                    mAllExpenseAdapter.removeItem(deletedId);
                    mUserActionListener.onUserAction(MainModel.MainUserActionEnum.RELOAD_MONTH_TOTAL, null);
                }
                break;
            case LOAD_MORE:
                if (success) {
                    mAllExpenseAdapter.addHistoryItems(model.getLoadMoreExpenseItemList());
                }
                mPresenter.setOnLoadMore(false);
                break;
        }
    }

    @Override
    public Uri getDataUri(MainModel.MainQueryEnum query) {
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