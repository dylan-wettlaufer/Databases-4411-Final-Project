package com.example.databases4411_finalproject.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.databases4411_finalproject.model.Note;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "notes.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "notes";
    private static final String COL_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_CONTENT = "content";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TITLE + " TEXT, " +
                COL_CONTENT + " TEXT)";
        Log.d("DBHelper", "Creating table: " + createTable);
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertNote(String title, String content) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TITLE, title);
        cv.put(COL_CONTENT, content);

        long result = db.insert(TABLE_NAME, null, cv);
        Log.d("DBHelper", "Insert result (row ID): " + result);

        if (result == -1) {
            Log.e("DBHelper", "Failed to insert note!");
        } else {
            Log.d("DBHelper", "Note inserted successfully with ID: " + result);
        }

        db.close();
    }

    public void deleteNote(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(id)});
        Log.d("DBHelper", "Deleted " + rowsDeleted + " rows");
        db.close();
    }

    public List<Note> getAllNotes() {
        List<Note> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        Log.d("DBHelper", "Query returned " + cursor.getCount() + " rows");

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE));
            String content = cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT));

            Log.d("DBHelper", "Retrieved note - ID: " + id + ", Title: " + title + ", Content: " + content);
            list.add(new Note(id, title, content));
        }
        cursor.close();
        db.close();

        Log.d("DBHelper", "Returning " + list.size() + " notes");
        return list;
    }
}