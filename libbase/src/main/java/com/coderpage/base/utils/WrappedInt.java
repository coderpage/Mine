package com.coderpage.base.utils;

/**
 * @author lc. 2019-02-13 10:49
 * @since 0.6.0
 */

public class WrappedInt {

    private int i;

    public WrappedInt(int i) {
        this.i = i;
    }

    public int get() {
        return i;
    }

    public void set(int i) {
        this.i = i;
    }
}
