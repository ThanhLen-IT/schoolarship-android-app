package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREF_NAME = "scholarship_gateway_pref";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_name";

    private final SharedPreferences prefs;

    public TokenManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public void saveUser(String email, String name) {
        prefs.edit()
                .putString(KEY_USER_EMAIL, email)
                .putString(KEY_USER_NAME, name)
                .apply();
    }

    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, "");
    }

    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "");
    }

    public void clear() {
        prefs.edit().clear().apply();
    }

    public boolean isLoggedIn() {
        return getToken() != null && !getToken().isEmpty();
    }
}
