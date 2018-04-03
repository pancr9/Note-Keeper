package builderspace.notekeeper.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import builderspace.notekeeper.R;
import builderspace.notekeeper.model.Note;
import builderspace.notekeeper.view.RecyclerAdapter;

public class MainActivity extends AppCompatActivity {

    private final HashMap<String, Integer> priorityMap = new HashMap<String, Integer>() {
        {
            put("High", 1);
            put("Medium", 2);
            put("Low", 3);
        }
    };
    private RecyclerView recycler;
    private EditText etNotes;
    private Spinner spPriority;
    private List<Note> notesList;
    private Note note;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mNoteRef = mRootRef.child("Notes");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnAdd = findViewById(R.id.btnAdd);

        etNotes = findViewById(R.id.etNote);
        recycler = findViewById(R.id.recylerNotes);
        recycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        spPriority = findViewById(R.id.spPriority);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.priority_map, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPriority.setAdapter(adapter);

        notesList = new ArrayList<>();

        Collections.sort(notesList, new CompareByStatus());

        btnAdd.setOnClickListener(v -> {
            if (IsNullEmpty(etNotes.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Please Add a Note", Toast.LENGTH_SHORT).show();
            } else {
                String priority = spPriority.getSelectedItem().toString();
                if (priority.equals("Priority")) {

                    Toast.makeText(getApplicationContext(), "Please set priority", Toast.LENGTH_SHORT).show();
                    return;
                }

                note = new Note();
                note.setTaskNote(etNotes.getText().toString());

                //Default settings.
                etNotes.setText("");
                spPriority.setSelection(0);

                note.setPriority(priorityMap.get(priority));
                note.setStatus("pending");
                note.setCreatedTime(Calendar.getInstance().getTime());
                saveNote(note);
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popup_menu, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mNoteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                notesList = new ArrayList<>();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Note post = postSnapshot.getValue(Note.class);
                    notesList.add(post);
                }
                fillAppsList(sortNotes(notesList));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void saveNote(Note note) {
        note.setId(mNoteRef.push().getKey());
        mNoteRef.child(note.getId()).setValue(note);
    }

    private void deleteNote(Note note) {
        mNoteRef.child(note.getId()).removeValue();
    }

    private List<Note> sortNotes(List<Note> noteList) {
        List<Note> completedNotes = new ArrayList<>();
        List<Note> pendingNotes = new ArrayList<>();
        List<Note> finalList = new ArrayList<>();

        for (Note note : noteList) {
            if (note.getStatus().equals("completed"))
                completedNotes.add(note);
        }

        for (Note note : noteList) {
            if (note.getStatus().equals("pending"))
                pendingNotes.add(note);
        }

        Collections.sort(pendingNotes, new CompareByStatus());
        Collections.sort(completedNotes, new CompareByStatus());
        finalList.addAll(pendingNotes);
        finalList.addAll(completedNotes);
        return finalList;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.show_all:
                Collections.sort(notesList, new CompareByStatus());

                fillAppsList(notesList);
                break;
            case R.id.show_completed:
                List<Note> completedNotes = new ArrayList<>();

                for (Note note : notesList) {
                    if (note.getStatus().equals("completed"))
                        completedNotes.add(note);
                }


                fillAppsList(completedNotes);
                break;
            case R.id.show_pending:
                List<Note> pendingNotes = new ArrayList<>();

                for (Note note : notesList) {
                    if (note.getStatus().equals("pending"))
                        pendingNotes.add(note);
                }

                fillAppsList(pendingNotes);
                break;
            case R.id.sort_by_priority:
                Collections.sort(notesList, new CompareByPriority());
                fillAppsList(notesList);
                break;
            case R.id.sort_by_time:
                Collections.sort(notesList, new CompareByTime());
                fillAppsList(notesList);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fillAppsList(final List<Note> notesList) {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(mLayoutManager);
        RecyclerAdapter notesAdapter = new RecyclerAdapter(this, notesList);
        recycler.setAdapter(notesAdapter);

    }

    private Boolean IsNullEmpty(String str) {
        return (str == null || str.isEmpty());
    }

    private class CompareByStatus implements Comparator<Note> {
        @Override
        public int compare(Note o1, Note o2) {
            return (o1.getStatus().equals(o2.getStatus())) ? 0 : (o1.getStatus().equals("pending")) ? -1 : 1;
        }
    }

    private class CompareByPriority implements Comparator<Note> {
        @Override
        public int compare(Note o1, Note o2) {
            return (o1.getPriority() == o2.getPriority()) ? 0 : (o1.getPriority() < o2.getPriority()) ? -1 : 1;
        }
    }

    private class CompareByTime implements Comparator<Note> {
        @Override
        public int compare(Note o1, Note o2) {
            return (o1.getCreatedTime() == o2.getCreatedTime()) ? 0 : (o1.getCreatedTime().after(o2.getCreatedTime())) ? -1 : 1;
        }
    }

}
