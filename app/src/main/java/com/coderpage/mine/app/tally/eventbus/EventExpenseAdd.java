package com.coderpage.mine.app.tally.eventbus;

import com.coderpage.mine.app.tally.persistence.model.Record;

/**
 * @author lc. 2018-09-02 20:28
 * @since 0.6.0
 */

public class EventExpenseAdd {
    private Record expense;

    public EventExpenseAdd(Record expense) {
        this.expense = expense;
    }

    public Record getExpense() {
        return expense;
    }
}
