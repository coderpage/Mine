package com.coderpage.mine.app.tally.module.home.model;

/**
 * @author lc. 2018-07-11 20:22
 * @since 0.6.0
 *
 * 首页模块数据 ITEM，定义模块的类型、模块数据
 */

public class HomeDisplayData {

    /** 模块类型：底部 VIEW */
    public static final int TYPE_BOTTOM = -1;

    /** 模块类型：本月消费、本月收入数据模块 */
    public static final int TYPE_MONTH_INFO = 1;

    /** 模块类型：近3日账单数据 */
    public static final int TYPE_RECENT_DAY_INFO = 2;

    /** 模块类型：账单记录 ITEM */
    public static final int TYPE_RECORD_ITEM = 3;

    public HomeDisplayData(int type, Object internal) {
        this.type = type;
        this.internal = internal;
    }

    private int type;

    private Object internal;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getInternal() {
        return internal;
    }

    public void setInternal(Object internal) {
        this.internal = internal;
    }
}
