package com.example.databases4411_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
    Button btnTest; // NEW
    List<Note> notes; // Keep reference to the list

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        btnAddNote = findViewById(R.id.btnAddNote);
        btnTest = findViewById(R.id.btnTest); // NEW

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the list and adapter once
        notes = new ArrayList<>();
        adapter = new NoteAdapter(notes, dbHelper, this);
        recyclerView.setAdapter(adapter);

        //dbHelper.clearDatabase();
        loadNotes();

        // Add Note Button
        btnAddNote.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NoteEditorActivity.class);
            startActivity(intent);
        });

        // Search bar
        etSearch = findViewById(R.id.etSearch);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String q = s.toString().trim();

                if (q.isEmpty()) {
                    loadNotes();
                } else {
                    notes.clear();
                    notes.addAll(dbHelper.searchSlow(q));  // Using SLOW search
                    adapter.notifyDataSetChanged();
                }
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        // ================================
        //   BENCHMARK TEST BUTTON LOGIC
        // ================================
        btnTest.setOnClickListener(v -> {
            String testQuery = "a"; // choose any sample query

            // Time slow search
            long slowTime = dbHelper.timeQuery(() -> dbHelper.searchSlow(testQuery));

            // Time fast search
            long fastTime = dbHelper.timeQuery(() -> dbHelper.searchFast(testQuery));

            // Log results
            Log.d("BENCHMARK", "Slow Search Time: " + (slowTime / 1_000_000) + " ms");
            Log.d("BENCHMARK", "Fast Search Time: " + (fastTime / 1_000_000) + " ms");

            // Toast results to screen
            Toast.makeText(
                    MainActivity.this,
                    "Slow: " + (slowTime / 1_000_000) + " ms\nFast: " + (fastTime / 1_000_000) + " ms",
                    Toast.LENGTH_LONG
            ).show();

            // Query Plan for Slow
            Log.d("BENCHMARK", "=== SLOW QUERY PLAN ===");
            dbHelper.explainQueryPlan(
                    "SELECT * FROM notes WHERE title LIKE '%" + testQuery + "%' OR content LIKE '%" + testQuery + "%'",
                    null
            );

            // Query Plan for Fast
            Log.d("BENCHMARK", "=== FAST QUERY PLAN ===");
            dbHelper.explainQueryPlan(
                    "SELECT * FROM notes WHERE title LIKE ? || '%' OR content LIKE ? || '%'",
                    new String[]{testQuery, testQuery}
            );
        });

        Button btnGenerate = findViewById(R.id.btnGenerate);
        Button btnClear = findViewById(R.id.btnClear);

        btnClear.setOnClickListener(v -> {
            dbHelper.clearDatabase();
            loadNotes();
            Toast.makeText(MainActivity.this, "All notes deleted!", Toast.LENGTH_SHORT).show();
        });

        btnGenerate.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Generating 50,000 notes…", Toast.LENGTH_LONG).show();

            new Thread(() -> {
                dbHelper.generateTestData(50000);

                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Finished generating 50,000 notes!", Toast.LENGTH_LONG).show();
                    loadNotes();
                });
            }).start();
        });


    }

    private void loadNotes() {
        notes.clear();
        notes.addAll(dbHelper.getAllNotes());
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }

}
