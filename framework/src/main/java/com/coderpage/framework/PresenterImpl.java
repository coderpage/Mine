package com.coderpage.framework;

import android.os.Bundle;
import android.support.annotation.Nullable;

import static com.coderpage.utils.LogUtils.LOGI;
import static com.coderpage.utils.LogUtils.makeLogTag;

/**
 * @author abner-l. 2017-04-13
 * @since 0.2.0
 */

public class PresenterImpl implements Presenter, UpdatableView.UserActionListener {
    private static final String TAG = makeLogTag(PresenterImpl.class);

    private UpdatableView[] mUpdatableViews;

    private Model mModel;

    private QueryEnum[] mInitialQueriesToLoad;

    private UserActionEnum[] mValidUserActions;

    public PresenterImpl(Model model, UpdatableView view, UserActionEnum[] validUserActions,
                         QueryEnum[] initialQueries) {
        this(model, new UpdatableView[]{view}, validUserActions, initialQueries);
    }

    public PresenterImpl(Model model, @Nullable UpdatableView[] views, UserActionEnum[] validUserActions,
                         QueryEnum[] initialQueries) {
        mModel = model;
        mValidUserActions = validUserActions;
        mInitialQueriesToLoad = initialQueries;
        mUpdatableViews = views;
        if (views == null) {
            return;
        }
        for (UpdatableView view : mUpdatableViews) {
            view.addListener(this);
        }
    }

    @Override
    public void loadInitialQueries() {
        // 如果不需要加载初始化数据，直接更新 view
        if (mInitialQueriesToLoad == null || mInitialQueriesToLoad.length == 0) {
            if (mUpdatableViews == null) {
                return;
            }
            for (UpdatableView view : mUpdatableViews) {
                view.displayData(mModel, null);
            }
            return;
        }
        // 加载初始化数据
        for (QueryEnum queryEnum : mInitialQueriesToLoad) {
            mModel.requestData(queryEnum, new Model.DataQueryCallback() {
                @Override
                public void onModelUpdated(Model model, QueryEnum query) {
                    if (mUpdatableViews == null) {
                        return;
                    }
                    for (UpdatableView view : mUpdatableViews) {
                        view.displayData(model, query);
                    }
                }

                @Override
                public void onError(QueryEnum query) {
                    if (mUpdatableViews == null) {
                        return;
                    }
                    for (UpdatableView view : mUpdatableViews) {
                        view.displayErrorMessage(query);
                    }
                }
            });
        }
    }

    @Override
    public void onUserAction(UserActionEnum action, @Nullable final Bundle args) {
        LOGI(TAG, "onUserAction -> " + action);
        if (isUserActionValid(action)) {
            mModel.deliverUserAction(action, args, new Model.UserActionCallback() {
                @Override
                public void onModelUpdated(Model model, UserActionEnum userAction) {
                    if (mUpdatableViews == null) {
                        return;
                    }
                    for (UpdatableView view : mUpdatableViews) {
                        view.displayUserActionResult(model, args, userAction, true);
                    }
                }

                @Override
                public void onError(UserActionEnum userAction) {
                    if (mUpdatableViews == null) {
                        return;
                    }
                    for (UpdatableView view : mUpdatableViews) {
                        view.displayUserActionResult(null, args, userAction, false);
                    }
                }
            });
        } else {
            if (mUpdatableViews == null) {
                return;
            }
            for (UpdatableView view : mUpdatableViews) {
                view.displayUserActionResult(null, args, action, false);
            }
            throw new RuntimeException(
                    "Invalid user action " + (action != null ? action.getId() : null) +
                            ". Have you called setValidUserActions on your presenter, with all " +

                            "the UserActionEnum you want to support?");
        }
    }

    protected boolean isUserActionValid(UserActionEnum action) {
        if (mValidUserActions != null && action != null) {
            for (UserActionEnum userActionEnum : mValidUserActions) {
                if (userActionEnum.getId() == action.getId()) {
                    return true;
                }
            }
        }
        return false;
    }
}
