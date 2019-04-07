package com.coderpage.mine.app.tally.module.records;

import android.app.Activity;
import android.app.Application;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.coderpage.base.common.Callback;
import com.coderpage.base.common.IError;
import com.coderpage.base.utils.CommonUtils;
import com.coderpage.base.utils.UIUtils;
import com.coderpage.framework.BaseViewModel;
import com.coderpage.mine.R;
import com.coderpage.mine.app.tally.eventbus.EventRecordDelete;
import com.coderpage.mine.app.tally.module.chart.TallyChartActivity;
import com.coderpage.mine.app.tally.module.detail.RecordDetailActivity;
import com.coderpage.mine.app.tally.module.edit.RecordEditActivity;
import com.coderpage.mine.app.tally.persistence.model.Record;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;

/**
 * @author lc. 2019-01-04 10:47
 * @since 0.6.0
 */

public class RecordItemViewModel extends BaseViewModel {

    private DecimalFormat mMoneyFormat = new DecimalFormat("0.00");

    private RecordsRepository mRepository;

    public RecordItemViewModel(Application application) {
        super(application);
        mRepository = new RecordsRepository();
    }

    /** 格式化金额 */
    public String formatMoney(Record record) {
        if (record == null) {
            return "--";
        }
        return "￥" + mMoneyFormat.format(record.getAmount());
    }

    /**
     * 记录 ITEM 点击
     *
     * @param view     {@link View}
     * @param activity {@link Activity}
     * @param record   记录 ITEM
     */
    public void onItemClick(View view, Activity activity, Record record) {
        if (CommonUtils.isViewFastDoubleClick(view) || record == null) {
            return;
        }
        if (record.getType() == Record.TYPE_EXPENSE) {
            RecordDetailActivity.openExpenseDetail(activity, record.getId());
        }
        if (record.getType() == Record.TYPE_INCOME) {
            RecordDetailActivity.openIncomeDetail(activity, record.getId());
        }
    }

    /**
     * 记录 ITEM 长按
     *
     * @param view     {@link View}
     * @param activity {@link Activity}
     * @param record   记录 ITEM
     */
    public boolean onItemLongClick(View view, Activity activity, Record record) {
        new AlertDialog.Builder(activity).setItems(R.array.recordItemLongClickOption, (dialog, which) -> {
            switch (which) {

                // 修改
                case 0:
                    if (record.getType() == Record.TYPE_EXPENSE) {
                        RecordEditActivity.openAsUpdateExpense(activity, record.getId());
                    }
                    if (record.getType() == Record.TYPE_INCOME) {
                        RecordEditActivity.openAsUpdateIncome(activity, record.getId());
                    }
                    break;

                // 删除
                case 1:
                    mRepository.deleteRecord(record.getId(), new Callback<Void, IError>() {
                        @Override
                        public void success(Void aVoid) {
                            EventBus.getDefault().post(new EventRecordDelete(record));
                        }

                        @Override
                        public void failure(IError iError) {
                            UIUtils.showToastShort(getApplication(), iError.msg());
                        }
                    });
                    break;
                default:
                    break;
            }
        }).show();
        return true;
    }

    /** 列表日期标题点击 */
    public void onItemDateTitleClick(Activity activity, RecordsDateTitle date) {
        if (date == null) {
            return;
        }
        // 打开对应月份的图表分析页
        TallyChartActivity.open(activity, date.getYear(), date.getMonth());
    }
}
