package com.coderpage.base.utils;

/**
 * @author lc. 2019-02-13 10:48
 * @since 0.6.0
 */


public class WrappedObject<T> {
    private T t;

    public WrappedObject(T object) {
        this.t = object;
    }

    public T get() {
        return t;
    }

    public void set(T t) {
        this.t = t;
    }
}
