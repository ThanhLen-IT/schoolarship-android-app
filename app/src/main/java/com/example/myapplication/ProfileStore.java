package com.example.myapplication;

import android.content.Context;

final class ProfileStore {
    private ProfileStore() {}

    static android.content.SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(
                "profile_" + new TokenManager(context).getUserEmail(), Context.MODE_PRIVATE);
    }

    static String name(Context context) {
        return prefs(context).getString("name", new TokenManager(context).getUserName());
    }

    static void save(Context context, UserProfile profile) {
        prefs(context)
                .edit()
                .putString("name", text(profile.getFullName()))
                .putString("university", text(profile.getUniversity()))
                .putString("major", text(profile.getMajor()))
                .putString("bio", text(profile.getBio()))
                .putString(
                        "skills",
                        profile.getSkills() == null
                                ? ""
                                : TextJoiner.join(", ", profile.getSkills()))
                .putString("avatar", text(profile.getAvatarUrl()))
                .apply();
    }

    private static String text(String value) {
        return value == null ? "" : value;
    }
}
