package com.example.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

public class UserIdManager {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USER_ID = "user_id";

    public static long getUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        long userId = prefs.getLong(KEY_USER_ID, -1);

        if (userId == -1) {
            do {
                userId = Math.abs(UUID.randomUUID().getMostSignificantBits());
            } while (userId == -1);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(KEY_USER_ID, userId);
            editor.apply();
        }

        return userId;
    }
}

