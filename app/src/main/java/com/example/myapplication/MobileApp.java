package com.example.myapplication;

public class MobileApp extends android.app.Application {
    public void onCreate() {
        super.onCreate();
        applyTheme(this);
    }

    static void applyTheme(android.content.Context c) {
        int mode =
                c.getSharedPreferences("appearance", 0)
                        .getInt(
                                "mode",
                                androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(mode);
    }
}
