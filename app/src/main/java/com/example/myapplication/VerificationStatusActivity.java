package com.example.myapplication;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class VerificationStatusActivity extends AppCompatActivity {
    public static final String EXTRA_NEW_SUBMISSION = "extra_new_submission";

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_verification_status);
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (view, insets) -> {
                    androidx.core.graphics.Insets bars =
                            insets.getInsets(
                                    androidx.core.view.WindowInsetsCompat.Type.systemBars()
                                            | androidx.core.view.WindowInsetsCompat.Type.ime());
                    view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                    return insets;
                });
        findViewById(R.id.btn_back_verification_status).setOnClickListener(v -> finish());
        findViewById(R.id.layout_empty_state).setOnClickListener(v -> load());
        if (ApiUi.requireLogin(this)) load();
    }

    private void load() {
        ApiUi.request(
                this,
                ApiClient.getApiService()
                        .verificationStatus(
                                ApiUi.bearer(this), getIntent().getStringExtra("scholarship_id")),
                result -> {
                    VerificationData data = result.getData();
                    List<VerificationStatusRecord> rows = new ArrayList<>();
                    if (data != null) {
                        String status =
                                "pending".equals(data.status)
                                        ? "Pending Review"
                                        : "approved".equals(data.status)
                                                ? "Approved"
                                                : "Failed / Rejected";
                        rows.add(
                                new VerificationStatusRecord(
                                        data.id,
                                        data.scholarship_title == null
                                                ? "Hồ sơ Mentor"
                                                : data.scholarship_title,
                                        "",
                                        status,
                                        data.rejection_reason == null
                                                ? data.created_at
                                                : data.rejection_reason));
                    }
                    RecyclerView list = findViewById(R.id.recycler_verification_status);
                    list.setLayoutManager(new LinearLayoutManager(this));
                    list.setAdapter(new VerificationStatusAdapter(rows));
                    list.setVisibility(rows.isEmpty() ? View.GONE : View.VISIBLE);
                    findViewById(R.id.layout_empty_state)
                            .setVisibility(rows.isEmpty() ? View.VISIBLE : View.GONE);
                },
                message -> ApiUi.error(this, message));
    }
}
