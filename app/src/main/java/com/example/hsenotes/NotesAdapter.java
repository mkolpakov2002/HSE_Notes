package com.example.hsenotes;

import static com.example.hsenotes.Constants.APP_LOG_TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements NotesViewHolder.NotesHolder.IListener, Filterable {
    private MainActivity ma;
    private final Context context;
    private List<Note> dataSet;
    private List<Note> allNotes;
    private final NoteClickedListener listener;
    private final List<Note> selectedNotesList;
    private List<Note> notesListFiltered;

    public NotesAdapter(List<Note> dataSet, @NonNull Context context) {
        if (context instanceof MainActivity) {
            ma = (MainActivity) context;
        }
        this.context = context;
        this.dataSet = new ArrayList<>(dataSet);
        selectedNotesList = new ArrayList<>();
        allNotes = notesListFiltered = this.dataSet;
        listener = new MyListener();

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return Objects.requireNonNull(NotesViewHolder.create(parent, viewType, context, this));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        dataSet.get(position).onBindViewHolder(holder);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @Override
    public void onNoteClicked(int id, View itemView) {
        listener.noteClicked(dataSet.get(id), itemView);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            List<Note> resultData;

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();

                if (charSequence == null || charSequence.length() == 0) {
                    filterResults.count = notesListFiltered.size();
                    filterResults.values = notesListFiltered;

                } else {
                    String searchChr = charSequence.toString().toLowerCase();

                    resultData = new ArrayList<>();

                    for (Note userModel : notesListFiltered) {
                        if (userModel.getNoteText().toLowerCase().contains(searchChr) ||
                                userModel.getNoteName().toLowerCase().contains(searchChr)) {
                            resultData.add(userModel);
                        }
                    }
                    filterResults.count = resultData.size();
                    filterResults.values = resultData;

                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                showFiltered(resultData);
            }
        };
    }

    public interface NoteClickedListener {
        void noteClicked(Note item, View itemView);

        void noteLongClicked(Note item, View itemView);
    }

    @Override
    public void onNoteLongClicked(int id, View itemView) {
        listener.noteLongClicked(dataSet.get(id), itemView);
    }

    public void onNewData(List<Note> newDataSet) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ItemsDiffUtilCallBack(newDataSet, dataSet), true);
        this.dataSet = notesListFiltered = allNotes = newDataSet;
        diffResult.dispatchUpdatesTo(this);
    }

    public void showFiltered(List<Note> newDataSet) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ItemsDiffUtilCallBack(newDataSet, dataSet), true);
        this.dataSet = newDataSet;
        diffResult.dispatchUpdatesTo(this);
    }

    public class MyListener implements NoteClickedListener {

        @Override
        public void noteClicked(Note item, View itemView) {

            MaterialCardView materialCardView = itemView.findViewById(R.id.device_item_card_view);

            //проверяю происходит ли выбор списка устройств
            if (selectedNotesList.size() != 0) {
                Log.d(APP_LOG_TAG, "...Список не пуст, нажато устройство...");
                //список не пуст
                //необходимо проверить на присутствие в списке
                boolean wasAlreadySelected = false;
                for (Note currentDevice : selectedNotesList) {
                    if (currentDevice.getNoteId() == (item.getNoteId())) {
                        selectedNotesList.remove(currentDevice);
                        wasAlreadySelected = true;
                        Log.d(APP_LOG_TAG, "...В списке нашлось это устройство, удаляю...");
                        materialCardView.setStrokeColor(Color.TRANSPARENT);
                        item.setIsSelectedOnScreen(false);
                        break;
                    }
                }

                if (!wasAlreadySelected) {
                    Log.d(APP_LOG_TAG, "...В списке не нашлось это устройство, добавляю...");
                    selectedNotesList.add(item);
                    item.setIsSelectedOnScreen(true);
                    ma.showDeviceSelectedItems();
                    materialCardView.setStrokeColor(ContextCompat.getColor(ma, R.color.colorAccent));

                } else {
                    if (selectedNotesList.size() == 0) {
                        Log.d(APP_LOG_TAG, "...Список очищен...");
                        ma.hideDeviceSelectedItems();
                    }
                }
                if (selectedNotesList.size() == 1) {
                    ma.showSendAction();
                } else {
                    ma.hideSendAction();
                }
            } else {
                Log.d(APP_LOG_TAG, "...Список пуст, окно заметки...");
                Intent intent = new Intent(context, EditOneNoteActivity.class);
                if (ma.itemIsBirthday(item.getNoteId())) {
                    BirthdayDao birthdayDao = App.getDatabase().getBirthdayDao();
                    Birthday note = birthdayDao.getById(item.getNoteId());
                    intent.putExtra("noteObject", note);
                } else if (ma.itemIsEvent(item.getNoteId())) {
                    EventDao eventDao = App.getDatabase().getEventDao();
                    Event note = eventDao.getById(item.getNoteId());
                    intent.putExtra("noteObject", note);
                } else if (ma.itemIsReminder(item.getNoteId())) {
                    ReminderDao reminderDao = App.getDatabase().getReminderDao();
                    Reminder note = reminderDao.getById(item.getNoteId());
                    intent.putExtra("noteObject", note);
                } else intent.putExtra("noteObject", item);

                context.startActivity(intent);
            }
        }

        @Override
        public void noteLongClicked(Note item, View itemView) {
            MaterialCardView materialCardView = itemView.findViewById(R.id.device_item_card_view);
            if (selectedNotesList.size() == 0) {
                Log.d(APP_LOG_TAG, "...Список пуст, добавляю устройство...");
                selectedNotesList.add(item);
                item.setIsSelectedOnScreen(true);
                ma.showDeviceSelectedItems();
                materialCardView.setStrokeColor(ContextCompat.getColor(ma, R.color.colorAccent));

            } else {
                boolean wasAlreadySelected = false;
                for (int i = 0; i < selectedNotesList.size(); i++) {
                    if (selectedNotesList.get(i).getNoteId() == (item.getNoteId())) {
                        wasAlreadySelected = true;
                    }
                }
                if (!wasAlreadySelected) {
                    selectedNotesList.add(item);
                    item.setIsSelectedOnScreen(true);
                    materialCardView.setStrokeColor(ContextCompat.getColor(ma, R.color.colorAccent));
                }
            }
            if (selectedNotesList.size() == 1) {
                ma.showSendAction();
            } else {
                ma.hideSendAction();
            }
        }
    }

    public void clearSelected() {
        selectedNotesList.clear();
    }

    public int getArraySize() {
        return allNotes.size();
    }

    public List<Note> getSelectedNotes() {
        return selectedNotesList;
    }

    public Note getByIndex(int index) {
        return allNotes.get(index);
    }

}

