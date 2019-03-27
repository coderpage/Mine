package com.coderpage.mine.app.tally.eventbus;

import com.coderpage.mine.app.tally.persistence.model.Record;

/**
 * @author abner-l. 2017-03-19
 */
public class EventRecordDelete {
    private final Record mRecord;

    public EventRecordDelete(Record expense) {
        mRecord = expense;
    }

    public Record getRecord() {
        return mRecord;
    }
}
