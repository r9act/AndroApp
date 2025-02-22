package com.example.myapplication.entity;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PersonBirthdayDao {
    @Insert
    void insertAll(List<PersonBirthday> birthdays);

    @Query("SELECT * FROM person_birthdays")
    List<PersonBirthday> getAllBirthdays();

    @Query("DELETE FROM person_birthdays")
    void deleteAll();
}
