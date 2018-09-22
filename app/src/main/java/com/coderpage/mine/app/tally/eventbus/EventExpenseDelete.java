package com.coderpage.mine.app.tally.eventbus;

import com.coderpage.mine.app.tally.persistence.model.Expense;

/**
 * @author lc. 2018-09-17 23:13
 * @since 0.6.0
 */
public class EventExpenseDelete {

    private final Expense expense;

    public EventExpenseDelete(Expense expense) {
        this.expense = expense;
    }

    public Expense getExpense() {
        return expense;
    }
}
