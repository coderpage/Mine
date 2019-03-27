package com.coderpage.base.utils;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author lc. 2018-09-02 16:55
 * @since 0.6.0
 */

public class ArrayUtils {

    /**
     * 拼接字符串
     *
     * @param delimiter      链接字符
     * @param element2String 格式化每个 ITEM 的字符格式
     * @param elements       item
     * @param <E>            类型
     * @return 拼接好的字符串
     */
    public static <E> String join(CharSequence delimiter, Function<E, CharSequence> element2String, E... elements) {
        StringJoiner joiner = new StringJoiner(delimiter);
        for (E cs : elements) {
            joiner.add(element2String.execute(cs));
        }

        return joiner.toString();
    }

    /**
     * 拼接字符串
     *
     * @param delimiter      链接字符
     * @param element2String 格式化每个 ITEM 的字符格式
     * @param elements       item
     * @param <E>            类型
     * @return 拼接好的字符串
     */
    public static <E> String join(CharSequence delimiter, Function<E, CharSequence> element2String, Iterable<E> elements) {
        StringJoiner joiner = new StringJoiner(delimiter);
        for (E cs : elements) {
            joiner.add(element2String.execute(cs));
        }

        return joiner.toString();
    }

    @Nullable
    public static <E> E findFirst(Collection<E> collection, Comparator<E> comparator) {
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

    /***
     * 移除集合中的指定元素
     *
     * @param list       集合
     * @param comparator 比较器，用于判断需要移除的元素
     * @return 返回移除的数量
     */
    public static <E> int remove(List<E> list, Comparator<E> comparator) {
        if (list == null || list.isEmpty()) {
            return 0;
        }

        int removeCount = 0;
        for (int i = list.size() - 1; i >= 0; i--) {
            E e = list.get(i);
            if (comparator.compare(e)) {
                list.remove(i);
                removeCount++;
            }
        }

        return removeCount;
    }

    /***
     * 查询集合中是否有指定的元素
     *
     * @param list       集合
     * @param comparator 比较器，用于判断元素是否相同
     * @return 返回是否包含指定的元素
     */
    public static <E> boolean contains(List<E> list, Comparator<E> comparator) {
        if (list == null || list.isEmpty()) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            E e = list.get(i);
            if (comparator.compare(e)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 遍历集合
     *
     * @param list     集合
     * @param consumer 遍历回调
     * @param <E>      泛型。元素类型
     */
    public static <E> void forEach(List<E> list, Consumer<E> consumer) {
        if (list == null || list.isEmpty()) {
            return;
        }
        int size = list.size();
        for (int i = 0; i < size; i++) {
            consumer.accept(size, i, list.get(i));
        }
    }

    /***
     * 查询集合中指定的元素
     *
     * @param list       集合
     * @param comparator 比较器，用于判断元素是否相同
     * @return 返回符合条件的所有元素
     */
    public static <E> List<E> find(List<E> list, Comparator<E> comparator) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>(0);
        }
        List<E> result = new ArrayList<>();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            E e = list.get(i);
            if (comparator.compare(e)) {
                result.add(e);
            }
        }
        return result;
    }

    public interface Comparator<E> {
        /**
         * 比较是否相同
         *
         * @param e item
         * @return true-相同  false-不相同
         */
        boolean compare(E e);
    }

    public interface Consumer<E> {

        /**
         * 遍历集合回调
         *
         * @param count 集合总数
         * @param index 当前遍历到的 item index
         * @param item  当前遍历到的 item
         */
        void accept(int count, int index, E item);
    }

    public interface Function<E, R> {
        /**
         * 执行函数
         *
         * @param e 函数参数
         * @return 返回值
         */
        R execute(E e);
    }
}
