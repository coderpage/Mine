package com.coderpage.mine.app.tally.eventbus;

/**
 * @author lc. 2019-04-24 23:07
 * @since 0.6.0
 */

public class EventCategoryOrderChange {

    private final int categoryType;

    /**
     * 构造函数
     *
     * @param categoryType 分类类型
     *                     {@link com.coderpage.mine.app.tally.persistence.model.CategoryModel#TYPE_EXPENSE}
     *                     {@link com.coderpage.mine.app.tally.persistence.model.CategoryModel#TYPE_INCOME}
     */
    public EventCategoryOrderChange(int categoryType) {
        this.categoryType = categoryType;
    }

    public int getCategoryType() {
        return categoryType;
    }
}
