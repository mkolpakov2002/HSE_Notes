package com.example.hsenotes;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class EditOneNoteActivity extends AppCompatActivity {
    Object someNote;
    private Note currentNote;
    private Birthday newBirthday;
    private Event newEvent;
    private Reminder newReminder;
    private EditText NoteName;

    private EditText NoteText;

    private Date date;
    private Date creationDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_one_note);
        MaterialToolbar toolbar = findViewById(R.id.edit_note_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Intent intent = getIntent();
        someNote = intent.getSerializableExtra("noteObject");
        NoteName = findViewById(R.id.note_name_title_edit);
        NoteText = findViewById(R.id.note_text_edit);
        TextView datePickedText = findViewById(R.id.birthday_date_text);
        ScrollView scrollView = findViewById(R.id.edit_one_note_scroll_layout);
        GestureDetector gestureDetector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }

                });
        scrollView.setOnTouchListener((v, event) -> {
            if(gestureDetector.onTouchEvent(event)){
                NoteText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(NoteText, InputMethodManager.SHOW_FORCED);
            }
            return false;
        });

        android.text.format.DateFormat df = new android.text.format.DateFormat();
        ConstraintLayout datePicker = findViewById(R.id.birthday_linear_layout);
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                builder.setSelection(creationDate.getTime());
                MaterialDatePicker<Long> picker = builder.build();
                picker.addOnPositiveButtonClickListener(selection -> {
                    date =  new Date(selection);
                    date.setHours(0);
                    datePickedText.setText(df.format("dd.MM.yyyy",date).toString());
                });
                picker.show(getSupportFragmentManager(), picker.toString());
            }
        });

        LinearLayout creationTime = findViewById(R.id.creation_time_layout);
        creationTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        datePickedText.setText(df.format("dd.MM.yyyy",new Date()).toString());
        TextView noteCreationTime = findViewById(R.id.creation_time_edit);
        creationDate = Calendar.getInstance().getTime();
        if(((Note) someNote).getCreationTime()==null){
            creationDate = new Date();
        } else {
            creationDate = ((Note) someNote).getCreationTime();
        }
        String currentDateTimeString = DateFormat.getDateTimeInstance().
                format(creationDate);
        noteCreationTime.setText(currentDateTimeString);
        if(someNote instanceof Birthday){
            newBirthday = (Birthday) someNote;
            newBirthday.setCreationTime(creationDate);
            newBirthday.setBirthdayTime(creationDate);
            datePicker.setVisibility(View.VISIBLE);
        } else if(someNote instanceof Event){
            newEvent = (Event) someNote;
            newEvent.setCreationTime(creationDate);
        } else if(someNote instanceof Reminder){
            newReminder = (Reminder) someNote;
            newReminder.setCreationTime(creationDate);
        } else {
            currentNote = (Note) someNote;
            currentNote.setCreationTime(creationDate);
        }




        NoteName.setText(((Note) someNote).getNoteName());
        NoteText.setText(((Note) someNote).getNoteText());
        noteCreationTime.setText(df.format("dd.MM.yyyy kk:mm:ss",((Note) someNote).getCreationTime()).toString());


    }

    @Override
    protected void onPause() {
        super.onPause();
        saveNote();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveNote();
    }


    synchronized void saveNote(){
        if(NoteText.getText().toString().trim().length()>0||
                NoteName.getText().toString().trim().length()>0){
            if(someNote instanceof Birthday){
                newBirthday.setNoteText(String.valueOf(NoteText.getText()));
                newBirthday.setNoteName(String.valueOf(NoteName.getText()));
                newBirthday.setBirthdayTime(date);
                AppDataBase db = App.getDatabase();
                checkAndSave(db.getBirthdayDao(),newBirthday);
            } else if(someNote instanceof Event){
                newEvent.setNoteText(String.valueOf(NoteText.getText()));
                newEvent.setNoteName(String.valueOf(NoteName.getText()));
                AppDataBase db = App.getDatabase();
                checkAndSave(db.getEventDao(),newEvent);
            } else if(someNote instanceof Reminder){
                newReminder.setNoteText(String.valueOf(NoteText.getText()));
                newReminder.setNoteName(String.valueOf(NoteName.getText()));
                newReminder.setEventTime(date);
                AppDataBase db = App.getDatabase();
                checkAndSave(db.getReminderDao(),newReminder);
            } else if(someNote instanceof Note){
                currentNote.setNoteText(String.valueOf(NoteText.getText()));
                currentNote.setNoteName(String.valueOf(NoteName.getText()));
                AppDataBase db = App.getDatabase();
                checkAndSave(db.getNoteDao(),currentNote);
            }
        }
    }

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);
            date =  calendar.getTime();
        }
    };

    public <T extends TemplateDao<N>,N extends Note> void checkAndSave(T selectedNoteDataBase, N editingNote){
        selectedNoteDataBase.getById(editingNote.getNoteId());
        if(selectedNoteDataBase.getById(editingNote.getNoteId())!=null&&
                !selectedNoteDataBase.getById(editingNote.getNoteId()).equals(editingNote)){
            selectedNoteDataBase.delete(selectedNoteDataBase.getById(editingNote.getNoteId()));
            selectedNoteDataBase.insertAll(editingNote);
        } else if(selectedNoteDataBase.getById(editingNote.getNoteId())==null){
            selectedNoteDataBase.insertAll(editingNote);
        }
    }


}