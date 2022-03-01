package com.example.reminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    RecyclerView mRecyclerview;
    ArrayList<Model> dataholder = new ArrayList<Model>();                                               //Array list to add reminders and display in recyclerview
    myAdapter adapter;
    private FloatingActionButton flt_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list2);


        mRecyclerview = (RecyclerView) findViewById(R.id.recyclerView);
        flt_btn = findViewById(R.id.fab_btn);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));



        Cursor cursor = new dbManager(getApplicationContext()).readallreminders();                  //Cursor To Load data From the database
        while (cursor.moveToNext()) {
            Model model = new Model(cursor.getString(1),cursor.getString(2), cursor.getString(3));
            dataholder.add(model);

        }
        flt_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListActivity.this,SettingsActivity.class);
                startActivity(intent);
            }
        });

        adapter = new myAdapter(dataholder);
        mRecyclerview.setAdapter(adapter);
        adapter.notifyItemRangeInserted(0, dataholder.size());
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ListActivity.this,MainActivity.class);
        startActivity(intent);
//        finish();//Makes the user to exit form the app
        super.onBackPressed();

    }
}