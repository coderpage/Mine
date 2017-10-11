package com.coderpage.mine.app.tally.search;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.coderpage.base.common.IError;
import com.coderpage.framework.Presenter;
import com.coderpage.framework.PresenterImpl;
import com.coderpage.framework.UpdatableView;
import com.coderpage.mine.R;
import com.coderpage.mine.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import static com.coderpage.mine.app.tally.search.SearchModel.Queries;
import static com.coderpage.mine.app.tally.search.SearchModel.UserActions;

public class SearchActivity extends BaseActivity
        implements UpdatableView<SearchModel, Queries, UserActions, IError> {

    private EditText mSearchEt;
    private SearchHistoryView mSearchHistoryView;
    private SearchResultView mSearchResultView;

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
        setToolbarAsBack(v -> {
            if (mSearchResultView.getVisibility() == View.VISIBLE) {
                mSearchResultView.setVisibility(View.INVISIBLE);
                mSearchHistoryView.setVisibility(View.VISIBLE);
            } else {
                onBackPressed();
            }
        });
    }

    private void initView() {
        mSearchEt = (EditText) findViewById(R.id.etSearch);
        mSearchHistoryView = (SearchHistoryView) findViewById(R.id.lySearchHistoryView);
        mSearchResultView = (SearchResultView) findViewById(R.id.lySearchResultView);
    }

    private void setupSearchView() {
        mSearchEt.setFocusable(true);
        mSearchEt.setFocusableInTouchMode(true);
        mSearchEt.requestFocus();
        showSoftKeyBoard();

        mSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString();

                // 搜索框内容删除，显示搜索历史记录
                if (TextUtils.isEmpty(keyword) && mSearchHistoryView.getVisibility() != View.VISIBLE) {
                    mSearchHistoryView.setVisibility(View.VISIBLE);
                    mSearchResultView.setVisibility(View.INVISIBLE);
                }

            }
        });
        mSearchEt.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                String keyword = mSearchEt.getText().toString();
                if (!TextUtils.isEmpty(keyword)) {
                    Bundle args = new Bundle(1);
                    args.putString(SearchModel.EXTRA_KEYWORD, keyword);
                    mUserActionListener.onUserAction(UserActions.SEARCH, args);
                    mUserActionListener.onUserAction(UserActions.SEARCH_HISTORY_ADD, args);
                }
                hideSoftKeyBoard();
            }
            return false;
        });
    }

    private void showSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInputFromInputMethod(mSearchEt.getWindowToken(), 0);
    }

    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchEt.getWindowToken(), 0);
    }

    private void initPresenter() {
        SearchModel model = new SearchModel(this);
        List<UpdatableView<SearchModel, Queries, UserActions, IError>> viewList = new ArrayList<>(3);
        viewList.add(this);
        viewList.add(mSearchHistoryView);
        viewList.add(mSearchResultView);
        mPresenter = new PresenterImpl<>(model, viewList, UserActions.values(), Queries.values());
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
                String keyword = args.getString(SearchModel.EXTRA_KEYWORD, "");
                mSearchEt.setText(keyword);
                mSearchEt.setSelection(keyword.length());
                hideSoftKeyBoard();
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
