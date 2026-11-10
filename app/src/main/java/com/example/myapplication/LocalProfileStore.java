package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

/** One device-local demo profile. Never stores credentials or authentication state. */
final class LocalProfileStore {
    private LocalProfileStore() {}
    static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences("local_demo_profile", Context.MODE_PRIVATE);
    }
    static String name(Context context) {
        return prefs(context).getString("name", context.getString(R.string.profile_name_default));
    }
    static void showAvatar(android.widget.ImageView image, String uri) {
        image.setImageResource(R.drawable.ic_profile_placeholder);
        if (!uri.isEmpty()) {
            try {
                image.setImageBitmap(LocalAvatarStorage.preview(image.getContext(), android.net.Uri.parse(uri)));
                if (image.getDrawable() == null) image.setImageResource(R.drawable.ic_profile_placeholder);
            } catch (java.io.IOException | RuntimeException e) {
                image.setImageResource(R.drawable.ic_profile_placeholder);
            }
        }
    }
}
