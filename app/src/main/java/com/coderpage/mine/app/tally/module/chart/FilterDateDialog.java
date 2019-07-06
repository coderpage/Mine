package com.coderpage.mine.app.tally.module.chart;

import android.app.Activity;
import android.app.Dialog;

/**
 * @author lc. 2019-06-15 13:02
 * @since 0.6.3
 */

class FilterDateDialog extends Dialog {

    private long mStartDateTime;
    private long mEndDateTime;

    FilterDateDialog(Activity activity) {
        super(activity);
    }
}
