package com.example.myapplication.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.myapplication.DTO.PersonBirthdayDto;
import com.example.myapplication.DTO.UserDto;
import com.example.myapplication.entity.AppDatabase;
import com.example.myapplication.entity.PersonBirthday;
import com.example.myapplication.R;
import com.example.myapplication.entity.PersonBirthdayDao;
import com.example.myapplication.retrofit.ApiService;
import com.example.myapplication.utils.PersonBirthdayExcelFileParser;
import com.example.myapplication.utils.PersonBirthdayFileParser;
import com.example.myapplication.utils.UserIdManager;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class MainActivity extends AppCompatActivity {
    public static final String BASE_URL = "http://10.0.2.2:8080/";
    public static final String EXTRA_BIRTHDAYS_LIST = "BIRTHDAYS_LIST";

    private static final String DATABASE_NAME = "birthday-database";
    private ActivityResultLauncher<String> filePickerLauncher;

    private AppDatabase database;
    private PersonBirthdayDao birthdayDao;
    private ApiService apiService;
    private UserDto userDto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initRoomDatabase();
        initApiService();

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

        Button uploadButton = findViewById(R.id.uploadButton);
        Button showAllButton = findViewById(R.id.showAllButton);
        Button deleteAllButton = findViewById(R.id.deleteAllButton);

        uploadButton.setOnClickListener(viewToDraw -> openFilePicker());
        showAllButton.setOnClickListener(viewToDraw -> displayAllBirthdays());
        deleteAllButton.setOnClickListener(view -> deleteAllBirthdays());

        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);

        userDto = getOrCreateUserDto();
        saveUserToRemote(userDto);
    }

    private void openFilePicker() {
        filePickerLauncher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    private void parseAndUploadFile(Uri fileUri) throws FileNotFoundException {
        InputStream inputStream = getContentResolver().openInputStream(fileUri);
        try {
            PersonBirthdayFileParser parser = new PersonBirthdayExcelFileParser();
            List<PersonBirthday> birthdayList = parser.parse(inputStream);
            List<PersonBirthdayDto> birthdayDtoList = birthdayList.stream()
                    .map(PersonBirthdayDto::new)
                    .collect(Collectors.toList());
            saveUserBirthdaysToRemote(userDto.getForeignId(), birthdayDtoList);
            // Сохраним пока в Room (синхр c бэкендом добавим позже)
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

    @NonNull
    private UserDto getOrCreateUserDto() {
        UserDto userDto = new UserDto();
        userDto.setForeignId(UserIdManager.getUserId(this));
        userDto.setName(getPhoneName());
        userDto.setIsReminderActive(false);
        return userDto;
    }

    private void saveUserToRemote(UserDto userDto) {
        Call<Long> call = apiService.saveUserToRemote(userDto);
        call.enqueue(new retrofit2.Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, retrofit2.Response<Long> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Long userId = response.body();
                    Log.d("API", "User saved successfully. Id in remote is: " + userId);
                } else {
                    Log.e("API", "Failed: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Log.e("API", "Error: " + t.getMessage());
            }
        });
    }

    private void saveUserBirthdaysToRemote(Long foreignId, List<PersonBirthdayDto> personBirthdayDtos) {
        Call<Void> call = apiService.saveUserBirthdays(foreignId, personBirthdayDtos);
        call.enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("API", "List sent successfully");
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
                intent.putExtra(EXTRA_BIRTHDAYS_LIST, new ArrayList<>(birthdays));
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
                    }).start();
                })
                .setNegativeButton("Нет", null)
                .show();
    }

    private void initApiService() {
        //Кастомный маппер (добавил в него JavaTimeModule) - чтобы JacksonConverterFactory могла работать с LocaleDate
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    private void initRoomDatabase() {
        database = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, DATABASE_NAME)
                .build();
        birthdayDao = database.personBirthdayDao();
    }

    public static String getPhoneName() {
        return Build.MANUFACTURER + " " + Build.MODEL;
    }
}
