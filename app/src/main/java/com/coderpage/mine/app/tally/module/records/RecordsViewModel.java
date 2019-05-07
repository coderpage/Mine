package com.coderpage.mine.app.tally.module.records;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;

import com.coderpage.base.common.Callback;
import com.coderpage.base.common.IError;
import com.coderpage.base.utils.ArrayUtils;
import com.coderpage.base.utils.ResUtils;
import com.coderpage.base.utils.UIUtils;
import com.coderpage.base.utils.WrappedInt;
import com.coderpage.framework.BaseViewModel;
import com.coderpage.framework.ViewReliedTask;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.common.utils.BaseLoadDelegate;
import com.coderpage.mine.app.tally.eventbus.EventRecordAdd;
import com.coderpage.mine.app.tally.eventbus.EventRecordDelete;
import com.coderpage.mine.app.tally.eventbus.EventRecordUpdate;
import com.coderpage.mine.app.tally.persistence.model.Record;
import com.coderpage.mine.app.tally.utils.DateUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * @author lc. 2018-12-20 23:26
 * @since 0.6.0
 */

public class RecordsViewModel extends BaseViewModel implements LifecycleObserver {

    private String mToolbarTitleBase = "";

    /** 导航栏副标题 */
    private MutableLiveData<CharSequence> mToolbarTitle = new MutableLiveData<>();
    /** 记录列表 */
    private MutableLiveData<List<Object>> mRecordList = new MutableLiveData<>();
    /** 需要依赖 activity 的任务 */
    private MutableLiveData<ViewReliedTask<Activity>> mViewReliedTask = new MutableLiveData<>();

    /** 查询规则 */
    private RecordQuery mQuery = new RecordQuery.Builder()
            .setType(RecordQuery.TYPE_ALL)
            .setStartTime(0)
            .setEndTime(System.currentTimeMillis())
            .build();
    private RecordsRepository mRepository;
    /** 数据加载代理。处理数据的刷新、加载更多等操作 */
    private BaseLoadDelegate<Record> mLoadDelegate;

    public RecordsViewModel(Application application) {
        super(application);
        mToolbarTitleBase = ResUtils.getString(application, R.string.tally_toolbar_title_records);
        mRepository = new RecordsRepository();
        mLoadDelegate = new BaseLoadDelegate<Record>(15) {
            @Override
            public void requestData(int page, int pageSize, Callback<List<Record>, IError> callback) {
                mRepository.queryRecords(page, pageSize, mQuery, callback);
            }

            @Override
            public void onRequestFinish(RequestType type, List<Record> dataList, @Nullable IError error) {
                super.onRequestFinish(type, dataList, error);
                if (error != null) {
                    showToastShort(error.msg());
                    return;
                }
                // 更新显示的列表数据
                mRecordList.postValue(formatRecordList(dataList));
            }
        };
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

    LiveData<CharSequence> getToolbarTitle() {
        return mToolbarTitle;
    }

    LiveData<List<Object>> getRecordList() {
        return mRecordList;
    }

    /** 设置查询条件，并重新加载数据 */
    void setQuery(RecordQuery query) {
        if (query == null) {
            return;
        }
        mQuery = query;
        boolean refreshUseLoadMode = mRecordList.getValue() == null || mRecordList.getValue().isEmpty();
        if (refreshUseLoadMode) {
            mLoadDelegate.load();
        } else {
            mLoadDelegate.refresh();
        }

        // 格式化 Toolbar Title
        // 全部记录不显示区间信息
        String dateRange = mQuery.getStartTime() <= 0 ?
                "" : DateUtils.formatDisplayDateRange(getApplication(), mQuery.getStartTime(), mQuery.getEndTime());

        SpannableStringBuilder titleBuilder = new SpannableStringBuilder();
        titleBuilder.append(mToolbarTitleBase).append(" ");
        if (!TextUtils.isEmpty(dateRange)) {
            titleBuilder.append(createAbsoluteSizeSpan(dateRange, null, 14,
                    ResUtils.getColor(getApplication(), R.color.appTextColorSecondary)));
        }
        mToolbarTitle.setValue(titleBuilder);
    }

    void load() {
        mLoadDelegate.load();
    }

    void refresh() {
        mLoadDelegate.refresh();
    }

    void loadMore() {
        mLoadDelegate.loadMore();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Private method
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 创建指定大小的 span
     *
     * @param text     文字
     * @param typeface 字体
     * @param sizeDip  大小 dp
     * @param color    颜色
     */
    private SpannableString createAbsoluteSizeSpan(String text, Typeface typeface, int sizeDip, int color) {
        SpannableString tipSpan = new SpannableString(text);
        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(UIUtils.dp2px(getApplication(), sizeDip)) {
            @Override
            public void updateDrawState(@NonNull TextPaint textPaint) {
                super.updateDrawState(textPaint);
                textPaint.setFakeBoldText(false);
                if (color != 0) {
                    textPaint.setColor(color);
                }
                if (typeface != null) {
                    textPaint.setTypeface(typeface);
                }
            }
        };
        tipSpan.setSpan(sizeSpan, 0, tipSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return tipSpan;
    }

    /**
     * 格式化列表数据。插入日期标题
     *
     * @param source 源数据列表
     * @return 格式化后的数据列表
     */
    private List<Object> formatRecordList(List<Record> source) {
        List<Object> currentDisplayList = mRecordList.getValue() == null ? new ArrayList<>() : mRecordList.getValue();
        if (source == null || source.isEmpty()) {
            return currentDisplayList;
        }
        currentDisplayList.clear();

        WrappedInt yearTemp = new WrappedInt(-1);
        WrappedInt monthTemp = new WrappedInt(-1);
        Calendar calendar = Calendar.getInstance(Locale.getDefault());

        // 格式化列表数据。插入日期信息标题
        ArrayUtils.forEach(source, (count, index, item) -> {
            long time = item.getTime();
            calendar.setTimeInMillis(time);
            int selfYear = calendar.get(Calendar.YEAR);
            int selfMonth = calendar.get(Calendar.MONTH) + 1;
            if (yearTemp.get() != selfYear || monthTemp.get() != selfMonth) {
                currentDisplayList.add(new RecordsDateTitle(selfYear, selfMonth));
                yearTemp.set(selfYear);
                monthTemp.set(selfMonth);
            }
            currentDisplayList.add(item);
        });

        return currentDisplayList;
    }

    ///////////////////////////////////////////////////////////////////////////
    // LifeCycle
    ///////////////////////////////////////////////////////////////////////////

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate(LifecycleOwner owner) {
        Activity activity = (Activity) owner;
        Intent intent = activity.getIntent();
        RecordQuery query = intent.getParcelableExtra(RecordsActivity.EXTRA_QUERY);
        mQuery = query == null ? new RecordQuery.Builder()
                .setType(RecordQuery.TYPE_ALL)
                .setStartTime(0)
                .setEndTime(System.currentTimeMillis())
                .build() : query;
        setQuery(mQuery);

        EventBus.getDefault().register(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy(LifecycleOwner owner) {
        EventBus.getDefault().unregister(this);
    }

    ///////////////////////////////////////////////////////////////////////////
    // EventBus
    ///////////////////////////////////////////////////////////////////////////

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRecordAdd(EventRecordAdd event) {
        mLoadDelegate.refreshAllBackground();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRecordUpdate(EventRecordUpdate event) {
        mLoadDelegate.refreshAllBackground();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRecordDelete(EventRecordDelete event) {
        mLoadDelegate.refreshAllBackground();
    }
}
