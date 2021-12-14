package com.example.hsenotes;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EventDao extends TemplateDao<Event> {
    @Query("DELETE FROM event WHERE noteId = :id")
    void delete(int id);

    // Получение всех Person из бд
    @Query("SELECT * FROM event")
    List<Event> getAll();

    // Получение всех Person из бд с условием
    @Query("SELECT * FROM event WHERE noteName LIKE :suchNoteName")
    List<Event> getAllNotesWithSuchNoteName(String suchNoteName);

    @Query("SELECT * FROM event WHERE noteId = :suchId")
    Event getById(long suchId);
}
