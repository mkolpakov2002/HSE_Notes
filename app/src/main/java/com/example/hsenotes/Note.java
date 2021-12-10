package com.example.hsenotes;

import android.graphics.Color;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity
public class Note implements Serializable {
    @PrimaryKey
    protected int noteId;
    protected Date creationTime;
    protected String noteText;
    protected String noteName;
    @Ignore
    private boolean isSelectedOnScreen = false;

    public Note() {
    }

    public Note(Note another) {
        this.creationTime = another.creationTime; // you can access
        this.noteText = another.noteText;
        this.noteName = another.noteName;
        this.isSelectedOnScreen = another.isSelectedOnScreen;
        this.noteId = another.noteId;
    }

    public void setCreationTime(Date creationTime){
        this.creationTime = creationTime;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    public String getNoteName() {
        return noteName;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setIsSelectedOnScreen(boolean isSelectedOnScreen){
        this.isSelectedOnScreen = isSelectedOnScreen;
    }

    public boolean getIsSelectedOnScreen(){
        return isSelectedOnScreen;
    }

    void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
        NotesViewHolder.NotesHolder mViewHolder = (NotesViewHolder.NotesHolder) viewHolder;
        mViewHolder.noteName.setText(noteName);
        mViewHolder.textInfo.setText(noteText);
        mViewHolder.materialCardView.setStrokeColor(Color.TRANSPARENT);
        if(noteName.length()==0){
            mViewHolder.noteName.setVisibility(View.GONE);
        } else {
            mViewHolder.noteName.setVisibility(View.VISIBLE);
        }
    }
}
