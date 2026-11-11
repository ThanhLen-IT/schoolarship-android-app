package com.example.myapplication;

import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

public class MentorDetailActivity extends androidx.appcompat.app.AppCompatActivity {
    private MobileScreen ui;
    private String mentor, scholarship;
    private Uri cv;
    private String draftIntro = "";
    private EditText intro;
    private TextView filename;
    private Button apply;
    private final androidx.activity.result.ActivityResultLauncher<String[]> pick =
            registerForActivityResult(
                    new androidx.activity.result.contract.ActivityResultContracts.OpenDocument(),
                    uri -> {
                        if (uri != null) {
                            cv = uri;
                            try {
                                getContentResolver()
                                        .takePersistableUriPermission(
                                                uri,
                                                android.content.Intent
                                                        .FLAG_GRANT_READ_URI_PERMISSION);
                            } catch (SecurityException ignored) {
                            }
                            filename.setText("Đã chọn CV PDF");
                        }
                    });

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        mentor = getIntent().getStringExtra("mentor_id");
        scholarship = getIntent().getStringExtra("scholarship_id");
        ui = new MobileScreen(this, "Hồ sơ Mentor");
        if (mentor == null || scholarship == null) {
            finish();
            return;
        }
        if (b != null) {
            draftIntro = b.getString("intro", "");
            String uri = b.getString("cv");
            if (uri != null) cv = Uri.parse(uri);
        }
        load();
    }

    private void load() {
        ui.loading();
        ApiUi.request(
                this,
                ApiClient.getApiService().mentor(mentor),
                r -> {
                    ui.rows.removeAllViews();
                    if (r.getData() == null || r.getData().mentor == null) {
                        ui.failed("Không có thông tin Mentor.", () -> load());
                        return;
                    }
                    UserProfile m = r.getData().mentor;
                    android.widget.LinearLayout card = ui.card();
                    android.widget.ImageView image = new ImageView(this);
                    card.addView(image, new android.widget.LinearLayout.LayoutParams(160, 160));
                    RemoteImage.show(image, m.getAvatarUrl());
                    ui.text(card, m.getFullName(), 22);
                    ui.text(card, m.getUniversity(), 16);
                    ui.text(card, m.getBio(), 16);
                    ui.text(
                            card,
                            m.getSkills() == null ? "" : TextJoiner.join(", ", m.getSkills()),
                            14);
                    filename = ui.text(card, cv == null ? "Chưa chọn CV" : "Đã chọn CV PDF", 14);
                    ui.button(
                            card,
                            "Chọn CV PDF (tối đa 5 MB)",
                            () -> {
                                if (ApiUi.requireLogin(this))
                                    pick.launch(new String[] {"application/pdf"});
                            });
                    intro = new EditText(this);
                    intro.setText(draftIntro);
                    intro.setHint("Giới thiệu và mục tiêu mentoring");
                    intro.setFilters(
                            new android.text.InputFilter[] {
                                new android.text.InputFilter.LengthFilter(3000)
                            });
                    card.addView(intro);
                    apply = ui.button(card, "Gửi hồ sơ tới Mentor", () -> apply());
                    ui.button(card, "Mở cuộc trò chuyện", () -> MobileForms.chat(this, mentor));
                    ui.ready(false);
                },
                m -> ui.failed(m, () -> load()));
    }

    private void apply() {
        if (!ApiUi.requireLogin(this)) return;
        if (cv == null) {
            ApiUi.error(this, "Vui lòng chọn CV PDF.");
            return;
        }
        apply.setEnabled(false);
        String note = intro.getText().toString();
        MobileFiles.pdf(
                this,
                cv,
                bytes -> {
                    okhttp3.MultipartBody.Part file =
                            okhttp3.MultipartBody.Part.createFormData(
                                    "cv",
                                    "cv.pdf",
                                    okhttp3.RequestBody.create(
                                            okhttp3.MediaType.parse("application/pdf"), bytes));
                    ApiUi.request(
                            this,
                            ApiClient.getApiService()
                                    .apply(
                                            ApiUi.bearer(this),
                                            MobileFiles.value(mentor),
                                            MobileFiles.value(scholarship),
                                            MobileFiles.value(note),
                                            file),
                            r -> {
                                apply.setEnabled(true);
                                ApiUi.error(
                                        this,
                                        "Đã gửi hồ sơ. Bạn có thể theo dõi ở Hồ sơ mentoring.");
                                startActivity(
                                        new android.content.Intent(
                                                this, MyApplicationsActivity.class));
                            },
                            m -> {
                                apply.setEnabled(true);
                                ApiUi.error(this, m);
                            });
                },
                m -> {
                    apply.setEnabled(true);
                    ApiUi.error(this, m);
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle b) {
        if (intro != null) b.putString("intro", intro.getText().toString());
        if (cv != null) b.putString("cv", cv.toString());
        super.onSaveInstanceState(b);
    }
}
