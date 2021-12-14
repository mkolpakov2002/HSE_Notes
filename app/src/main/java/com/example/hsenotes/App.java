package com.example.hsenotes;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.room.Room;

public class App extends Application {

    private static App instance;

    private static AppDataBase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, AppDataBase.class, "database")
                .allowMainThreadQueries()
                .build();
        //AppCompatDelegate.setDefaultNightMode(
        //AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public static App getInstance() {
        return instance;
    }

    public static AppDataBase getDatabase() {
        return database;
    }

    public static Context getContext() {
        return getInstance().getApplicationContext();
    }

}
