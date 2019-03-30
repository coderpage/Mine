package com.coderpage.mine.app.tally.module.search;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.coderpage.base.common.Callback;
import com.coderpage.base.common.IError;
import com.coderpage.base.utils.ArrayUtils;
import com.coderpage.base.utils.CommonUtils;
import com.coderpage.framework.BaseViewModel;
import com.coderpage.mine.app.tally.common.utils.BaseLoadDelegate;
import com.coderpage.mine.app.tally.persistence.model.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lc. 2019-03-27 23:27
 * @since 0.6.0
 *
 * 搜索页 ViewModel
 */

public class SearchViewModel extends BaseViewModel {

    /** 最近一次搜索的时间，用于控制搜索的频率 */
    private long mLastSearchTimestamp;

    /** 是否显示索索结果页 */
    private ObservableBoolean mShowSearchResult = new ObservableBoolean(false);
    /** 搜索关键字 */
    private ObservableField<String> mSearchKeyWord = new ObservableField<>();

    /** 搜索历史列表 */
    private MutableLiveData<List<String>> mSearchHistoryList = new MutableLiveData<>();
    /** 搜索结果列表 */
    private MutableLiveData<List<Record>> mSearchResultList = new MutableLiveData<>();

    private Handler mHandler;
    private Runnable mSearchPendingTask = this::search;
    private SearchRepository mRepository;
    private BaseLoadDelegate<Record> mLoadDelegate;

    public SearchViewModel(Application application) {
        super(application);
        mHandler = new Handler();
        mRepository = new SearchRepository();
        mLoadDelegate = new BaseLoadDelegate<Record>(15) {
            @Override
            public void requestData(int page, int pageSize, Callback<List<Record>, IError> callback) {
                String keyword = mSearchKeyWord.get();
                mRepository.queryRecords(page, pageSize, keyword, callback);
            }

            @Override
            public void onRequestFinish(RequestType type, List<Record> dataList, @Nullable IError error) {
                if (error != null) {
                    showToastShort(error.msg());
                    return;
                }
                mSearchResultList.postValue(dataList);
            }
        };
        mSearchKeyWord.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                String keyword = mSearchKeyWord.get();
                if (TextUtils.isEmpty(keyword)) {
                    // 搜索框为空，隐藏搜索结果。显示搜索历史记录
                    mShowSearchResult.set(false);
                }
                // 搜索框有内容，检索数据
                else {
                    mHandler.removeCallbacks(mSearchPendingTask);
                    mHandler.postDelayed(mSearchPendingTask, 500);
                }
            }
        });
        // 读取历史记录
        loadSearchHistory();
    }

    public ObservableBoolean getShowSearchResult() {
        return mShowSearchResult;
    }

    public ObservableField<String> getSearchKeyWord() {
        return mSearchKeyWord;
    }

    LiveData<List<String>> getSearchHistoryList() {
        return mSearchHistoryList;
    }

    LiveData<List<Record>> getSearchResultList() {
        return mSearchResultList;
    }

    void refresh() {
        mLoadDelegate.refresh();
    }

    void load() {
        mLoadDelegate.load();
    }

    void loadMore() {
        mLoadDelegate.loadMore();
    }

    LiveData<Boolean> getRefreshing() {
        return mLoadDelegate.getRefreshing();
    }

    LiveData<Boolean> getLoadingMore() {
        return mLoadDelegate.getLoadingMore();
    }

    LiveData<Integer> getLoadingStatus() {
        return mLoadDelegate.getLoadingStatus();
    }

    /** 搜索点击 */
    public void onSearchClick() {
        refresh();
    }

    /** 历史搜索记录 item 点击 */
    public void onSearchHistoryItemClick(String history) {
        mSearchKeyWord.set(history);
    }

    /** 移除搜索记录 ITEM */
    public void onRemoveHistoryItemClick(String history) {
        List<String> searchHistoryList = mSearchHistoryList.getValue();
        searchHistoryList = searchHistoryList == null ? new ArrayList<>() : searchHistoryList;
        ArrayUtils.remove(searchHistoryList, item -> CommonUtils.isEqual(item, history));
        // 缓存到本地
        mRepository.saveSearchHistory(searchHistoryList,
                // 缓存成功，刷新列表
                v -> loadSearchHistory());
    }

    /** 清除搜索历史点击 */
    public void onClearSearchHistoryClick() {
        mRepository.saveSearchHistory(new ArrayList<>(), v -> {
            // 清除完成后 刷新搜索历史记录
            loadSearchHistory();
        });
    }

    private void search() {
        String kewWord = mSearchKeyWord.get();
        if (TextUtils.isEmpty(kewWord)) {
            showToastShort("请先输入搜索内容");
            return;
        }
        mLoadDelegate.refresh(result -> {
            // 显示搜索结果
            mShowSearchResult.set(true);
            // 缓存搜索历史记录
            addSearchHistory(kewWord);
        });
    }

    /** 加载搜索历史记录 */
    private void loadSearchHistory() {
        mRepository.loadSearchHistory(historyList -> {
            mSearchHistoryList.setValue(historyList);
        });
    }

    /** 添加搜索记录 */
    private void addSearchHistory(String keyWord) {
        if (TextUtils.isEmpty(keyWord)) {
            return;
        }
        List<String> searchHistoryList = mSearchHistoryList.getValue();
        searchHistoryList = searchHistoryList == null ? new ArrayList<>() : searchHistoryList;
        // 移除之前的，添加到第一个位置
        ArrayUtils.remove(searchHistoryList, item -> CommonUtils.isEqual(item, keyWord));
        // 添加到列表中
        searchHistoryList.add(0, keyWord);
        // 历史记录不得超过 10
        int maxCacheSize = 10;
        if (searchHistoryList.size() > maxCacheSize) {
            searchHistoryList = searchHistoryList.subList(0, maxCacheSize);
        }
        // 缓存到本地
        mRepository.saveSearchHistory(searchHistoryList,
                // 缓存成功，刷新列表
                v -> loadSearchHistory());
    }
}
