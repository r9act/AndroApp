package com.example.myapplication.entity;

import androidx.room.Entity;
import androidx.room.TypeConverters;

@Entity(tableName = "users")
public class User {
    private Long id;
    private Long foreignId;
    private String name;
    private Boolean isReminderActive;

    public User(Long foreignId, String name, Boolean isReminderActive) {
        this.foreignId = foreignId;
        this.name = name;
        this.isReminderActive = isReminderActive;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getForeignId() {
        return foreignId;
    }

    public void setForeignId(Long foreignId) {
        this.foreignId = foreignId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getReminderActive() {
        return isReminderActive;
    }

    public void setReminderActive(Boolean reminderActive) {
        isReminderActive = reminderActive;
    }
}