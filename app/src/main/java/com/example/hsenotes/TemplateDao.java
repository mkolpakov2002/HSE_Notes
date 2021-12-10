package com.example.hsenotes;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

public interface TemplateDao<N extends Note> {
    // Добавление Note в бд
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(N note);

    // Удаление Note из бд
    @Delete
    void delete(N note);

    // Удаление Note из бд
    @Query("DELETE FROM note WHERE noteId = :id")
    void delete(int id);

    @Update
    void update(N note);

    // Получение всех Person из бд
    @Query("SELECT * FROM note")
    List<N> getAll();

    // Получение всех Person из бд с условием
    @Query("SELECT * FROM note WHERE noteName LIKE :suchNoteName")
    List<N> getAllNotesWithSuchNoteName(String suchNoteName);

    @Query("SELECT * FROM note WHERE noteId = :suchId")
    N getById(long suchId);
}
