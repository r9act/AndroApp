package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BirthdayListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthdays);  // Новая разметка для этого экрана

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Получаем список дней рождения из предыдущего Activity
        List<Birthday> birthdays = (List<Birthday>) getIntent().getSerializableExtra("BIRTHDAYS_LIST");

        BirthdayAdapter adapter = new BirthdayAdapter(birthdays);
        recyclerView.setAdapter(adapter);
    }
}
