package com.coderpage.mine.app.tally.module.records;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.coderpage.framework.PresenterImpl;
import com.coderpage.framework.UserActionEnum;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author abner-l. 2017-05-12
 * @since 0.1.0
 */

public class RecordsPresenter extends PresenterImpl {
    private AtomicBoolean mIsOnLoadMore = new AtomicBoolean(false);

    public RecordsPresenter(RecordsModel model, RecordsActivityOld view,
                            RecordsModel.RecordsUserActionEnum[] validUserActions,
                            RecordsModel.RecordsQueryEnum[] initialQueries) {
        super(model, view, validUserActions, initialQueries);
    }

    @Override
    public void onUserAction(UserActionEnum action, @Nullable Bundle args) {
        if (isUserActionValid(action)) {
            RecordsModel.RecordsUserActionEnum actionEnum = (RecordsModel.RecordsUserActionEnum) action;
            switch (actionEnum) {
                case LOAD_MORE:
                    if (mIsOnLoadMore.getAndSet(true)) {
                        return;
                    }
                    break;
            }
        }
        super.onUserAction(action, args);
    }

    /**
     * 设置是否正在加载更多；
     * 如果设置为正在加载更多，将会拦截"加载更多"的请求。
     * 当一次加载更多的请求完成之后，应该调用本方法设置为 false，处理下一次的"加载更多请求"
     *
     * @param onLoadMore 是否正在加载更多
     */
    public void setOnLoadMore(boolean onLoadMore) {
        mIsOnLoadMore.set(onLoadMore);
    }
}
