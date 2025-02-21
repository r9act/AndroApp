package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<String> filePickerLauncher;

    private final List<Birthday> birthdays = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button uploadButton = findViewById(R.id.uploadButton);
        Button showAllButton = findViewById(R.id.showAllButton);

        // Инициализация лаунчера для выбора файла
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

        // Обработчик нажатия на кнопку для выбора файла
        uploadButton.setOnClickListener(viewToDraw -> openFilePicker());

        // Обработчик нажатия на кнопку для перехода на экран с днями рождения
        showAllButton.setOnClickListener(viewToDraw -> displayAllBirthdays());
    }

    // Открытие файлового менеджера для выбора Excel файла
    private void openFilePicker() {
        filePickerLauncher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    // Парсинг и загрузка данных из выбранного Excel файла
    private void parseAndUploadFile(Uri fileUri) throws FileNotFoundException {
        InputStream inputStream = getContentResolver().openInputStream(fileUri);
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                String name = row.getCell(0).getStringCellValue();
                Date date = row.getCell(1).getDateCellValue();
                birthdays.add(new Birthday(name, date));
            }
            workbook.close();

            // Уведомление об успешной загрузке и сохранении данных
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, "Файл успешно загружен и данные сохранены!", Toast.LENGTH_SHORT).show();
            });

        } catch (IOException e) {
            Log.e("FilePicker", "IOException while reading the Excel file: " + e.getMessage());
            e.printStackTrace();
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, "Ошибка при чтении файла", Toast.LENGTH_SHORT).show();
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
        // Передаем список дней рождения в новое Activity
        Intent intent = new Intent(MainActivity.this, BirthdayListActivity.class);
        intent.putExtra("BIRTHDAYS_LIST", new ArrayList<>(birthdays));  // Передаем список
        startActivity(intent);  // Переход в новое Activity
    }
}