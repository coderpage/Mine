package com.coderpage.mine.app.tally.common.utils;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.Nullable;

import com.coderpage.base.common.Callback;
import com.coderpage.base.common.IError;
import com.coderpage.base.common.NonThrowError;
import com.coderpage.base.common.Result;
import com.coderpage.base.common.SimpleCallback;
import com.coderpage.base.widget.LoadingLayout;
import com.coderpage.mine.app.tally.common.error.ErrorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author lc. 2019-01-17 16:50
 * @since 0.6.0
 */

public abstract class BaseLoadDelegate<T> {

    /** 页号 */
    private int mPageIndex = 1;
    /** 每页数量量 */
    private int mPageSize = 10;
    /** 数据列表 */
    private List<T> mDataList = new ArrayList<>();
    /** 是否正在请求数据中... */
    private AtomicBoolean mIsRequestingData = new AtomicBoolean();

    /** 是否正在刷新数据 */
    private MutableLiveData<Boolean> mRefreshing = new MutableLiveData<>();
    /** 是否正在加载更多 */
    private MutableLiveData<Boolean> mLoadingMore = new MutableLiveData<>();
    /** 加载数据状态 */
    private MutableLiveData<Integer> mLoadingStatus = new MutableLiveData<>();

    public BaseLoadDelegate(int pageSize) {
        mPageSize = pageSize;
        mLoadingStatus.setValue(LoadingLayout.STATUS_SUCCESS);
    }

    public int getPageIndex() {
        return mPageIndex;
    }

    /**
     * 设置初始化显示的数据列表
     *
     * @param initData 初始化数据列表
     */
    public void setInitData(List<T> initData) {
        if (initData == null || initData.isEmpty()) {
            return;
        }
        mPageIndex = (int) Math.ceil((float) initData.size() / (float) mPageSize);
        mDataList.clear();
        mDataList.addAll(initData);
    }

    public LiveData<Boolean> getRefreshing() {
        return mRefreshing;
    }

    public LiveData<Boolean> getLoadingMore() {
        return mLoadingMore;
    }

    public LiveData<Integer> getLoadingStatus() {
        return mLoadingStatus;
    }

    /**
     * 返回列表中最后一个 ITEM
     *
     * @return 如果列表为空，返回 null
     */
    @Nullable
    public T getLastItem() {
        return mDataList.isEmpty() ? null : mDataList.get(mDataList.size() - 1);
    }

    /**
     * 加载第一页数据
     */
    public void load() {
        // 正在请求数据。不重复请求
        if (mIsRequestingData.getAndSet(true)) {
            return;
        }

        // 设置正在加载数据
        mLoadingStatus.setValue(LoadingLayout.STATUS_LOADING);
        int pageIndex = 1;
        // 回调请求开始
        onRequestStart(RequestType.LOAD);
        // 请求第一页数据
        requestData(pageIndex, mPageSize, new Callback<List<T>, IError>() {
            @Override
            public void success(List<T> result) {
                // 刷新数据，为空。显示空数据占位
                if (result == null || result.isEmpty()) {
                    mLoadingStatus.postValue(LoadingLayout.STATUS_EMPTY);
                } else {
                    mLoadingStatus.postValue(LoadingLayout.STATUS_SUCCESS);
                }

                mDataList.clear();
                if (result != null) {
                    mDataList.addAll(result);
                }

                mPageIndex = pageIndex;
                mIsRequestingData.set(false);
                // 回调请求结束
                onRequestFinish(RequestType.LOAD, mDataList, null);
            }

            @Override
            public void failure(IError error) {
                mLoadingStatus.setValue(LoadingLayout.STATUS_ERROR);
                mIsRequestingData.set(false);
                // 回调请求结果
                onRequestFinish(RequestType.LOAD, mDataList, error);
            }
        });
    }

    public void refresh() {
        refresh(null);
    }

    /**
     * 刷新数据
     */
    public void refresh(@Nullable SimpleCallback<Result<List<T>, IError>> callback) {
        // 正在请求数据。不重复请求
        if (mIsRequestingData.getAndSet(true)) {
            mRefreshing.setValue(false);
            return;
        }

        // 设置正在加载数据
        mRefreshing.setValue(true);
        int pageIndex = 1;
        // 回调请求开始
        onRequestStart(RequestType.REFRESH);
        // 请求第一页数据
        requestData(pageIndex, mPageSize, new Callback<List<T>, IError>() {
            @Override
            public void success(List<T> result) {
                // 刷新数据，为空。显示空数据占位
                if (result == null || result.isEmpty()) {
                    mLoadingStatus.postValue(LoadingLayout.STATUS_EMPTY);
                } else {
                    mLoadingStatus.postValue(LoadingLayout.STATUS_SUCCESS);
                }

                mDataList.clear();
                if (result != null) {
                    mDataList.addAll(result);
                }

                mPageIndex = pageIndex;
                mRefreshing.postValue(false);
                mIsRequestingData.set(false);
                // 回调请求结束
                onRequestFinish(RequestType.REFRESH, mDataList, null);
                if (callback != null) {
                    callback.success(new Result<>(mDataList, null));
                }
            }

            @Override
            public void failure(IError error) {
                mRefreshing.postValue(false);
                mIsRequestingData.set(false);
                // 回调请求结果
                onRequestFinish(RequestType.REFRESH, mDataList, error);
                if (callback != null) {
                    callback.success(new Result<>(null, error));
                }
            }
        });
    }

    /**
     * 加载更多
     */
    public void loadMore() {
        // 正在请求数据。不重复请求
        if (mIsRequestingData.get()) {
            return;
        }

        // 当前列表数据为空。不允许加载下一页
        if (mDataList.isEmpty()) {
            mLoadingMore.postValue(false);
            return;
        }

        // 正在请求数据。不重复请求
        if (mIsRequestingData.getAndSet(true)) {
            return;
        }

        // 设置正在加载更多数据
        mLoadingMore.postValue(true);
        int pageIndex = mPageIndex + 1;
        // 回调请求开始
        onRequestStart(RequestType.LOAD_MORE);
        // 请求第一页数据
        requestData(pageIndex, mPageSize, new Callback<List<T>, IError>() {
            @Override
            public void success(List<T> result) {
                IError error = null;
                if (result == null || result.isEmpty()) {
                    // 数据为空。没有更多数据
                    error = new NonThrowError(ErrorCode.UNKNOWN, "没有更多数据~");
                } else {
                    // 将数据添加到列表之后
                    mDataList.addAll(result);
                    // 更新页号
                    mPageIndex = pageIndex;
                }

                mLoadingMore.postValue(false);
                mIsRequestingData.set(false);
                // 回调请求结束
                onRequestFinish(RequestType.LOAD_MORE, mDataList, error);
            }

            @Override
            public void failure(IError error) {
                mLoadingMore.postValue(false);
                mIsRequestingData.set(false);
                // 回调请求结果
                onRequestFinish(RequestType.LOAD_MORE, mDataList, error);
            }
        });
    }

    /**
     * 后台刷新全部
     *
     * 应用场景：当列表中某一个 ITEM 被修改后，需要刷新当前列表的所有数据.
     */
    public void refreshAllBackground() {
        refreshAllBackground(null);
    }

    /**
     * 后台刷新全部
     *
     * 应用场景：当列表中某一个 ITEM 被修改后，需要刷新当前列表的所有数据.
     *
     * @param callback 请求结果回调
     */
    public void refreshAllBackground(@Nullable SimpleCallback<Result<List<T>, IError>> callback) {
        // 正在请求数据。不重复请求
        if (mIsRequestingData.getAndSet(true)) {
            return;
        }

        int pageIndex = 1;
        // 后台刷新。会请求当前显示所有数据
        int pageSize = mPageSize * mPageIndex;
        // 回调请求开始
        onRequestStart(RequestType.REFRESH_BACKGROUND);
        // 请求第一页数据
        requestData(pageIndex, pageSize, new Callback<List<T>, IError>() {
            @Override
            public void success(List<T> result) {
                // 刷新数据，为空。显示空数据占位
                if (result == null || result.isEmpty()) {
                    mLoadingStatus.postValue(LoadingLayout.STATUS_EMPTY);
                } else {
                    mLoadingStatus.postValue(LoadingLayout.STATUS_SUCCESS);
                }

                mDataList.clear();
                if (result != null) {
                    mDataList.addAll(result);
                }

                mIsRequestingData.set(false);
                // 回调请求结束
                onRequestFinish(RequestType.REFRESH_BACKGROUND, mDataList, null);

                if (callback != null) {
                    callback.success(new Result<>(result, null));
                }
            }

            @Override
            public void failure(IError error) {
                mIsRequestingData.set(false);
                // 回调请求结果
                onRequestFinish(RequestType.REFRESH_BACKGROUND, mDataList, error);

                if (callback != null) {
                    callback.success(new Result<>(null, error));
                }
            }
        });
    }

    /**
     * 当请求开始时被调用
     *
     * @param type 请求类型
     */
    public void onRequestStart(RequestType type) {
        // no-op
    }

    /**
     * 当请求结束时被调用
     *
     * @param type     请求类型
     * @param dataList 数据列表
     * @param error    错误。错误为空时表示请求成功。不为空时表示请求异常，应该处理错误
     */
    public void onRequestFinish(RequestType type, List<T> dataList, @Nullable IError error) {
        // no-op
    }

    /**
     * 请求数据列表
     *
     * @param page     页号
     * @param pageSize 每页数据量
     * @param callback 回调
     */
    public abstract void requestData(int page, int pageSize, Callback<List<T>, IError> callback);

    public enum RequestType {
        // 加载数据（第一页）
        LOAD,
        // 刷新
        REFRESH,
        // 后台刷新
        REFRESH_BACKGROUND,
        // 加载更多
        LOAD_MORE
    }
}