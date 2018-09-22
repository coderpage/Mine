package com.coderpage.mine.app.tally.eventbus;

import com.coderpage.mine.app.tally.persistence.model.Income;

/**
 * @author lc. 2018-09-18 23:26
 * @since 0.6.0
 */

public class EventIncomeAdd {

    private Income income;

    public EventIncomeAdd(Income income){
        this.income = income;
    }

    public Income getIncome() {
        return income;
    }
}
