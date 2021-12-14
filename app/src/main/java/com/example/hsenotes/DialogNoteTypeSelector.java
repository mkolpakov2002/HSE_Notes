package com.example.hsenotes;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(c);
        ArrayAdapter<String> adapterType = new ArrayAdapter<String>(c, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.TYPES_LIST));
        builder.setSingleChoiceItems(adapterType, 0, (dialogInterface, i) -> {
            String noteType = getResources().getStringArray(R.array.TYPES_LIST)[i];
            Intent intent = new Intent(c, EditOneNoteActivity.class);
            switch (i) {
                case 0:
                    Birthday newBirthday = new Birthday();
                    newBirthday.setNoteId(ma.getArraySize());
                    intent.putExtra("noteObject", newBirthday);
                    break;
                case 1:
                    Note newNote = new Note();
                    newNote.setNoteId(ma.getArraySize());
                    intent.putExtra("noteObject", newNote);
                    break;
                case 2:
                    Event newEvent = new Event();
                    newEvent.setNoteId(ma.getArraySize());
                    intent.putExtra("noteObject", newEvent);
                    break;
                case 3:
                    Reminder newReminder = new Reminder();
                    newReminder.setNoteId(ma.getArraySize());
                    intent.putExtra("noteObject", newReminder);
                    break;
            }
            dialogInterface.dismiss();
            c.startActivity(intent);
        });
        builder.setTitle(c.getResources().getString(R.string.dialog_selection));
        builder.setNegativeButton(c.getResources().getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });


        return builder.create();
    }


}