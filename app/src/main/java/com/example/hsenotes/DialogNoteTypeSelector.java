package com.example.hsenotes;

import static com.example.hsenotes.Constants.TYPES_LIST;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class DialogNoteTypeSelector extends DialogFragment {

    private Context c;
    private Spinner spinnerType;
    private MainActivity ma;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity){
            ma = (MainActivity) context;
        }
        c = context;
    }

    public DialogNoteTypeSelector(){
        //nothing
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_device, null);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(c);
        builder.setView(dialogView);

        ArrayAdapter<String> adapterType = new ArrayAdapter<String>(c, android.R.layout.simple_spinner_item, TYPES_LIST);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType = dialogView.findViewById(R.id.spinner_type);
        spinnerType.setAdapter(adapterType);


        MaterialButton buttonToCancelChanges = dialogView.findViewById(R.id.dialog_edit_cancel);
        MaterialButton buttonToSaveChanges = dialogView.findViewById(R.id.dialog_edit_save);

        AlertDialog alertDialog = builder.create();

        buttonToCancelChanges.setOnClickListener(view -> {
            alertDialog.dismiss();
        });

        buttonToSaveChanges.setOnClickListener(view -> {
            String noteType = TYPES_LIST[(int) spinnerType.getSelectedItemId()];
            Intent intent = new Intent(c, EditOneNoteActivity.class);
            switch (noteType) {
                case "Note":
                    Note newNote = new Note();
                    newNote.setNoteId(ma.getArraySize());
                    intent.putExtra("noteObject", newNote);
                    break;
                case "Birthday":
                    Birthday newBirthday = new Birthday();
                    newBirthday.setNoteId(ma.getArraySize());
                    intent.putExtra("noteObject", newBirthday);
                    break;
                case "Event":
                    Event newEvent = new Event();
                    newEvent.setNoteId(ma.getArraySize());
                    intent.putExtra("noteObject", newEvent);
                    break;
                case "Reminder":
                    Reminder newReminder = new Reminder();
                    newReminder.setNoteId(ma.getArraySize());
                    intent.putExtra("noteObject", newReminder);
                    break;
            }
            alertDialog.dismiss();
            c.startActivity(intent);
        });
        return alertDialog;
    }


}