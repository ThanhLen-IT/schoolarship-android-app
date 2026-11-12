package com.example.myapplication;

import android.app.Activity;
import android.view.View;
import android.widget.*;

import androidx.core.content.ContextCompat;

/** Shared loading, empty and retry state for authenticated mobile lists. */
final class MobileScreen {
    final Activity owner;
    final LinearLayout root, toolbar, rows, footer;
    final TextView state;
    final ProgressBar progress;
    final ScrollView scroll;

    MobileScreen(Activity owner, String title) {
        this.owner = owner;
        root = new LinearLayout(owner);
        root.setOrientation(1);
        root.setBackgroundColor(ContextCompat.getColor(owner, R.color.color_background));
        root.setPadding(dp(16), 0, dp(16), 0);
        toolbar = new LinearLayout(owner);
        toolbar.setGravity(android.view.Gravity.CENTER_VERTICAL);
        root.addView(toolbar);
        Button back = button(toolbar, "‹ Quay lại", () -> owner.finish());
        text(toolbar, title, 22);
        progress = new ProgressBar(owner, null, android.R.attr.progressBarStyleHorizontal);
        root.addView(progress, new LinearLayout.LayoutParams(-1, 6));
        state = text(root, "", 14);
        state.setPadding(12, 12, 12, 12);
        scroll = new ScrollView(owner);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        rows = new LinearLayout(owner);
        rows.setOrientation(1);
        scroll.addView(rows);
        footer = new LinearLayout(owner);
        footer.setOrientation(1);
        root.addView(footer);
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(
                root,
                (v, insets) -> {
                    androidx.core.graphics.Insets b =
                            insets.getInsets(
                                    androidx.core.view.WindowInsetsCompat.Type.systemBars()
                                            | androidx.core.view.WindowInsetsCompat.Type.ime());
                    v.setPadding(dp(16) + b.left, b.top, dp(16) + b.right, b.bottom);
                    return insets;
                });
        owner.setContentView(root);
        progress.setVisibility(View.GONE);
    }

    TextView text(LinearLayout parent, String value, int size) {
        TextView v = new TextView(owner);
        v.setText(value == null ? "" : value);
        v.setTextSize(size);
        v.setTextColor(ContextCompat.getColor(owner, R.color.color_text_primary));
        parent.addView(v);
        return v;
    }

    Button button(LinearLayout parent, String label, Runnable run) {
        com.google.android.material.button.MaterialButton v =
                new com.google.android.material.button.MaterialButton(owner);
        v.setText(label);
        v.setOnClickListener(x -> run.run());
        parent.addView(v);
        return v;
    }

    LinearLayout card() {
        com.google.android.material.card.MaterialCardView card =
                new com.google.android.material.card.MaterialCardView(owner);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, 8, 0, 8);
        rows.addView(card, lp);
        LinearLayout box = new LinearLayout(owner);
        box.setPadding(dp(16), dp(12), dp(16), dp(12));
        box.setOrientation(1);
        card.addView(box);
        return box;
    }

    void loading() {
        progress.setVisibility(View.VISIBLE);
        state.setText("Đang tải…");
        state.setOnClickListener(null);
    }

    void ready(boolean empty) {
        progress.setVisibility(View.GONE);
        state.setText(empty ? "Chưa có dữ liệu để hiển thị." : "");
        state.setOnClickListener(null);
    }

    void failed(String message, Runnable retry) {
        progress.setVisibility(View.GONE);
        state.setText(message + "\nChạm để thử lại.");
        state.setOnClickListener(v -> retry.run());
    }

    private int dp(int value) {
        return Math.round(value * owner.getResources().getDisplayMetrics().density);
    }
}
