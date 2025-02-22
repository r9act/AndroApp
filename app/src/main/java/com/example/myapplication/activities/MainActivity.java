package com.example.myapplication.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.entity.PersonBirthday;
import com.example.myapplication.R;
import com.example.myapplication.utils.PersonBirthdayExcelFileParser;
import com.example.myapplication.utils.PersonBirthdayFileParser;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<String> filePickerLauncher;

    List<PersonBirthday> birthdayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button uploadButton = findViewById(R.id.uploadButton);
        Button showAllButton = findViewById(R.id.showAllButton);

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
    }

    private void openFilePicker() {
        filePickerLauncher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    private void parseAndUploadFile(Uri fileUri) throws FileNotFoundException {
        InputStream inputStream = getContentResolver().openInputStream(fileUri);
        try {
            PersonBirthdayFileParser parser = new PersonBirthdayExcelFileParser();
            birthdayList = parser.parse(inputStream);
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, "Файл успешно загружен и данные сохранены!", Toast.LENGTH_SHORT).show();
            });

        } catch (Exception e) {
            Log.e("FilePicker", "Error while processing the Excel file: " + e.getMessage());
            e.printStackTrace();
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, "Ошибка при обработке файла", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void displayAllBirthdays() {
        Intent intent = new Intent(MainActivity.this, BirthdayListActivity.class);
        intent.putExtra("BIRTHDAYS_LIST", new ArrayList<>(birthdayList)); // Передаем список
        startActivity(intent);
    }
}
