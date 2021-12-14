package com.example.hsenotes;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao extends TemplateDao<Note> {
    // Удаление Note из бд
    @Query("DELETE FROM note WHERE noteId = :id")
    void delete(int id);

    // Получение всех Person из бд
    @Query("SELECT * FROM note")
    List<Note> getAll();

    // Получение всех Person из бд с условием
    @Query("SELECT * FROM note WHERE noteName LIKE :suchNoteName")
    List<Note> getAllNotesWithSuchNoteName(String suchNoteName);

    @Query("SELECT * FROM note WHERE noteId = :suchId")
    Note getById(long suchId);

}
