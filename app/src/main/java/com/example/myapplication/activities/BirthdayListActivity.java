package com.example.myapplication.activities;

import static com.example.myapplication.activities.MainActivity.EXTRA_BIRTHDAYS_LIST;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.BirthdayAdapter;
import com.example.myapplication.entity.PersonBirthday;
import com.example.myapplication.R;

import java.util.List;

public class BirthdayListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthdays);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<PersonBirthday> personBirthdays = (List<PersonBirthday>) getIntent().getSerializableExtra(EXTRA_BIRTHDAYS_LIST);

        BirthdayAdapter adapter = new BirthdayAdapter(personBirthdays);
        recyclerView.setAdapter(adapter);
        // Чёрный фон для статус-бара и панели навигации
        getWindow().setStatusBarColor(Color.BLACK);  // Статус-бар
        getWindow().setNavigationBarColor(Color.BLACK);  // Панель навигации
    }
}
