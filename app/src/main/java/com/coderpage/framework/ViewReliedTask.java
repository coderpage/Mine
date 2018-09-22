package com.coderpage.framework;

/**
 * @author lc
 *         created on 2018/8/13 下午3:41
 *         description : 在 ViewModel 中，有些业务处理需要 View 的支持，比如弹出一个网络加载 Dialog。可以通过 LiveData 将
 *         ViewModel 和 View 联系起来，在合法的生命周期中使用 View 对象
 */
public interface ViewReliedTask<V> {
    /**
     * 执行业务
     *
     * @param view ViewModel 绑定的 View
     */
    void execute(V view);
}
