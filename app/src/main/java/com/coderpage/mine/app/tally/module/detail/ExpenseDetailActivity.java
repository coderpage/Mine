package com.coderpage.mine.app.tally.module.detail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.coderpage.base.common.IError;
import com.coderpage.base.utils.UIUtils;
import com.coderpage.framework.Presenter;
import com.coderpage.framework.PresenterImpl;
import com.coderpage.framework.UpdatableView;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.common.error.ErrorUtils;
import com.coderpage.mine.app.tally.data.Expense;
import com.coderpage.mine.app.tally.edit.ExpenseEditActivity;
import com.coderpage.mine.app.tally.eventbus.EventRecordDelete;
import com.coderpage.mine.app.tally.eventbus.EventRecordUpdate;
import com.coderpage.mine.ui.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.coderpage.base.utils.LogUtils.makeLogTag;
import static com.coderpage.mine.app.tally.module.detail.ExpenseDetailModel.Queries;
import static com.coderpage.mine.app.tally.module.detail.ExpenseDetailModel.UserActions;

/**
 * @author abner-l. 2017-09-17
 *
 *         展示消费记录详细数据
 */
public class ExpenseDetailActivity extends BaseActivity
        implements UpdatableView<ExpenseDetailModel, Queries, UserActions, IError> {

    private static final String TAG = makeLogTag(ExpenseDetailActivity.class);
    private static final String EXTRA_EXPENSE_ID = "extra_expense_id";

    private AppCompatImageView mCategoryIconIv;
    private TextView mCategoryNameTv;
    private TextView mExpenseAmountTv;
    private TextView mNoteTv;
    private TextView mTimeTv;

    // 标识数据是否被修改了，如果被修改了则不使用转场动画回退，
    // 由于修改数据后 recycler view 视图发生变化，会引起崩溃问题
    private boolean mDataModified = false;
    private long mExpenseId;
    private Presenter<Queries, UserActions> mPresenter;
    private UserActionListener<UserActions> mUserActionListener;

    public static void open(Context context, long expenseId, View sharedRoot) {
        Intent intent = new Intent(context, ExpenseDetailActivity.class);
        intent.putExtra(EXTRA_EXPENSE_ID, expenseId);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return;
        }

        if (sharedRoot == null) {
            context.startActivity(intent);
            return;
        }

        Activity activity = (Activity) context;
        if (Build.VERSION.SDK_INT >= 16) {
            activity.startActivity(intent,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                            activity, sharedRoot, "sharedRoot").toBundle());
        } else {
            activity.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tally_expense_detail);

        initData();
        initToolbar();
        initView();
        initPresenter();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tally_record_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_trash:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_title_delete_confirm)
                        .setNegativeButton(R.string.dialog_btn_cancel, null)
                        .setPositiveButton(R.string.dialog_btn_delete, (dialog, which) -> {
                            mUserActionListener.onUserAction(UserActions.DELETE, null);
                        }).show();
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        if (mDataModified) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private void initData() {
        Intent intent = getIntent();
        mExpenseId = intent.getLongExtra(EXTRA_EXPENSE_ID, 0);
    }

    private void initToolbar() {
        setToolbarAsClose(v -> onBackPressed());
    }

    private void initView() {
        mCategoryIconIv = (AppCompatImageView) findViewById(R.id.ivCategoryIcon);
        mCategoryNameTv = (TextView) findViewById(R.id.tvCategoryName);
        mExpenseAmountTv = (TextView) findViewById(R.id.tvExpenseAmount);
        mNoteTv = (TextView) findViewById(R.id.tvNote);
        mTimeTv = (TextView) findViewById(R.id.tvTime);
        findViewById(R.id.btnModify).setOnClickListener(mOnClickListener);
    }

    private void initPresenter() {
        ExpenseDetailModel model = new ExpenseDetailModel(this, mExpenseId);
        mPresenter = new PresenterImpl<>(model, this, UserActions.values(), Queries.values());
        mPresenter.loadInitialQueries();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventModifyFinished(EventRecordUpdate event) {
        mDataModified = true;
        mUserActionListener.onUserAction(UserActions.MODIFY_FINISH, null);
    }

    @Override
    public void displayData(ExpenseDetailModel model, Queries query) {
        switch (query) {

            case DATA_INIT:
                refreshView(model);
                break;
        }
    }

    @Override
    public void displayErrorMessage(Queries query, IError error) {
        switch (query) {

            case DATA_INIT:
                UIUtils.showToastLong(this, ErrorUtils.formatDisplayMsg(error));
                break;
        }
    }

    @Override
    public void displayUserActionResult(ExpenseDetailModel model,
                                        Bundle args,
                                        UserActions userAction,
                                        boolean success,
                                        IError error) {
        switch (userAction) {

            case DELETE:
                if (success) {
                    UIUtils.showToastShort(this, R.string.tally_toast_delete_success);
                    EventBus.getDefault().post(new EventRecordDelete(model.getExpense()));
                    finish();
                } else {
                    UIUtils.showToastShort(this, ErrorUtils.formatDisplayMsg(error));
                }
                break;

            case MODIFY_FINISH:
                if (success) {
                    refreshView(model);
                } else {
                    UIUtils.showToastShort(this, ErrorUtils.formatDisplayMsg(error));
                }
                break;
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public Uri getDataUri(Queries query) {
        return null;
    }

    @Override
    public void addListener(UserActionListener<UserActions> listener) {
        mUserActionListener = listener;
    }

    private void refreshView(ExpenseDetailModel model) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault());

        Expense expense = model.getExpense();
        mCategoryIconIv.setImageResource(expense.getCategoryIconResId());
        mCategoryNameTv.setText(expense.getCategoryName());
        mExpenseAmountTv.setText(decimalFormat.format(expense.getAmount()));
        mNoteTv.setText(expense.getDesc());
        mTimeTv.setText(dateFormat.format(new Date(expense.getTime())));
    }

    private View.OnClickListener mOnClickListener = (view) -> {
        int id = view.getId();
        switch (id) {
            case R.id.btnModify:
                ExpenseEditActivity.open(ExpenseDetailActivity.this, mExpenseId);
                break;
        }
    };
}
