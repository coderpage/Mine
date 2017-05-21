package com.coderpage.common;

import com.coderpage.framework.SimpleCallback;

/**
 * @author abner-l. 2017-01-22
 */

public interface Callback<TData, TError> extends SimpleCallback<TData> {
    void success(TData data);

    void failure(TError error);
}
