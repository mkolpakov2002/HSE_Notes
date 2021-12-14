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

    @Update
    void update(N note);

}
