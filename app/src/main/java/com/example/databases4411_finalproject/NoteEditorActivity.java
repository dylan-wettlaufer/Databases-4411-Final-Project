package com.example.databases4411_finalproject;
import com.example.databases4411_finalproject.model.Note;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.databases4411_finalproject.data.DBHelper;

import java.util.List;
import java.util.Vector;

public class NoteEditorActivity extends AppCompatActivity {

    EditText etTitle, etContent;
    Button btnSave;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        dbHelper = new DBHelper(this);

        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        btnSave = findViewById(R.id.btnSave);

        btnSave = findViewById(R.id.btnSave);

        Intent intent = getIntent();
        boolean name = intent.hasExtra("noteIdToCheck");
        int noteId = intent.getIntExtra("noteIdToCheck", -1);

        if (noteId != -1 && name) {
            Note note = dbHelper.getNoteById(noteId);
            if (note != null) {
                etTitle.setText(note.getTitle());
                etContent.setText(note.getContent());
            }
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString();
                String content = etContent.getText().toString();

                Log.d("NoteEditor", "Save button clicked");
                Log.d("NoteEditor", "Title: '" + title + "'");
                Log.d("NoteEditor", "Content: '" + content + "'");
                Log.d("NoteEditor", "Title empty: " + title.isEmpty());
                Log.d("NoteEditor", "Content empty: " + content.isEmpty());

                if (!title.isEmpty() && !content.isEmpty()) {
                    List<Note> allNotes = dbHelper.getAllNotes();
                    for (Note existingNote : allNotes) {
                        if (existingNote.getTitle().equals(title) && existingNote.getId() != noteId) {
                            Toast.makeText(NoteEditorActivity.this, "This note title already exists!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    Log.d("NoteEditor", "Inserting note into database...");
                    if (noteId != -1) {
                        dbHelper.updateNote(noteId, title,content);
                    } else {
                        dbHelper.insertNote(title, content);
                    }
                    Log.d("NoteEditor", "Note inserted successfully");
                    Toast.makeText(NoteEditorActivity.this, "Note saved!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.d("NoteEditor", "Title or content is empty - not saving");
                    Toast.makeText(NoteEditorActivity.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}