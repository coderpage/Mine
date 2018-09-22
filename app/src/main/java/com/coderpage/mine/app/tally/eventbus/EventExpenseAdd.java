package com.coderpage.mine.app.tally.eventbus;

import com.coderpage.mine.app.tally.persistence.model.Expense;

/**
 * @author lc. 2018-09-02 20:28
 * @since 0.6.0
 */

public class EventExpenseAdd {
    private Expense expense;

    public EventExpenseAdd(Expense expense) {
        this.expense = expense;
    }

    public Expense getExpense() {
        return expense;
    }
}
