package com.coderpage.mine.app.tally.eventbus;

import com.coderpage.mine.app.tally.persistence.model.CategoryModel;

/**
 * @author lc. 2019-04-20 15:30
 * @since 0.6.0
 */


public class EventCategoryUpdate {

    private CategoryModel category;

    public EventCategoryUpdate(CategoryModel category) {
        this.category = category;
    }

    public CategoryModel getCategory() {
        return category;
    }
}
