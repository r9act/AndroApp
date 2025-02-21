package com.example.myapplication;

import java.io.Serializable;
import java.util.Date;

public class Birthday implements Serializable {
    private String name;
    private Date date;

    public Birthday(String name, Date date) {
        this.name = name;
        this.date = date;
    }

    public String getName() { return name; }
    public Date getDate() { return date; }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

