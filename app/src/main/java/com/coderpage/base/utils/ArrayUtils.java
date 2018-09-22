package com.coderpage.base.utils;

import android.support.annotation.Nullable;

import java.util.Collection;

/**
 * @author lc. 2018-09-02 16:55
 * @since 0.6.0
 */

public class ArrayUtils {

    @Nullable
    public static <E> E query(Collection<E> collection, Comparator<E> comparator) {
        if (collection == null || collection.isEmpty()) {
            return null;
        }
        for (E e : collection) {
            if (comparator.compare(e)) {
                return e;
            }
        }
        return null;
    }

    public interface Comparator<E> {
        boolean compare(E e);
    }
}
