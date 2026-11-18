package com.example.myapplication;

import android.content.Intent;
import android.os.*;

import java.util.*;

public class NotificationsActivity extends androidx.appcompat.app.AppCompatActivity {
    private MobileScreen ui;
    private int page, pages;
    private boolean loading;
    private final List<MobileData.Notice> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        ui = new MobileScreen(this, "Thông báo");
        ui.button(ui.footer, "Làm mới", () -> load(1));
        ui.button(
                ui.footer,
                "Đánh dấu tất cả đã đọc",
                () ->
                        ApiUi.request(
                                this,
                                ApiClient.getApiService().readNotices(ApiUi.bearer(this)),
                                r -> load(1),
                                m -> ApiUi.error(this, m)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ApiUi.requireLogin(this)) load(1);
    }

    private void load(int n) {
        if (loading) return;
        loading = true;
        ui.loading();
        ApiUi.request(
                this,
                ApiClient.getApiService().notifications(ApiUi.bearer(this), n, 20),
                r -> {
                    loading = false;
                    page = n;
                    pages = r.getTotalPages() == null ? n : r.getTotalPages();
                    if (n == 1) items.clear();
                    if (r.getData() != null) items.addAll(r.getData());
                    render();
                },
                m -> {
                    loading = false;
                    ui.failed(m, () -> load(n));
                });
    }

    private void render() {
        ui.rows.removeAllViews();
        for (MobileData.Notice n : items) {
            android.widget.LinearLayout row = ui.card();
            ui.text(row, (n.is_read ? "" : "● ") + n.title, 18);
            ui.text(row, n.content, 14);
            ui.text(row, n.created_at, 11);
            ui.button(row, "Xem", () -> open(n));
        }
        if (page < pages) ui.button(ui.rows, "Tải thêm", () -> load(page + 1));
        ui.ready(items.isEmpty());
    }

    private void open(MobileData.Notice n) {
        ApiUi.request(
                this,
                ApiClient.getApiService().readNotice(ApiUi.bearer(this), n.id),
                r -> {
                    n.is_read = true;
                    render();
                    if ("chat".equals(n.type))
                        startActivity(
                                new Intent(this, ChatDetailActivity.class)
                                        .putExtra("conversation_id", n.reference_id));
                    else if ("verification".equals(n.type))
                        startActivity(
                                new Intent(this, VerificationStatusActivity.class)
                                        .putExtra("scholarship_id", n.reference_id));
                    else if ("support".equals(n.type))
                        startActivity(new Intent(this, SupportActivity.class));
                    else if ("mentoring".equals(n.type))
                        startActivity(new Intent(this, MyApplicationsActivity.class));
                },
                m -> ApiUi.error(this, m));
    }
}
