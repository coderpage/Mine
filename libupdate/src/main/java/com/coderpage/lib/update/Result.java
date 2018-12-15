package com.coderpage.lib.update;

/**
 * @author lc. 2017-09-23 23:49
 * @since 0.5.0
 */

public class Result <TData, TError> {
    private TData mData;
    private TError mErr;

    public Result() {
    }

    public Result(TData data, TError error) {
        mData = data;
        mErr = error;
    }


    public TData data() {
        return mData;
    }

    public void setData(TData data) {
        mData = data;
    }

    public TError error() {
        return mErr;
    }

    public void setErr(TError err) {
        mErr = err;
    }

    public boolean isOk() {
        return error() == null;
    }
}
