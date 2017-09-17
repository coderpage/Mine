package com.coderpage.mine.app.tally.search;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.coderpage.common.Callback;
import com.coderpage.common.IError;
import com.coderpage.framework.Model;
import com.coderpage.framework.QueryEnum;
import com.coderpage.framework.UserActionEnum;
import com.coderpage.mine.app.tally.data.DataHelper;
import com.coderpage.mine.app.tally.data.Expense;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lc. 2017-09-17
 * @since 0.5.0
 */

class SearchModel implements Model<
        SearchModel.Queries, SearchModel.UserActions, SearchModel, IError> {

    static final String EXTRA_KEYWORD = "extra_keyword";

    private Context mContext;
    private SearchModel self;

    private List<Expense> mResults = new ArrayList<>();

    SearchModel(Context context) {
        mContext = context;
        self = this;
    }

    public List<Expense> getResults() {
        return mResults;
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
        }
    }

    @Override
    public void cleanUp() {

    }

    enum Queries implements QueryEnum {
        ;
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
        SEARCH(1);

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
