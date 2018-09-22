package com.coderpage.mine.app.tally.module.search;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.coderpage.base.common.Callback;
import com.coderpage.base.common.IError;
import com.coderpage.base.utils.CommonUtils;
import com.coderpage.base.utils.LogUtils;
import com.coderpage.framework.Model;
import com.coderpage.framework.QueryEnum;
import com.coderpage.framework.UserActionEnum;
import com.coderpage.mine.app.tally.data.DataHelper;
import com.coderpage.mine.app.tally.data.Expense;
import com.coderpage.mine.utils.PreferencesUtils;

import java.util.ArrayList;
import java.util.List;

import static com.coderpage.base.utils.LogUtils.makeLogTag;

/**
 * @author lc. 2017-09-17
 * @since 0.5.0
 */

class SearchModel implements Model<
        SearchModel.Queries, SearchModel.UserActions, SearchModel, IError> {

    private static final String TAG = makeLogTag(SearchModel.class);

    static final String EXTRA_KEYWORD = "extra_keyword";

    private Context mContext;
    private SearchModel self;

    private List<String> mSearchHistory = new ArrayList<>();
    private List<Expense> mResults = new ArrayList<>();

    SearchModel(Context context) {
        mContext = context;
        self = this;
    }

    List<Expense> getResults() {
        return mResults;
    }

    List<String> getSearchHistory() {
        return mSearchHistory;
    }

    @Override
    public Queries[] getQueries() {
        return Queries.values();
    }

    @Override
    public UserActions[] getUserActions() {
        return UserActions.values();
    }

    @Override
    public void requestData(
            Queries query,
            DataQueryCallback<SearchModel, Queries, IError> callback) {
        switch (query) {

            case LOAD_SEARCH_HISTORY:
                // 读取搜索记录
                List<String> searchHistory = PreferencesUtils.getSearchHistory(mContext);
                mSearchHistory.clear();
                mSearchHistory.addAll(searchHistory);
                callback.onModelUpdated(self, query);
                LogUtils.LOGI(TAG, "load search history size=" + searchHistory.size());
                break;

        }
    }

    @Override
    public void deliverUserAction(
            UserActions action,
            @Nullable Bundle args,
            UserActionCallback<SearchModel, UserActions, IError> callback) {

        switch (action) {

            case SEARCH:
                if (args == null || !args.containsKey(EXTRA_KEYWORD)) {
                    throw new IllegalArgumentException("miss param " + EXTRA_KEYWORD);
                }
                String keyword = args.getString(EXTRA_KEYWORD);
                DataHelper.searchByKeywordAsync(mContext, keyword, new Callback<List<Expense>, IError>() {
                    @Override
                    public void success(List<Expense> expenses) {
                        mResults = expenses;
                        callback.onModelUpdated(self, action);
                    }

                    @Override
                    public void failure(IError error) {
                        callback.onError(action, error);
                    }
                });
                break;

            case SEARCH_HISTORY_ADD:
                if (args == null || !args.containsKey(EXTRA_KEYWORD)) {
                    throw new IllegalArgumentException("miss param " + EXTRA_KEYWORD);
                }
                keyword = args.getString(EXTRA_KEYWORD);
                if (!CommonUtils.collectionContains(mSearchHistory, keyword)) {
                    mSearchHistory.add(0, keyword);
                    PreferencesUtils.setSearchHistory(mContext, mSearchHistory);
                    callback.onModelUpdated(self, action);
                }
                break;

            case SEARCH_HISTORY_REMOVE:
                if (args == null || !args.containsKey(EXTRA_KEYWORD)) {
                    throw new IllegalArgumentException("miss param " + EXTRA_KEYWORD);
                }
                keyword = args.getString(EXTRA_KEYWORD);
                if (CommonUtils.collectionContains(mSearchHistory, keyword)) {
                    CommonUtils.collectionRemoveElememt(mSearchHistory, keyword);
                    PreferencesUtils.setSearchHistory(mContext, mSearchHistory);
                    callback.onModelUpdated(self, action);
                }
                break;

            case SEARCH_HISTORY_CLEAR:
                mSearchHistory.clear();
                PreferencesUtils.setSearchHistory(mContext, mSearchHistory);
                callback.onModelUpdated(self, action);
                break;
        }
    }

    @Override
    public void cleanUp() {

    }

    enum Queries implements QueryEnum {
        LOAD_SEARCH_HISTORY(1, null);
        private int id;
        private String[] projection;

        @Override
        public int getId() {
            return id;
        }

        @Override
        public String[] getProjection() {
            return projection;
        }

        Queries(int id, String[] projection) {
            this.id = id;
            this.projection = projection;
        }
    }

    enum UserActions implements UserActionEnum {
        SEARCH(1),
        SEARCH_HISTORY_ADD(2),
        SEARCH_HISTORY_REMOVE(3),
        SEARCH_HISTORY_CLEAR(4);

        private int id;

        @Override
        public int getId() {
            return id;
        }

        UserActions(int id) {
            this.id = id;
        }
    }

}
