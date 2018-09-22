package com.coderpage.base.common;

/**
 * @author abner-l. 2017-05-07
 */

public class Result<TData, TError> extends ResultAbs<TData, TError> {
    public Result() {
    }

    public Result(TData data, TError error) {
        this.data = data;
        this.error = error;
    }
}
