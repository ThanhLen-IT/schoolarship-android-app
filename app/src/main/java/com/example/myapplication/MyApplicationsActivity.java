package com.example.myapplication;

import android.os.Bundle;
import android.widget.*;

import java.util.*;

public class MyApplicationsActivity extends androidx.appcompat.app.AppCompatActivity {
    private MobileScreen ui;
    private boolean loading;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        ui = new MobileScreen(this, "Hồ sơ mentoring");
        ui.button(ui.footer, "Làm mới", () -> load());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ApiUi.requireLogin(this)) load();
    }

    private void load() {
        if (loading) return;
        loading = true;
        ui.loading();
        ApiUi.request(
                this,
                ApiClient.getApiService().myApplications(ApiUi.bearer(this)),
                r -> {
                    loading = false;
                    ui.rows.removeAllViews();
                    List<ApplicationData> rows =
                            r.getData() == null ? Collections.emptyList() : r.getData();
                    for (ApplicationData a : rows) render(a);
                    ui.ready(rows.isEmpty());
                },
                m -> {
                    loading = false;
                    ui.failed(m, () -> load());
                });
    }

    private void render(ApplicationData a) {
        LinearLayout row = ui.card();
        boolean mentor = "mentor".equals(a.viewer_role);
        ui.text(row, a.scholarship_title, 20);
        ui.text(
                row,
                (mentor ? "Mentee: " + a.mentee_name : "Mentor: " + a.mentor_name)
                        + " · "
                        + a.label(),
                16);
        ui.text(row, a.introduction, 14);
        ui.button(row, "Xem CV", () -> MobileFiles.openCv(this, a.id));
        ui.button(
                row,
                "Trò chuyện",
                () -> MobileForms.chat(this, mentor ? a.mentee_id : a.mentor_id));
        if (mentor && "pending".equals(a.status)) {
            ui.button(row, "Chấp nhận", () -> progress(a, "accept"));
            ui.button(row, "Từ chối", () -> progress(a, "reject"));
        }
        if (mentor && "accepted".equals(a.status))
            ui.button(row, "Bắt đầu mentoring", () -> progress(a, "start"));
        if (!mentor && "in_progress".equals(a.status)) {
            ui.button(row, "Xác nhận hoàn thành", () -> progress(a, "complete"));
            ui.button(
                    row,
                    "Gửi yêu cầu hỗ trợ",
                    () ->
                            MobileForms.input(
                                    this,
                                    "Yêu cầu hỗ trợ",
                                    "Nội dung cần Mentor phản hồi",
                                    false,
                                    value ->
                                            ApiUi.request(
                                                    this,
                                                    ApiClient.getApiService()
                                                            .responseRequest(
                                                                    ApiUi.bearer(this),
                                                                    a.id,
                                                                    MobileForms.body(
                                                                            "request_note", value)),
                                                    r -> requests(a),
                                                    m -> ApiUi.error(this, m))));
        }
        if ("in_progress".equals(a.status) || "completed".equals(a.status))
            ui.button(row, "Các yêu cầu hỗ trợ", () -> requests(a));
        if (!mentor && "completed".equals(a.status))
            ui.button(
                    row,
                    "Đánh giá Mentor",
                    () ->
                            new androidx.appcompat.app.AlertDialog.Builder(this)
                                    .setTitle("Chọn số sao")
                                    .setItems(
                                            new String[] {
                                                "1 sao", "2 sao", "3 sao", "4 sao", "5 sao"
                                            },
                                            (d, n) -> {
                                                Map<String, Object> body = new HashMap<>();
                                                body.put("application_id", a.id);
                                                body.put("rating", n + 1);
                                                ApiUi.request(
                                                        this,
                                                        ApiClient.getApiService()
                                                                .review(
                                                                        ApiUi.bearer(this),
                                                                        a.mentor_id,
                                                                        body),
                                                        r -> ApiUi.error(this, "Đã gửi đánh giá."),
                                                        m -> ApiUi.error(this, m));
                                            })
                                    .show());
    }

    private void progress(ApplicationData a, String action) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage("Xác nhận thay đổi trạng thái hồ sơ?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton(
                        "Xác nhận",
                        (d, w) ->
                                ApiUi.request(
                                        this,
                                        ApiClient.getApiService()
                                                .progress(
                                                        ApiUi.bearer(this),
                                                        a.id,
                                                        MobileForms.body("action", action)),
                                        r -> load(),
                                        m -> ApiUi.error(this, m)))
                .show();
    }

    private void requests(ApplicationData a) {
        ApiUi.request(
                this,
                ApiClient.getApiService().responseRequests(ApiUi.bearer(this), a.id),
                r -> {
                    List<MobileData.Request> list =
                            r.getData() == null ? Collections.emptyList() : r.getData();
                    if (list.isEmpty()) {
                        ApiUi.error(this, "Chưa có yêu cầu hỗ trợ.");
                        return;
                    }
                    String[] labels = new String[list.size()];
                    for (int i = 0; i < labels.length; i++)
                        labels[i] =
                                list.get(i).request_note
                                        + (list.get(i).responded_at == null
                                                ? " · Chưa phản hồi"
                                                : " · Đã phản hồi");
                    new androidx.appcompat.app.AlertDialog.Builder(this)
                            .setTitle("Yêu cầu hỗ trợ")
                            .setItems(
                                    labels,
                                    (d, n) -> {
                                        MobileData.Request q = list.get(n);
                                        if ("mentor".equals(a.viewer_role)
                                                && q.responded_at == null)
                                            MobileForms.input(
                                                    this,
                                                    "Phản hồi",
                                                    q.request_note,
                                                    false,
                                                    value ->
                                                            ApiUi.request(
                                                                    this,
                                                                    ApiClient.getApiService()
                                                                            .respond(
                                                                                    ApiUi.bearer(
                                                                                            this),
                                                                                    q.id,
                                                                                    MobileForms
                                                                                            .body(
                                                                                                    "response_note",
                                                                                                    value)),
                                                                    x -> requests(a),
                                                                    m -> ApiUi.error(this, m)));
                                        else
                                            new androidx.appcompat.app.AlertDialog.Builder(this)
                                                    .setMessage(
                                                            q.request_note
                                                                    + "\n\n"
                                                                    + (q.response_note == null
                                                                            ? "Đang chờ Mentor phản"
                                                                                    + " hồi."
                                                                            : q.response_note))
                                                    .setPositiveButton("Đóng", null)
                                                    .show();
                                    })
                            .show();
                },
                m -> ApiUi.error(this, m));
    }
}
