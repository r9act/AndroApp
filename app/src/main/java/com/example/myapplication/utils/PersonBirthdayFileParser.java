package com.example.myapplication.utils;

import com.example.myapplication.entity.PersonBirthday;

import java.io.InputStream;
import java.util.List;

public interface PersonBirthdayFileParser {

    List<PersonBirthday> parse(InputStream inputStream);
}