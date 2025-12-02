package com.example.databases4411_finalproject;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DBHelper dbHelper;
    EditText inputItem;
    Button btnAdd;
    ListView listView;
    ArrayList<String> items;
    ArrayList<Integer> ids;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);
        inputItem = findViewById(R.id.inputItem);
        btnAdd = findViewById(R.id.btnAdd);
        listView = findViewById(R.id.listView);

        items = new ArrayList<>();
        ids = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);

        loadItems();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = inputItem.getText().toString();
                if (!name.isEmpty()) {
                    dbHelper.insertItem(name);
                    inputItem.setText("");
                    loadItems();
                }
            }
        });

        // Delete item on long click
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int itemId = ids.get(position);
                dbHelper.deleteItem(itemId);
                loadItems();
                return true;
            }
        });
    }

    private void loadItems() {
        items.clear();
        ids.clear();

        Cursor cursor = dbHelper.getAllItems();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            items.add(name);
            ids.add(id);
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }
}
