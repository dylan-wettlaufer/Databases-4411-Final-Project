package com.example.databases4411_finalproject.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
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

        // Indexes for optimized searching
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_notes_title ON " + TABLE_NAME + "(" + COL_TITLE + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_notes_content ON " + TABLE_NAME + "(" + COL_CONTENT + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // -----------------------------
    // INSERT
    // -----------------------------
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

    // -----------------------------
    // DELETE
    // -----------------------------
    public void deleteNote(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(id)});
        Log.d("DBHelper", "Deleted " + rowsDeleted + " rows");
        db.close();
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    public void updateNote(int id, String title, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, title);
        values.put(COL_CONTENT, content);
        db.update(TABLE_NAME, values, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // -----------------------------
    // GET ONE NOTE
    // -----------------------------
    public Note getNoteById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        Note note = null;
        if (cursor != null && cursor.moveToFirst()) {
            int noteId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE));
            String content = cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT));
            note = new Note(noteId, title, content);
            cursor.close();
        }
        db.close();
        return note;
    }

    // -----------------------------
    // GET ALL NOTES
    // -----------------------------
    public List<Note> getAllNotes() {
        List<Note> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        Log.d("DBHelper", "Query returned " + cursor.getCount() + " rows");

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE));
            String content = cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT));
            list.add(new Note(id, title, content));
        }

        cursor.close();
        db.close();
        return list;
    }

    // -----------------------------
    // SLOW SEARCH (FULL SCAN)
    // -----------------------------
    public List<Note> searchSlow(String query) {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Forces full table scan (slow)
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_NAME +
                        " WHERE " + COL_TITLE + " LIKE '%" + query + "%' OR " +
                        COL_CONTENT + " LIKE '%" + query + "%'",
                null
        );

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE));
            String content = cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT));
            notes.add(new Note(id, title, content));
        }

        cursor.close();
        return notes;
    }

    // -----------------------------
    // FAST SEARCH (INDEXED PREFIX MATCH)
    // -----------------------------
    public List<Note> searchFast(String query) {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Allows SQLite to use indexes: query%
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_NAME +
                        " WHERE " + COL_TITLE + " LIKE ? || '%' OR " +
                        COL_CONTENT + " LIKE ? || '%'",
                new String[]{query, query}
        );

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE));
            String content = cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT));
            notes.add(new Note(id, title, content));
        }

        cursor.close();
        return notes;
    }

    // -----------------------------
    // EXPLAIN QUERY PLAN
    // -----------------------------
    public void explainQueryPlan(String sql, String[] args) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("EXPLAIN QUERY PLAN " + sql, args);

        while (c.moveToNext()) {
            Log.d("QUERY_PLAN", c.getString(3));
        }
        c.close();
    }

    // -----------------------------
    // TIMING TOOL
    // -----------------------------
    public long timeQuery(Runnable r) {
        long start = System.nanoTime();
        r.run();
        return System.nanoTime() - start;
    }

    public void generateTestData(int count) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            String sql = "INSERT INTO " + TABLE_NAME + " (" + COL_TITLE + ", " + COL_CONTENT + ") VALUES (?, ?)";
            SQLiteStatement stmt = db.compileStatement(sql);

            for (int i = 0; i < count; i++) {
                stmt.clearBindings();
                stmt.bindString(1, "Title " + i);
                stmt.bindString(2, "This is the content of note " + i);
                stmt.executeInsert();

                if (i % 5000 == 0) {
                    Log.d("DBTest", "Inserted: " + i);
                }
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.close();
        Log.d("DBTest", "Finished inserting " + count + " records");
    }

    public void clearDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.close();
    }


}
