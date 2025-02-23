package com.example.myapplication.entity;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {PersonBirthday.class}, version = 1)
@TypeConverters({LocalDateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract PersonBirthdayDao personBirthdayDao();
}
