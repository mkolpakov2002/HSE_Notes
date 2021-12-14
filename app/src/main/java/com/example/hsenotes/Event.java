package com.example.hsenotes;

import androidx.room.Entity;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.ArrayList;
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

    public void addToPeopleList(String adding) {
        if(people==null){
            people = new ArrayList<>();
        }
        people.add(adding);
    }

    public void deleteFromPeopleList(String adding) {
        if(people!=null){
            people.remove(people.lastIndexOf(adding));
        }
    }
}
