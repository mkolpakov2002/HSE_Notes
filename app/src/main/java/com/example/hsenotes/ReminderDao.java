package com.example.hsenotes;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ReminderDao extends TemplateDao<Reminder> {
    // Удаление Note из бд
    @Query("DELETE FROM reminder WHERE noteId = :id")
    void delete(int id);

    // Получение всех Person из бд
    @Query("SELECT * FROM reminder")
    List<Reminder> getAll();

    // Получение всех Person из бд с условием
    @Query("SELECT * FROM reminder WHERE noteName LIKE :suchNoteName")
    List<Reminder> getAllNotesWithSuchNoteName(String suchNoteName);

    @Query("SELECT * FROM reminder WHERE noteId = :suchId")
    Reminder getById(long suchId);
}
