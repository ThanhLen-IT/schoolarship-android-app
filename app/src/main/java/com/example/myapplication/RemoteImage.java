package com.example.myapplication;

final class RemoteImage {
    private RemoteImage() {}

    static void show(android.widget.ImageView image, String url) {
        com.bumptech.glide.Glide.with(image)
                .load(url == null || url.isEmpty() ? null : url)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .into(image);
    }
}
