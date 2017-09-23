package com.coderpage.framework;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.coderpage.base.utils.LogUtils.LOGI;
import static com.coderpage.base.utils.LogUtils.makeLogTag;


/**
 * @author abner-l. 2017-04-13
 * @since 0.2.0
 */

public class PresenterImpl<
        M extends Model<Q, UA, M, E>,
        V extends UpdatableView<M, Q, UA, E>,
        UA extends UserActionEnum,
        Q extends QueryEnum,
        E> implements Presenter<Q, UA>, UpdatableView.UserActionListener<UA> {
    private static final String TAG = makeLogTag(PresenterImpl.class);

    private List<WeakReference<V>> mUpdatableViews;

    private M mModel;

    private Q[] mInitialQueriesToLoad;

    private UA[] mValidUserActions;

    public PresenterImpl(M model,
                         V view,
                         UA[] validUserActions,
                         Q[] initialQueries) {
        this(model, Arrays.asList(view), validUserActions, initialQueries);
    }

    public PresenterImpl(M model,
                         @Nullable List<V> views,
                         UA[] validUserActions,
                         Q[] initialQueries) {
        mModel = model;
        mValidUserActions = validUserActions;
        mInitialQueriesToLoad = initialQueries;
        if (views == null) return;

        mUpdatableViews = new ArrayList<>(views.size());
        for (V view : views) {
            mUpdatableViews.add(new WeakReference<V>(view));
        }

        for (WeakReference<V> ref : mUpdatableViews) {
            final V v = ref.get();
            if (v != null) {
                v.addListener(this);
            }
        }
    }

    @Override
    public void loadInitialQueries() {
        // 如果不需要加载初始化数据，直接更新 view
        if (mInitialQueriesToLoad == null || mInitialQueriesToLoad.length == 0) {
            if (mUpdatableViews == null) {
                return;
            }
//            for (UpdatableView view : mUpdatableViews) {
//                view.displayData(mModel, null);
//            }
            return;
        }
        // 加载初始化数据
        for (Q queryEnum : mInitialQueriesToLoad) {
            mModel.requestData(queryEnum, new Model.DataQueryCallback<M, Q, E>() {
                @Override
                public void onModelUpdated(M model, Q query) {
                    if (mUpdatableViews == null) {
                        return;
                    }
                    for (WeakReference<V> ref : mUpdatableViews) {
                        V view = ref.get();
                        if (view != null) {
                            view.displayData(model, query);
                        }
                    }
                }

                @Override
                public void onError(Q query, E error) {
                    if (mUpdatableViews == null) {
                        return;
                    }
                    for (WeakReference<V> ref : mUpdatableViews) {
                        V view = ref.get();
                        if (view != null) {
                            view.displayErrorMessage(query, error);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onUserAction(UA action, @Nullable final Bundle args) {
        LOGI(TAG, "onUserAction -> " + action);
        if (isUserActionValid(action)) {
            mModel.deliverUserAction(action, args, new Model.UserActionCallback<M, UA, E>() {
                @Override
                public void onModelUpdated(M model, UA userAction) {
                    if (mUpdatableViews == null) {
                        return;
                    }
                    for (WeakReference<V> ref : mUpdatableViews) {
                        V view = ref.get();
                        if (view != null) {
                            view.displayUserActionResult(model, args, userAction, true, null);
                        }
                    }
                }

                @Override
                public void onError(UA userAction, E error) {
                    if (mUpdatableViews == null) {
                        return;
                    }
                    for (WeakReference<V> ref : mUpdatableViews) {
                        V view = ref.get();
                        if (view != null) {
                            view.displayUserActionResult(null, args, userAction, false, error);
                        }
                    }
                }
            });
        } else {
            if (mUpdatableViews == null) {
                return;
            }
            throw new RuntimeException(
                    "Invalid user action " + (action != null ? action.getId() : null) +
                            ". Have you called setValidUserActions on your presenter, with all " +
                            "the UserActionEnum you want to support?");
        }
    }

    protected boolean isUserActionValid(UA action) {
        if (mValidUserActions != null && action != null) {
            for (UA userActionEnum : mValidUserActions) {
                if (userActionEnum.getId() == action.getId()) {
                    return true;
                }
            }
        }
        return false;
    }
}
