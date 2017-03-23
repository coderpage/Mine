package com.coderpage.mine.app.tally.common.event;

import com.coderpage.mine.app.tally.data.ExpenseItem;

/**
 * @author abner-l. 2017-03-19
 */
public class EventRecordDelete {
    private final ExpenseItem mExpenseItem;

    public EventRecordDelete(ExpenseItem expenseItem) {
        mExpenseItem = expenseItem;
    }

    public ExpenseItem getExpenseItem() {
        return mExpenseItem;
    }
}
