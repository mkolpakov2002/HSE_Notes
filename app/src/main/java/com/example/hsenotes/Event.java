package com.example.hsenotes;

import androidx.room.Entity;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.List;

@Entity
public class Event extends Note implements Serializable {
    @TypeConverters(Converters.class)
    protected List<String> people;

    public void setPeople(List<String> people) {
        this.people = people;
    }

    public List<String> getPeople() {
        return people;
    }
}
