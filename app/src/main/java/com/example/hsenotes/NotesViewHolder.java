package com.example.hsenotes;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

public class NotesViewHolder {

    static class NotesHolder extends RecyclerView.ViewHolder {
        TextView textInfo;
        TextView noteName;
        MainActivity ma;
        ConstraintLayout noteLayout;
        MaterialCardView materialCardView;

        private static final String TAG = "VHFactory";


        public NotesHolder(@NonNull View itemView, @NonNull Context context, IListener listener) {
            super(itemView);
            textInfo = itemView.findViewById(R.id.note_text);
            noteName = itemView.findViewById(R.id.note_name);
            noteLayout = itemView.findViewById(R.id.note_layout);
            materialCardView = itemView.findViewById(R.id.device_item_card_view);

            if (context instanceof Activity) {
                ma = (MainActivity) context;
            }
            itemView.setOnClickListener(v -> {
                listener.onNoteClicked(getAdapterPosition(), itemView);
            });
            itemView.setOnLongClickListener(v -> {
                listener.onNoteLongClicked(getAdapterPosition(), itemView);
                return true;
            });
        }

        interface IListener {
            void onNoteClicked(int id, View itemView);

            void onNoteLongClicked(int id, View itemView);
        }

    }

    public static RecyclerView.ViewHolder create(ViewGroup parent, int viewType, Context context, NotesHolder.IListener listener) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View layout = inflater.inflate(R.layout.note_item, parent, false);
        return new NotesHolder(layout, context, listener);
    }
}
