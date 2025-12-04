package com.example.databases4411_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databases4411_finalproject.data.DBHelper;
import com.example.databases4411_finalproject.model.Note;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DBHelper dbHelper;
    RecyclerView recyclerView;
    EditText etSearch;

    NoteAdapter adapter;
    Button btnAddNote;
    List<Note> notes; // Keep reference to the list

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        btnAddNote = findViewById(R.id.btnAddNote);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the list and adapter once
        notes = new ArrayList<>();
        adapter = new NoteAdapter(notes, dbHelper, this);
        recyclerView.setAdapter(adapter);

        loadNotes();

        btnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NoteEditorActivity.class);
                startActivity(intent);
            }
        });

        etSearch = findViewById(R.id.etSearch);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String q = s.toString().trim();

                if (q.isEmpty()) {
                    loadNotes(); // show all notes
                } else {
                    notes.clear();
                    notes.addAll(dbHelper.searchNotes(q));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

    }

    private void loadNotes() {
        notes.clear(); // Clear the existing list
        notes.addAll(dbHelper.getAllNotes()); // Add all notes from DB
        adapter.notifyDataSetChanged(); // Notify adapter of changes
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes(); // Refresh list after returning from editor
    }


}