package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import java.util.*;

public class MessagesActivity extends androidx.appcompat.app.AppCompatActivity {
    private MobileScreen ui;
    private int page = 0, pages = 0;
    private boolean loading;
    private final List<MobileData.Conversation> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        ui = new MobileScreen(this, "Tin nhắn");
        ui.button(ui.footer, "Làm mới", () -> load(1));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ApiUi.requireLogin(this)) load(1);
    }

    private void load(int requested) {
        if (loading) return;
        loading = true;
        ui.loading();
        ApiUi.request(
                this,
                ApiClient.getApiService().conversations(ApiUi.bearer(this), requested, 20),
                r -> {
                    loading = false;
                    page = requested;
                    pages = r.getTotalPages() == null ? page : r.getTotalPages();
                    if (page == 1) items.clear();
                    if (r.getData() != null) items.addAll(r.getData());
                    render();
                },
                m -> {
                    loading = false;
                    ui.failed(m, () -> load(requested));
                });
    }

    private void render() {
        ui.rows.removeAllViews();
        for (MobileData.Conversation c : items) {
            android.widget.LinearLayout row = ui.card();
            ui.text(
                    row,
                    c.peer_name + (c.unread_count > 0 ? " · " + c.unread_count + " chưa đọc" : ""),
                    18);
            ui.text(row, c.last_message, 14);
            ui.button(
                    row,
                    "Mở trò chuyện",
                    () ->
                            startActivity(
                                    new Intent(this, ChatDetailActivity.class)
                                            .putExtra("conversation_id", c.id)
                                            .putExtra(EXTRA_NAME, c.peer_name)));
        }
        if (page < pages) ui.button(ui.rows, "Tải thêm", () -> load(page + 1));
        ui.ready(items.isEmpty());
    }

    static final String EXTRA_NAME = "peer_name";
}
