package com.coderpage.mine.app.tally.eventbus;

import com.coderpage.mine.app.tally.persistence.model.Record;

/**
 * @author lc. 2018-09-18 23:26
 * @since 0.6.0
 */

public class EventIncomeAdd {

    private Record income;

    public EventIncomeAdd(Record income){
        this.income = income;
    }

    public Record getIncome() {
        return income;
    }
}
