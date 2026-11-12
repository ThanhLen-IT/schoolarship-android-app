package com.example.myapplication;

import android.app.Activity;
import android.widget.*;

import java.util.*;

final class MobileForms {
    static Map<String, String> body(String key, String value) {
        Map<String, String> b = new HashMap<>();
        b.put(key, value);
        return b;
    }

    static void input(
            Activity a,
            String title,
            String hint,
            boolean password,
            java.util.function.Consumer<String> done) {
        EditText input = new EditText(a);
        input.setHint(hint);
        input.setInputType(
                password
                        ? 129
                        : android.text.InputType.TYPE_CLASS_TEXT
                                | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        androidx.appcompat.app.AlertDialog d =
                new androidx.appcompat.app.AlertDialog.Builder(a)
                        .setTitle(title)
                        .setView(input)
                        .setNegativeButton("Hủy", null)
                        .setPositiveButton("Tiếp tục", null)
                        .create();
        d.setOnShowListener(
                x ->
                        d.getButton(-1)
                                .setOnClickListener(
                                        v -> {
                                            String value = input.getText().toString();
                                            if (password
                                                    ? value.isEmpty()
                                                    : value.trim().isEmpty()) {
                                                input.setError("Vui lòng nhập thông tin.");
                                                return;
                                            }
                                            d.dismiss();
                                            done.accept(password ? value : value.trim());
                                        }));
        d.show();
    }

    static void chat(Activity a, String peer) {
        if (!ApiUi.requireLogin(a)) return;
        ApiUi.request(
                a,
                ApiClient.getApiService()
                        .conversation(ApiUi.bearer(a), body("participant_id", peer)),
                r -> {
                    if (r.getData() != null)
                        a.startActivity(
                                new android.content.Intent(a, ChatDetailActivity.class)
                                        .putExtra("conversation_id", r.getData().id));
                },
                m -> ApiUi.error(a, m));
    }
}
