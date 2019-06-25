package com.coderpage.mine.app.tally.utils;

import android.app.Activity;

import com.coderpage.mine.Global;
import com.coderpage.mine.app.tally.ui.dialog.FingerprintAuthDialog;

/**
 * @author lc. 2019-06-25 22:07
 * @since 0.7.0
 */

public class SecurityUtils {

    public static void executeAfterFingerprintAuth(Activity activity, Runnable runnable) {
        if (!Global.getInstance().isNeedFingerprint()) {
            runnable.run();
            return;
        }
        new FingerprintAuthDialog(activity).setListener(success -> {
            if (success) {
                runnable.run();
                Global.getInstance().setNeedFingerprint(false);
            }
        }).show();
    }
}
