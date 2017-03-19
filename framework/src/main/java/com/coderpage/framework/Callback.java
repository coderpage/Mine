package com.coderpage.framework;

/**
 * @author abner-l. 2017-01-22
 */

public interface Callback<TData, TError> {
    void success(TData data);

    void failure(TError error);
}
