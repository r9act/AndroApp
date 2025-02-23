package com.example.myapplication.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.myapplication.DTO.UserDto;
import com.example.myapplication.entity.AppDatabase;
import com.example.myapplication.entity.PersonBirthday;
import com.example.myapplication.R;
import com.example.myapplication.entity.PersonBirthdayDao;
import com.example.myapplication.retrofit.ApiService;
import com.example.myapplication.utils.PersonBirthdayExcelFileParser;
import com.example.myapplication.utils.PersonBirthdayFileParser;
import com.example.myapplication.utils.UserIdManager;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<String> filePickerLauncher;

    private AppDatabase database;
    private PersonBirthdayDao birthdayDao;

    private ApiService apiService;
    private String androidId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Room database
        database = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "birthday-database")
                .build();
        birthdayDao = database.personBirthdayDao();

        androidId = UserIdManager.getUserId(this);
        Log.d("UserID", "Unique ID: " + androidId);


        Button uploadButton = findViewById(R.id.uploadButton);
        Button showAllButton = findViewById(R.id.showAllButton);
        Button deleteAllButton = findViewById(R.id.deleteAllButton);


        // Setup Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/") // Points to localhost:8080 on your machine
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

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
        // Чёрный фон для статус-бара и панели навигации
        getWindow().setStatusBarColor(Color.BLACK);  // Статус-бар
        getWindow().setNavigationBarColor(Color.BLACK);  // Панель навигации
    }

    private void openFilePicker() {
        filePickerLauncher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    private void parseAndUploadFile(Uri fileUri) throws FileNotFoundException {
        InputStream inputStream = getContentResolver().openInputStream(fileUri);
        try {
            PersonBirthdayFileParser parser = new PersonBirthdayExcelFileParser();
            List<PersonBirthday> birthdayList = parser.parse(inputStream);
            UserDto userDto = new UserDto();
            userDto.setAndroidId(777777L);
            userDto.setFirstName("Artemchik");

            // Create and send DTO
            sendToBackend(userDto);

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

    private void sendToBackend(UserDto userDto) {
        Call<Void> call = apiService.sendUser(userDto);
        call.enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("API", "User sent successfully");
                } else {
                    Log.e("API", "Failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API", "Error: " + t.getMessage());
            }
        });
    }

    private void displayAllBirthdays() {
        new Thread(() -> {
            // 1. Выполняем код в новом потоке
            List<PersonBirthday> birthdays = birthdayDao.getAllBirthdays();

            // 2. Передаём выполнение в главный (UI) поток
            runOnUiThread(() -> {
                // 3. Запускаем новую Activity в UI-потоке
                Intent intent = new Intent(MainActivity.this, BirthdayListActivity.class);
                intent.putExtra("BIRTHDAYS_LIST", new ArrayList<>(birthdays));
                startActivity(intent);
            });
        }).start();
    }


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
