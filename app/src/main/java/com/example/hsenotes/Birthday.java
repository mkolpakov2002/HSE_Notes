package com.example.hsenotes;

import androidx.room.Entity;

import java.io.Serializable;
import java.util.Date;

@Entity
public class Birthday extends Note implements Serializable {
    protected Date birthdayTime;
    protected String personName;

    public void setBirthdayTime(Date birthdayTime) {
        this.birthdayTime = birthdayTime;
    }

    public void setPersonName(String name) {
        this.personName = name;
    }

    public Date getBirthdayTime() {
        return birthdayTime;
    }

    public String getPersonName() {
        return personName;
    }
}
