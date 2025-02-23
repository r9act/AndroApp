package com.example.myapplication.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = findViewById(R.id.progressBar);

        // Симуляция загрузки))
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progressStatus);
                        }
                    });
                    try {
                        Thread.sleep(10); // Скорость заполнения прогресс бара
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // Полоска загрузки закончена, переходим на следующий экран
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }
                });
            }
        }).start();
    }
}