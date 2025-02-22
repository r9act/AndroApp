package com.example.myapplication.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.myapplication.entity.AppDatabase;
import com.example.myapplication.entity.PersonBirthday;
import com.example.myapplication.R;
import com.example.myapplication.entity.PersonBirthdayDao;
import com.example.myapplication.utils.PersonBirthdayExcelFileParser;
import com.example.myapplication.utils.PersonBirthdayFileParser;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<String> filePickerLauncher;

    private AppDatabase database;
    private PersonBirthdayDao birthdayDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Room database
        database = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "birthday-database")
                .build();
        birthdayDao = database.personBirthdayDao();

        Button uploadButton = findViewById(R.id.uploadButton);
        Button showAllButton = findViewById(R.id.showAllButton);
        Button deleteAllButton = findViewById(R.id.deleteAllButton);

        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        new Thread(() -> {
                            try {
                                parseAndUploadFile(uri);
                            } catch (FileNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }).start();
                    }
                }
        );

        uploadButton.setOnClickListener(viewToDraw -> openFilePicker());
        showAllButton.setOnClickListener(viewToDraw -> displayAllBirthdays());
        deleteAllButton.setOnClickListener(view -> deleteAllBirthdays());
    }

    private void openFilePicker() {
        filePickerLauncher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    private void parseAndUploadFile(Uri fileUri) throws FileNotFoundException {
        InputStream inputStream = getContentResolver().openInputStream(fileUri);
        try {
            PersonBirthdayFileParser parser = new PersonBirthdayExcelFileParser();
            List<PersonBirthday> birthdayList = parser.parse(inputStream);

            // Save to Room database
            new Thread(() -> {
                birthdayDao.insertAll(birthdayList);
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this,
                            "Файл успешно загружен и данные сохранены!",
                            Toast.LENGTH_SHORT).show();
                });
            }).start();

        } catch (Exception e) {
            Log.e("FilePicker", "Error while processing the Excel file: " + e.getMessage());
            e.printStackTrace();
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this,
                        "Ошибка при обработке файла",
                        Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void displayAllBirthdays() {
        new Thread(() -> {
            List<PersonBirthday> birthdays = birthdayDao.getAllBirthdays();
            runOnUiThread(() -> {
                Intent intent = new Intent(MainActivity.this, BirthdayListActivity.class);
                intent.putExtra("BIRTHDAYS_LIST", new ArrayList<>(birthdays));
                startActivity(intent);
            });
        }).start();
    }

    // New method to delete all birthdays
    private void deleteAllBirthdays() {
        new AlertDialog.Builder(this)
                .setTitle("Подтверждение")
                .setMessage("Вы уверены, что хотите удалить все записи?")
                .setPositiveButton("Да", (dialog, which) -> {
                    new Thread(() -> {
                        birthdayDao.deleteAll();
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this,
                                    "Все записи удалены!",
                                    Toast.LENGTH_SHORT).show();
                        });
                    }).start(); // Added .start() to run the thread
                })
                .setNegativeButton("Нет", null)
                .show();
    }
}
