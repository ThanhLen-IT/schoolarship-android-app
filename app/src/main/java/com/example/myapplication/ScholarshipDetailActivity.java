package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ScholarshipDetailActivity extends AppCompatActivity {
    public static final String EXTRA_SCHOLARSHIP_ID = "extra_scholarship_id";
    public static final String EXTRA_SCHOLARSHIP_TITLE = "extra_scholarship_title";
    public static final String EXTRA_SCHOLARSHIP_CATEGORY = "extra_scholarship_category";
    public static final String EXTRA_SCHOLARSHIP_IMAGE = "extra_scholarship_image";
    private String scholarshipId;
    private Scholarship scholarship;
    private retrofit2.Call<ApiResponse<Scholarship>> activeCall;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_scholarship_detail);
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    androidx.core.graphics.Insets bars =
                            insets.getInsets(
                                    androidx.core.view.WindowInsetsCompat.Type.systemBars());
                    v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                    return insets;
                });
        findViewById(R.id.btn_back_scholarship_detail).setOnClickListener(v -> finish());
        scholarshipId = getIntent().getStringExtra(EXTRA_SCHOLARSHIP_ID);
        findViewById(R.id.btn_share_scholarship)
                .setOnClickListener(
                        v -> {
                            if (scholarship == null) return;
                            Intent share =
                                    new Intent(Intent.ACTION_SEND)
                                            .setType("text/plain")
                                            .putExtra(
                                                    Intent.EXTRA_TEXT,
                                                    scholarship.getTitle()
                                                            + "\n"
                                                            + scholarship.getApplicationLink());
                            startActivity(Intent.createChooser(share, "Chia sẻ học bổng"));
                        });
        findViewById(R.id.btn_detail_find_mentor)
                .setOnClickListener(
                        v -> {
                            if (scholarship == null) return;
                            startActivity(
                                    new Intent(this, MentorRankingActivity.class)
                                            .putExtra(
                                                    MentorRankingActivity.EXTRA_SCHOLARSHIP_ID,
                                                    scholarshipId)
                                            .putExtra(
                                                    MentorRankingActivity.EXTRA_SCHOLARSHIP_NAME,
                                                    scholarship.getTitle()));
                        });
        Button apply = findViewById(R.id.btn_apply_now);
        apply.setText("Mở trang ứng tuyển");
        apply.setOnClickListener(
                v -> {
                    if (scholarship != null) ApiUi.openUrl(this, scholarship.getApplicationLink());
                });
        Button save = findViewById(R.id.btn_download_form);
        save.setText("Lưu học bổng");
        save.setOnClickListener(
                v -> {
                    if (scholarship == null || !ApiUi.requireLogin(this)) return;
                    save.setEnabled(false);
                    ApiUi.request(
                            this,
                            ApiClient.getApiService()
                                    .saveScholarship(ApiUi.bearer(this), scholarshipId),
                            result -> {
                                save.setEnabled(true);
                                ApiUi.error(this, "Đã lưu học bổng vào tài khoản.");
                            },
                            message -> {
                                save.setEnabled(true);
                                ApiUi.error(this, message);
                            });
                });
        load();
    }

    private void load() {
        if (scholarshipId == null || !scholarshipId.matches("[1-9][0-9]*")) {
            ApiUi.error(this, "ID học bổng không hợp lệ.");
            finish();
            return;
        }
        ((TextView) findViewById(R.id.tv_scholarship_detail_title)).setText("Đang tải…");
        ((TextView) findViewById(R.id.tv_scholarship_description)).setText("");
        ((TextView) findViewById(R.id.chip_category)).setText("");
        ((TextView) findViewById(R.id.chip_funding)).setText("");
        ((TextView) findViewById(R.id.chip_deadline)).setText("");
        activeCall =
                ApiUi.request(
                        this,
                        ApiClient.getApiService().getScholarshipDetail(scholarshipId),
                        result -> {
                            scholarship = result.getData();
                            if (scholarship == null) {
                                showError("Không có dữ liệu học bổng.");
                                return;
                            }
                            ((TextView) findViewById(R.id.tv_scholarship_detail_title))
                                    .setText(scholarship.getTitle());
                            ((TextView) findViewById(R.id.chip_category))
                                    .setText(scholarship.getDegreeLevel());
                            ((TextView) findViewById(R.id.chip_funding))
                                    .setText(scholarship.getFundingType());
                            ((TextView) findViewById(R.id.chip_deadline))
                                    .setText(scholarship.getDeadlineStr());
                            RemoteImage.show(
                                    findViewById(R.id.iv_scholarship_hero),
                                    scholarship.getImageUrl());
                            TextView description = findViewById(R.id.tv_scholarship_description);
                            String content =
                                    scholarship.getContentHtml().isEmpty()
                                            ? scholarship.getDescription()
                                            : scholarship.getContentHtml();
                            description.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY));
                            description.append(
                                    "\n\nYêu cầu\n"
                                            + Html.fromHtml(
                                                    scholarship.getRequirements(),
                                                    Html.FROM_HTML_MODE_LEGACY));
                            description.append(
                                    "\n\nQuyền lợi\n"
                                            + Html.fromHtml(
                                                    scholarship.getBenefits(),
                                                    Html.FROM_HTML_MODE_LEGACY));
                            description.setOnClickListener(null);
                        },
                        this::showError);
    }

    private void showError(String message) {
        ((TextView) findViewById(R.id.tv_scholarship_detail_title))
                .setText("Không thể tải học bổng");
        TextView retry = findViewById(R.id.tv_scholarship_description);
        retry.setText(message + "\nChạm để thử lại.");
        retry.setOnClickListener(v -> load());
    }

    @Override
    protected void onDestroy() {
        if (activeCall != null) activeCall.cancel();
        super.onDestroy();
    }
}
