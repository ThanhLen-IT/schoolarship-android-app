package com.example.myapplication;

final class SessionStore {
    private SessionStore() {}

    static void clear(android.content.Context context) {
        android.content.Context app = context.getApplicationContext();
        ProfileStore.prefs(app).edit().clear().apply();
        new TokenManager(app).clear();
        com.bumptech.glide.Glide.get(app).clearMemory();
        new Thread(
                        () -> {
                            com.bumptech.glide.Glide.get(app).clearDiskCache();
                            java.io.File dir = new java.io.File(app.getCacheDir(), "cv");
                            java.io.File[] files = dir.listFiles();
                            if (files != null)
                                for (java.io.File file : files) if (file.isFile()) file.delete();
                        },
                        "clear-session-cache")
                .start();
    }
}
