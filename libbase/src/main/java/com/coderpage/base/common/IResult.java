package com.coderpage.base.common;

/**
 * @author abner-l. 2017-05-07
 */

public interface IResult<TData, TError> {

    TData data();

    TError error();

    boolean isOk();
}
