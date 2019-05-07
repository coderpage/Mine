package com.coderpage.base.utils;

/**
 * @author lc. 2019-05-07 15:22
 * @since 0.6.2
 */
public class FunctionUtils {

    public static <T> void invokeSafe(T t, Task<T> task) {
        if (t != null) {
            task.run(t);
        }
    }

    public interface Task<T> {
        void run(T t);
    }
}
