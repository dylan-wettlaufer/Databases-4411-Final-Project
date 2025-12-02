package com.example.databases4411_finalproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.databases4411_finalproject.data.DBHelper;

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
                    Log.d("NoteEditor", "Inserting note into database...");
                    dbHelper.insertNote(title, content);
                    Log.d("NoteEditor", "Note inserted successfully");
                    Toast.makeText(NoteEditorActivity.this, "Note saved!", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to MainActivity
                } else {
                    Log.d("NoteEditor", "Title or content is empty - not saving");
                    Toast.makeText(NoteEditorActivity.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}