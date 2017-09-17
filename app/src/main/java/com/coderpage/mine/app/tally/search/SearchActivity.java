package com.coderpage.mine.app.tally.search;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.EditText;

import com.coderpage.common.IError;
import com.coderpage.framework.Presenter;
import com.coderpage.framework.PresenterImpl;
import com.coderpage.framework.UpdatableView;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.common.error.ErrorUtils;
import com.coderpage.mine.ui.BaseActivity;
import com.coderpage.utils.UIUtils;

import static com.coderpage.mine.app.tally.search.SearchModel.Queries;
import static com.coderpage.mine.app.tally.search.SearchModel.UserActions;

public class SearchActivity extends BaseActivity
        implements UpdatableView<SearchModel, Queries, UserActions, IError> {

    private EditText mSearchEt;
    private RecyclerView mResultRecycler;
    private SearchResultAdapter mSearchResultAdapter;

    private Presenter<Queries, UserActions> mPresenter;
    private UserActionListener<UserActions> mUserActionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        overridePendingTransition(0, 0);

        initToolbar();
        initView();
        setupSearchView();
        initPresenter();
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0, 0);
        super.onPause();
    }

    private void initToolbar() {
        setToolbarAsBack(v -> onBackPressed());
    }

    private void initView() {
        mSearchEt = (EditText) findViewById(R.id.etSearch);
        mResultRecycler = (RecyclerView) findViewById(R.id.recyclerResult);
        mResultRecycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mSearchResultAdapter = new SearchResultAdapter(this);
        mResultRecycler.setAdapter(mSearchResultAdapter);
    }

    private void setupSearchView() {
        mSearchEt.setFocusable(true);
        mSearchEt.setFocusableInTouchMode(true);
        mSearchEt.requestFocus();
        showSoftKeyBoard();

        mSearchEt.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                String keyword = mSearchEt.getText().toString();
                if (TextUtils.isEmpty(keyword)) {
                } else {
                    Bundle args = new Bundle(1);
                    args.putString(SearchModel.EXTRA_KEYWORD, keyword);
                    mUserActionListener.onUserAction(UserActions.SEARCH, args);
                }
            }
            return false;
        });
    }

    private void showSoftKeyBoard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void hideSoftKeyBoard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void initPresenter() {
        SearchModel model = new SearchModel(this);
        mPresenter = new PresenterImpl<>(model, this, UserActions.values(), Queries.values());
        mPresenter.loadInitialQueries();
    }

    @Override
    public void displayData(SearchModel model, Queries query) {

    }

    @Override
    public void displayErrorMessage(Queries query, IError error) {

    }

    @Override
    public void displayUserActionResult(SearchModel model,
                                        Bundle args,
                                        UserActions userAction,
                                        boolean success,
                                        IError error) {
        switch (userAction) {

            case SEARCH:
                if (success) {
                    mSearchResultAdapter.refresh(model.getResults());
                } else {
                    UIUtils.showToastShort(SearchActivity.this, ErrorUtils.formatDisplayMsg(error));
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
}
