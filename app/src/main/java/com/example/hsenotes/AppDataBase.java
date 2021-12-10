package com.example.hsenotes;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Note.class, Birthday.class, Event.class, Reminder.class /*, AnotherEntityType.class, AThirdEntityType.class */}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDataBase extends RoomDatabase {

    public abstract NoteDao getNoteDao();
    public abstract BirthdayDao getBirthdayDao();
    public abstract EventDao getEventDao();
    public abstract ReminderDao getReminderDao();
}