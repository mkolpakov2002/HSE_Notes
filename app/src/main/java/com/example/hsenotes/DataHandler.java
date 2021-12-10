package com.example.hsenotes;

public class DataHandler {
    private static Note note;

    public static synchronized Note getNote(){
        return note;
    }

    public static synchronized void setNote(Note note){
        DataHandler.note = note;
    }
}
