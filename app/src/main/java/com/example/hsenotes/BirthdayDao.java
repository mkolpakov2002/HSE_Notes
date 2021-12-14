package com.example.hsenotes;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BirthdayDao extends TemplateDao<Birthday> {
    @Query("DELETE FROM birthday WHERE noteId = :id")
    void delete(int id);

    // Получение всех Person из бд
    @Query("SELECT * FROM birthday")
    List<Birthday> getAll();

    // Получение всех Person из бд с условием
    @Query("SELECT * FROM birthday WHERE noteName LIKE :suchNoteName")
    List<Birthday> getAllNotesWithSuchNoteName(String suchNoteName);

    @Query("SELECT * FROM birthday WHERE noteId = :suchId")
    Birthday getById(long suchId);
}
