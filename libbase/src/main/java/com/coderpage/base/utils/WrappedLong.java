package com.coderpage.base.utils;

/**
 * @author lc. 2019-02-13 10:49
 * @since 0.6.0
 */

public class WrappedLong {

    private long i;

    public WrappedLong(int i) {
        this.i = i;
    }

    public long get() {
        return i;
    }

    public void set(long i) {
        this.i = i;
    }
}
