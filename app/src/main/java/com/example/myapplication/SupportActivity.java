package com.example.myapplication;

import android.os.Bundle;

import java.util.*;

public class SupportActivity extends androidx.appcompat.app.AppCompatActivity {
    private MobileScreen ui;
    private boolean loading;
    private int page, pages;
    private final List<MobileData.Ticket> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        ui = new MobileScreen(this, "Hỗ trợ & góp ý");
        ui.button(ui.footer, "Gửi yêu cầu mới", () -> compose());
        ui.button(ui.footer, "Làm mới", () -> load(1));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ApiUi.requireLogin(this)) load(1);
    }

    private void compose() {
        MobileForms.input(
                this,
                "Tiêu đề yêu cầu",
                "Mô tả ngắn gọn vấn đề",
                false,
                subject ->
                        MobileForms.input(
                                this,
                                "Nội dung yêu cầu",
                                "Mô tả chi tiết để chúng tôi hỗ trợ bạn",
                                false,
                                message -> {
                                    Map<String, String> body = MobileForms.body("subject", subject);
                                    body.put("message", message);
                                    ApiUi.request(
                                            this,
                                            ApiClient.getApiService()
                                                    .support(ApiUi.bearer(this), body),
                                            r -> {
                                                ApiUi.error(this, "Đã tiếp nhận yêu cầu hỗ trợ.");
                                                load(1);
                                            },
                                            m -> ApiUi.error(this, m));
                                }));
    }

    private void load(int n) {
        if (loading) return;
        loading = true;
        ui.loading();
        ApiUi.request(
                this,
                ApiClient.getApiService().supportHistory(ApiUi.bearer(this), n, 20),
                r -> {
                    loading = false;
                    page = n;
                    pages = r.getTotalPages() == null ? n : r.getTotalPages();
                    if (n == 1) items.clear();
                    if (r.getData() != null) items.addAll(r.getData());
                    ui.rows.removeAllViews();
                    for (MobileData.Ticket t : items) {
                        android.widget.LinearLayout row = ui.card();
                        ui.text(row, t.subject, 18);
                        ui.text(row, t.message, 15);
                        ui.text(
                                row,
                                "open".equals(t.status)
                                        ? "Đang chờ hỗ trợ"
                                        : "Trạng thái: " + t.status,
                                12);
                        if (t.admin_response != null)
                            ui.text(row, "Phản hồi: " + t.admin_response, 15);
                    }
                    if (page < pages) ui.button(ui.rows, "Tải thêm", () -> load(page + 1));
                    ui.ready(items.isEmpty());
                },
                m -> {
                    loading = false;
                    ui.failed(m, () -> load(n));
                });
    }
}
