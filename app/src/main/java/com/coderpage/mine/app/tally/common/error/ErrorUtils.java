package com.coderpage.mine.app.tally.common.error;

import com.coderpage.common.IError;

import java.util.Locale;

/**
 * @author lc. 2017-09-17
 * @since 0.5.0
 */

public class ErrorUtils {

    public static String formatDisplayMsg(IError error) {
        return String.format(Locale.getDefault(), "[%d] %s", error.code(), error.msg());
    }
}
