package com.coderpage.mine.app.tally.eventbus;

import com.coderpage.mine.app.tally.persistence.model.Record;

/**
 * @author abner-l. 2017-03-19
 */
public class EventRecordAdd {
    private final Record mRecord;

    public EventRecordAdd(Record record) {
        mRecord = record;
    }

    public Record getRecord() {
        return mRecord;
    }
}
