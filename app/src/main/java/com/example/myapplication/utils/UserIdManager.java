package com.example.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

public class UserIdManager {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USER_ID = "user_id";

    public static String getUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String userId = prefs.getString(KEY_USER_ID, null);

        if (userId == null) {
            // Generate a new UUID if none exists
            userId = UUID.randomUUID().toString();
            // Save it to SharedPreferences
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_USER_ID, userId);
            editor.apply();
        }

        return userId;
    }
}
