package com.coderpage.mine.app.tally.eventbus;

import com.coderpage.mine.app.tally.data.ExpenseItem;

/**
 * @author abner-l. 2017-03-19
 */
public class EventRecordUpdate {
    private final ExpenseItem mExpenseItem;

    public EventRecordUpdate(ExpenseItem expenseItem) {
        mExpenseItem = expenseItem;
    }

    public ExpenseItem getExpenseItem() {
        return mExpenseItem;
    }
}
