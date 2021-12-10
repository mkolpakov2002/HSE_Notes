package com.example.hsenotes;

import androidx.room.Entity;

import java.io.Serializable;
import java.util.Date;

@Entity
public class Reminder extends Note implements Serializable {

    protected Date eventTime;

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public Date getEventTime() {
        return eventTime;
    }
}
