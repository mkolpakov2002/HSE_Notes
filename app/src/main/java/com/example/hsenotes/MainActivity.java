package com.example.hsenotes;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private FloatingActionButton fabToDelete;
    private FloatingActionButton fabToSend;
    private FloatingActionButton fabToAdd;
    private RecyclerView recycler;
    private SwipeRefreshLayout swipeToRefreshLayout;
    private NotesAdapter adapter = null;
    SearchView searchView;
    List<Note> newNotesList;
    List<Birthday> newBirthdayList;
    List<Event> newEventList;
    List<Reminder> newReminderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sPref = getPreferences(MODE_PRIVATE);
        int isFirstLaunch = sPref.getInt("isFirstLaunch", 1);

        MaterialToolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);


        fabToDelete = findViewById(R.id.floating_action_button_delete);
        fabToDelete.setOnClickListener(v -> {

            for(Note selectedNote: adapter.getSelectedNotes()){
                if(itemIsBirthday(selectedNote.getNoteId())){
                    BirthdayDao birthdayDao = App.getDatabase().getBirthdayDao();
                    birthdayDao.delete(selectedNote.getNoteId());
                } else if(itemIsEvent(selectedNote.getNoteId())){
                    EventDao eventDao = App.getDatabase().getEventDao();
                    eventDao.delete(selectedNote.getNoteId());
                } else if(itemIsReminder(selectedNote.getNoteId())){
                    ReminderDao reminderDao = App.getDatabase().getReminderDao();
                    reminderDao.delete(selectedNote.getNoteId());
                } else {
                    NoteDao devicesDao = App.getDatabase().getNoteDao();
                    devicesDao.delete(selectedNote.getNoteId());
                }

            }
            onRefresh();
        });
        fabToDelete.hide();

        fabToSend = findViewById(R.id.floating_action_button_send);
        fabToSend.setOnClickListener(v -> {
            Note selectedNote = adapter.getSelectedNotes().get(adapter.getSelectedNotes().size()-1);
            String selectedNoteText;
            if(selectedNote.getNoteName().trim().length()==0 ||
                    selectedNote.getNoteText().trim().length()==0){
                selectedNoteText = selectedNote.getNoteName() + selectedNote.getNoteText();
            } else {
                selectedNoteText = selectedNote.getNoteName()
                        + System.lineSeparator()
                        + selectedNote.getNoteText();
            }
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT,selectedNoteText);
            shareIntent.setType("text/plain");
            startActivity(shareIntent);
        });
        fabToSend.hide();

        fabToAdd = findViewById(R.id.floating_action_button_add);
        fabToAdd.setOnClickListener(v -> {
            DialogFragment noteTypeDialog = new DialogNoteTypeSelector();
            noteTypeDialog.show(getSupportFragmentManager(),"typeSelection");
            //Intent intent = new Intent(v.getContext(), EditOneNoteActivity.class);
            //v.getContext().startActivity(intent);
        });

        swipeToRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeToRefreshLayout.setOnRefreshListener(this);
        recycler = findViewById(R.id.notes_list);
        StaggeredGridLayoutManager gridLayoutManager;
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // code for portrait mode
            gridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        } else {
            // code for landscape mode
            gridLayoutManager = new StaggeredGridLayoutManager( 3, LinearLayoutManager.VERTICAL);
        }
        recycler.setLayoutManager(gridLayoutManager);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.END) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition(); //get position which is swipe

                if (direction == ItemTouchHelper.END) {    //if swipe left

                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this); //alert for confirm to delete
                    builder.setMessage("Are you sure to delete?");    //set message

                    builder.setPositiveButton("REMOVE", new DialogInterface.OnClickListener() { //when click on DELETE
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adapter.notifyItemRemoved(position);    //item removed from recylcerview
                            Note note = adapter.getByIndex(position);
                            if(itemIsBirthday(note.getNoteId())){
                                BirthdayDao birthdayDao = App.getDatabase().getBirthdayDao();
                                birthdayDao.delete(note.getNoteId());
                            } else if(itemIsEvent(note.getNoteId())){
                                EventDao eventDao = App.getDatabase().getEventDao();
                                eventDao.delete(note.getNoteId());
                            } else if(itemIsReminder(note.getNoteId())){
                                ReminderDao reminderDao = App.getDatabase().getReminderDao();
                                reminderDao.delete(note.getNoteId());
                            } else {
                                NoteDao devicesDao = App.getDatabase().getNoteDao();
                                devicesDao.delete(note.getNoteId());
                            }
                            onRefresh();

                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onRefresh();
                        }
                    }).show();  //show alert dialog
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recycler); //set swipe to recylcerview

        if (isFirstLaunch == 1) {
            sPref = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putInt("isFirstLaunch", 0);
            ed.apply();
        }

    }

    public void hideDeviceSelectedItems(){
        fabToDelete.hide();
        invalidateOptionsMenu();
    }

    public void showDeviceSelectedItems(){
        fabToDelete.show();
        invalidateOptionsMenu();
    }

    public void showSendAction(){
        fabToSend.show();
    }

    public void hideSendAction(){
        fabToSend.hide();
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        fabToDelete.hide();
        fabToSend.hide();
        NoteDao notesDao = App.getDatabase().getNoteDao();
        newNotesList = new ArrayList<>(notesDao.getAll());
        BirthdayDao birthdayDao = App.getDatabase().getBirthdayDao();
        newBirthdayList = new ArrayList<>(birthdayDao.getAll());
        EventDao eventDao = App.getDatabase().getEventDao();
        newEventList = new ArrayList<>(eventDao.getAll());
        ReminderDao reminderDao = App.getDatabase().getReminderDao();
        newReminderList = new ArrayList<>(reminderDao.getAll());
        newNotesList.addAll(newBirthdayList);
        newNotesList.addAll(newEventList);
        newNotesList.addAll(newReminderList);
        if (adapter == null){
            adapter = new NotesAdapter(newNotesList,this);
            recycler.setAdapter(adapter);
        } else {
            adapter.clearSelected();
            adapter.onNewData(newNotesList);
        }
        swipeToRefreshLayout.setRefreshing(false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                adapter.getFilter().filter(query);
                return false;
            }
        });
        if(adapter.getSelectedNotes()!=null&&adapter.getSelectedNotes().size()>0){
            menu.findItem(R.id.action_search).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    public int getArraySize(){
        return adapter.getArraySize();
    }

    public Boolean itemIsBirthday(int noteId) {
        return newBirthdayList.stream().filter(note -> note.getNoteId() == noteId).findFirst().orElse(null) != null;
    }

    public Boolean itemIsEvent(int noteId) {
        return newEventList.stream().filter(note -> note.getNoteId() == noteId).findFirst().orElse(null) != null;
    }

    public Boolean itemIsReminder(int noteId) {
        return newReminderList.stream().filter(note -> note.getNoteId() == noteId).findFirst().orElse(null) != null;
    }

}