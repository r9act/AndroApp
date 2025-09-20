package com.example.myapplication.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.time.LocalDate;

@Entity(tableName = "person_birthdays")
@TypeConverters({LocalDateConverter.class})
public class PersonBirthday implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public Long id;

    public String name;
    public String surname;
    public LocalDate date;
    public Long ownerId;

    public PersonBirthday() {
    }

    public PersonBirthday(String name, String surname, LocalDate date, Long ownerId) {
        this.name = name;
        this.surname = surname;
        this.date = date;
        this.ownerId = ownerId;
    }

    @Ignore
    public PersonBirthday(String name, String surname, LocalDate date) {
        this.name = name;
        this.surname = surname;
        this.date = date;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}

