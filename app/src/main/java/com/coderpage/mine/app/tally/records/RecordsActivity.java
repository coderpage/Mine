package com.coderpage.mine.app.tally.records;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.coderpage.framework.UpdatableView;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.data.ExpenseItem;
import com.coderpage.mine.app.tally.eventbus.EventRecordUpdate;
import com.coderpage.mine.app.tally.main.MainModel;
import com.coderpage.mine.app.tally.ui.widget.LoadMoreRecyclerView;
import com.coderpage.mine.ui.BaseActivity;
import com.coderpage.mine.ui.widget.DrawShadowFrameLayout;
import com.coderpage.mine.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author abner-l. 2017-05-12
 */

public class RecordsActivity extends BaseActivity implements UpdatableView<RecordsModel,
        RecordsModel.RecordsQueryEnum, RecordsModel.RecordsUserActionEnum> {

    private UserActionListener mUserActionListener;
    private RecordsPresenter mPresenter;

    private LoadMoreRecyclerView mHistoryRecordsRecycler;
    private HistoryRecordsAdapter mHistoryRecordsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tall_records);
        initView();
        initPresenter();
        EventBus.getDefault().register(this);
    }

    private void initView() {
        mHistoryRecordsRecycler = ((LoadMoreRecyclerView) findViewById(R.id.recyclerRecord));
        mHistoryRecordsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mHistoryRecordsAdapter = new HistoryRecordsAdapter(this);
        mHistoryRecordsRecycler.setAdapter(mHistoryRecordsAdapter);
        mHistoryRecordsRecycler.setPullActionListener(new LoadMoreRecyclerView.PullActionListener() {

            @Override
            public void onPullUpLoadMore() {
                ExpenseItem lastExpenseShow = mHistoryRecordsAdapter.getLastExpenseShow();
                long loadMoreStartDate = System.currentTimeMillis();
                if (lastExpenseShow != null) {
                    loadMoreStartDate = lastExpenseShow.getTime();
                }
                Bundle args = new Bundle(1);
                args.putLong(RecordsModel.EXTRA_LOAD_MORE_START_DATE, loadMoreStartDate);
                mUserActionListener.onUserAction(RecordsModel.RecordsUserActionEnum.LOAD_MORE, args);
            }
        });
    }

    private void initPresenter() {
        mPresenter = new RecordsPresenter(new RecordsModel(this),
                this,
                RecordsModel.RecordsUserActionEnum.values(),
                RecordsModel.RecordsQueryEnum.values());
        mPresenter.loadInitialQueries();
        mHistoryRecordsAdapter.setUserActionListener(mUserActionListener);
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
        args.putLong(RecordsModel.EXTRA_EXPENSE_ID, event.getExpenseItem().getId());
        mUserActionListener.onUserAction(RecordsModel.RecordsUserActionEnum.EXPENSE_EDITED, args);
    }

    @Override
    public void displayData(RecordsModel model, RecordsModel.RecordsQueryEnum query) {
        switch (query) {
            case INIT_DATA:
                mHistoryRecordsAdapter.refreshData(model.getInitExpenseList());
                break;
        }
    }

    @Override
    public void displayUserActionResult(RecordsModel model,
                                        Bundle args,
                                        RecordsModel.RecordsUserActionEnum userAction,
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
                    long deletedId = args.getLong(RecordsModel.EXTRA_EXPENSE_ID);
                    mHistoryRecordsAdapter.removeItem(deletedId);
                    mUserActionListener.onUserAction(MainModel.MainUserActionEnum.RELOAD_MONTH_TOTAL, null);
                }
                break;
            case LOAD_MORE:
                if (success) {
                    mHistoryRecordsAdapter.addHistoryItems(model.getLoadMoreExpenseList());
                }
                mPresenter.setOnLoadMore(false);
                break;
        }
    }

    @Override
    public void displayErrorMessage(RecordsModel.RecordsQueryEnum query) {

    }

    @Override
    public Uri getDataUri(RecordsModel.RecordsQueryEnum query) {
        return null;
    }

    @Override
    public Context getContext() {
        return RecordsActivity.this;
    }

    @Override
    public void addListener(UserActionListener listener) {
        mUserActionListener = listener;
    }
}
