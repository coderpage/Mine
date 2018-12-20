package com.coderpage.mine.app.tally.eventbus;

import com.coderpage.mine.app.tally.persistence.model.Record;

/**
 * @author lc. 2018-09-17 23:13
 * @since 0.6.0
 */
public class EventExpenseDelete {

    private final Record expense;

    public EventExpenseDelete(Record expense) {
        this.expense = expense;
    }

    public Record getExpense() {
        return expense;
    }
}
