package com.coderpage.mine.app.tally.eventbus;

import com.coderpage.mine.app.tally.persistence.model.Expense;

/**
 * @author lc. 2018-09-02 20:29
 * @since 0.6.0
 */

public class EventExpenseUpdate {

    private Expense expense;

    public EventExpenseUpdate(Expense expense) {
        this.expense = expense;
    }

    public Expense getExpense() {
        return expense;
    }
}
