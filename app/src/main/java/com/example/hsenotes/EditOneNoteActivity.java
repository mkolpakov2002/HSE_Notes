package com.example.hsenotes;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class EditOneNoteActivity extends AppCompatActivity {
    private Object someNote;
    private Note currentNote;
    private Birthday newBirthday;
    private Event newEvent;
    private Reminder newReminder;
    private EditText NoteName;
    private EditText NoteText;

    private Date creationDate;
    private ChipGroup chipGroup;
    private SwitchMaterial reminderAlarmButton;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;

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

        alarmManager = (AlarmManager)getApplicationContext().
                getSystemService(Context.ALARM_SERVICE);

        Intent intent = getIntent();
        someNote = intent.getSerializableExtra("noteObject");
        if(someNote==null){
            someNote = new Note();
        }
        NoteName = findViewById(R.id.note_name_title_edit);
        NoteText = findViewById(R.id.note_text_edit);

        ScrollView scrollView = findViewById(R.id.edit_one_note_scroll_layout);
        GestureDetector gestureDetector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }

                });
        scrollView.setOnTouchListener((v, event) -> {
            if (gestureDetector.onTouchEvent(event)) {
                NoteText.requestFocus();
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(NoteText, InputMethodManager.SHOW_FORCED);
            }
            return false;
        });

        LinearLayout creationTime = findViewById(R.id.creation_time_layout);
        creationTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackbar = Snackbar
                        .make(scrollView, "Время создания изменить нельзя",
                                Snackbar.LENGTH_LONG)
                        .setAction("Ок", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //nothing
                            }
                        });
                snackbar.show();
            }
        });

        TextView noteCreationTime = findViewById(R.id.creation_time_edit);
        creationDate = Calendar.getInstance().getTime();

        if (((Note) someNote).getCreationTime() == null) {
            creationDate = new Date();
        } else {
            creationDate = ((Note) someNote).getCreationTime();
        }
        String currentDateTimeString = DateFormat.getDateTimeInstance().
                format(creationDate);
        noteCreationTime.setText(currentDateTimeString);
        if (someNote instanceof Birthday) {
            TextView datePickedText = findViewById(R.id.birthday_date_text);
            ConstraintLayout datePicker = findViewById(R.id.birthday_linear_layout);
            datePickedText.setText(android.text.format.DateFormat.format(
                    "dd.MM.yyyy", new Date()).toString());
            datePicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                    //builder.setTheme(R.style.Theme_Material3_DayNight_Dialog_Alert);
                    builder.setSelection(creationDate.getTime());
                    MaterialDatePicker<Long> picker = builder.build();
                    picker.addOnPositiveButtonClickListener(selection -> {

                        Date date = new Date(selection);
                        date.setHours(0);
                        datePickedText.setText(android.text.format.DateFormat.format(
                                "dd.MM.yyyy", date).toString());
                        newBirthday.setBirthdayTime(date);

                    });
                    picker.show(getSupportFragmentManager(), picker.toString());
                }
            });
            newBirthday = (Birthday) someNote;
            newBirthday.setCreationTime(creationDate);
            newBirthday.setBirthdayTime(creationDate);
            datePicker.setVisibility(View.VISIBLE);
        } else if (someNote instanceof Event) {
            LinearLayout emailSelection = findViewById(R.id.event_linear_layout);
            chipGroup = findViewById(R.id.chip_group);
            EditText peopleList = findViewById(R.id.editTextTextEmailAddress);
            peopleList.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        // Your action on done
                        if (isEmailValid(peopleList.getText().toString())) {
                            addChip(chipGroup, peopleList.getText().toString(), true);
                            newEvent.addToPeopleList(peopleList.getText().toString());
                            peopleList.setText("");
                        }
                        return true;
                    }
                    return false;
                }
            });
            peopleList.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.toString().trim().length() > 0 && !isEmailValid(editable)) {
                        peopleList.setError("Некорректный email");
                    }

                }
            });
            newEvent = (Event) someNote;
            newEvent.setCreationTime(creationDate);
            emailSelection.setVisibility(View.VISIBLE);
            if (newEvent.getPeople() != null) {
                for (String item : newEvent.getPeople()) {
                    addChip(chipGroup, item, false);
                }
            }
        } else if (someNote instanceof Reminder) {
            findViewById(R.id.constraintLayoutReminderDate).setVisibility(View.VISIBLE);
            TextView reminderTimePickedText = findViewById(R.id.reminder_time_text);
            TextView reminderDatePickedText = findViewById(R.id.reminder_time_date);
            reminderAlarmButton = findViewById(R.id.switch1);
            ConstraintLayout reminderTimePicker = findViewById(R.id.constraintLayoutReminderTime);
            reminderTimePicker.setVisibility(View.GONE);
            int clockFormat;
            if (android.text.format.DateFormat.is24HourFormat(this))
                clockFormat = TimeFormat.CLOCK_24H;
            else clockFormat = TimeFormat.CLOCK_12H;
            reminderTimePicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Date date = newReminder.getEventTime();
                    MaterialTimePicker.Builder builder = new MaterialTimePicker.Builder();
                    builder.setHour(date.getHours());
                    builder.setTimeFormat(clockFormat);
                    MaterialTimePicker picker = builder.build();

                    picker.addOnPositiveButtonClickListener(selection -> {
                        date.setHours(picker.getHour());
                        date.setMinutes(picker.getMinute());
                        reminderTimePickedText.setText(
                                android.text.format.DateFormat.format(
                                        "kk:mm", date).toString());
                        newReminder.setEventTime(date);

                    });
                    picker.show(getSupportFragmentManager(), picker.toString());
                }
            });

            ConstraintLayout reminderDatePicker = findViewById(R.id.constraintLayoutReminderDate);
            reminderDatePicker.setVisibility(View.GONE);
            reminderDatePicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                    builder.setSelection(creationDate.getTime());
                    MaterialDatePicker<Long> picker = builder.build();
                    picker.addOnPositiveButtonClickListener(selection -> {
                        Date date = new Date(selection);
                        date.setHours(12);
                        reminderDatePickedText.setText(
                                android.text.format.DateFormat.format(
                                        "dd.MM.yyyy", date).toString());
                        reminderTimePicker.setVisibility(View.VISIBLE);
                        reminderTimePickedText.setText(
                                android.text.format.DateFormat.format(
                                        "kk:mm", date).toString());
                        newReminder.setEventTime(date);
                        reminderAlarmButton.setVisibility(View.VISIBLE);
                    });
                    picker.show(getSupportFragmentManager(), picker.toString());
                }
            });

            reminderAlarmButton.setUseMaterialThemeColors(true);
            reminderAlarmButton.setOnCheckedChangeListener((compoundButton, b) -> {
                if(b){
                    setAlarm();
                } else {
                    cancelAlarm();
                }
            });
            newReminder = (Reminder) someNote;
            newReminder.setCreationTime(creationDate);

            if(newReminder.getEventTime()!=null){
                reminderTimePicker.setVisibility(View.VISIBLE);
                reminderAlarmButton.setVisibility(View.VISIBLE);
                reminderDatePickedText.setText(
                        android.text.format.DateFormat.format(
                                "dd.MM.yyyy", newReminder.getEventTime()).toString());
                reminderTimePickedText.setText(
                        android.text.format.DateFormat.format(
                                "kk:mm", newReminder.getEventTime()).toString());
            }

            reminderDatePicker.setVisibility(View.VISIBLE);
        } else {
            currentNote = (Note) someNote;
            currentNote.setCreationTime(creationDate);
        }

        NoteName.setText(((Note) someNote).getNoteName());
        NoteText.setText(((Note) someNote).getNoteText());
        noteCreationTime.setText(
                android.text.format.DateFormat.format("dd.MM.yyyy kk:mm:ss",
                        ((Note) someNote).getCreationTime()).toString());
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


    synchronized void saveNote() {
        if (NoteText.getText().toString().trim().length() > 0 ||
                NoteName.getText().toString().trim().length() > 0) {
            if (someNote instanceof Birthday) {
                newBirthday.setNoteText(String.valueOf(NoteText.getText()));
                newBirthday.setNoteName(String.valueOf(NoteName.getText()));
                AppDataBase db = App.getDatabase();
                BirthdayDao devicesDao = db.getBirthdayDao();
                devicesDao.insertAll(newBirthday);
            } else if (someNote instanceof Event) {
                newEvent.setNoteText(String.valueOf(NoteText.getText()));
                newEvent.setNoteName(String.valueOf(NoteName.getText()));
                AppDataBase db = App.getDatabase();
                EventDao devicesDao = db.getEventDao();
                devicesDao.insertAll(newEvent);
            } else if (someNote instanceof Reminder) {
                newReminder.setNoteText(String.valueOf(NoteText.getText()));
                newReminder.setNoteName(String.valueOf(NoteName.getText()));
                AppDataBase db = App.getDatabase();
                ReminderDao devicesDao = db.getReminderDao();
                devicesDao.insertAll(newReminder);
            } else if (someNote instanceof Note) {
                currentNote.setNoteText(String.valueOf(NoteText.getText()));
                currentNote.setNoteName(String.valueOf(NoteName.getText()));
                AppDataBase db = App.getDatabase();
                NoteDao devicesDao = db.getNoteDao();
                devicesDao.insertAll(currentNote);
            }
        } else {
            if (someNote instanceof Birthday) {
                AppDataBase db = App.getDatabase();
                BirthdayDao devicesDao = db.getBirthdayDao();
                devicesDao.delete(newBirthday);
            } else if (someNote instanceof Event) {
                AppDataBase db = App.getDatabase();
                EventDao devicesDao = db.getEventDao();
                devicesDao.delete(newEvent);
            } else if (someNote instanceof Reminder) {
                cancelAlarm();
                AppDataBase db = App.getDatabase();
                ReminderDao devicesDao = db.getReminderDao();
                devicesDao.delete(newReminder);
            } else if (someNote instanceof Note) {
                AppDataBase db = App.getDatabase();
                NoteDao devicesDao = db.getNoteDao();
                devicesDao.delete(currentNote);
            }
        }
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    void addChip(ChipGroup chipGroup, String text, Boolean isNeedToSave) {
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chipGroup.removeView(chip);
                newEvent.deleteFromPeopleList(chip.getText().toString());
            }
        });
        chipGroup.addView(chip);
        if (isNeedToSave)
            newEvent.addToPeopleList(chip.getText().toString());
    }

    public void setAlarm() {
        long time;
        Toast.makeText(EditOneNoteActivity.this, "ALARM ON", Toast.LENGTH_SHORT).show();
        Calendar calendar = Calendar.getInstance();

        // calendar is called to get current time in hour and minute
        calendar.set(Calendar.DATE, newReminder.getEventTime().getDate());
        calendar.set(Calendar.HOUR_OF_DAY, newReminder.getEventTime().getHours());
        calendar.set(Calendar.MINUTE, newReminder.getEventTime().getMinutes());


        // get a Calendar at the current time
        Calendar now = Calendar.getInstance();

        if (now.before(calendar)) {
            // it's not 14:00 yet, start today
            time = calendar.getTimeInMillis();
        } else {
            // start 14:00 tomorrow
            calendar.add(Calendar.DATE, 1);
            time = calendar.getTimeInMillis();
        }

        // using intent i have class AlarmReceiver class which inherits
        // BroadcastReceiver
        Intent intent = new Intent(getApplicationContext(), EditOneNoteActivity.class);

        // we call broadcast using pendingIntent
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        // Alarm rings continuously until toggle button is turned off
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);

    }

    public void cancelAlarm() {
        if(pendingIntent!=null){
            alarmManager.cancel(pendingIntent);
        }
    }

}