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

    EditText etTitle, etContent; // input fields for title and context
    Button btnSave;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) { // initial set up
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        dbHelper = new DBHelper(this);

        etTitle = findViewById(R.id.etTitle); // find the ui elements with id
        etContent = findViewById(R.id.etContent);
        btnSave = findViewById(R.id.btnSave);

        btnSave = findViewById(R.id.btnSave);

        Intent intent = getIntent();
        boolean name = intent.hasExtra("noteIdToCheck"); // checks if note id was passed
        int noteId = intent.getIntExtra("noteIdToCheck", -1); // gets the id or defaults to -1

        if (noteId != -1 && name) { // if the note is to be edited
            Note note = dbHelper.getNoteById(noteId);
            if (note != null) {
                etTitle.setText(note.getTitle()); // set title
                etContent.setText(note.getContent()); // set content
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

                if (!title.isEmpty() && !content.isEmpty()) { // validate input
                    List<Note> allNotes = dbHelper.getAllNotes();
                    for (Note existingNote : allNotes) {
                        if (existingNote.getTitle().equals(title) && existingNote.getId() != noteId) {
                            Toast.makeText(NoteEditorActivity.this, "This note title already exists!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    Log.d("NoteEditor", "Inserting note into database...");
                    if (noteId != -1) { // update note
                        dbHelper.updateNote(noteId, title,content);
                    } else { // insert note
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