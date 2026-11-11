package com.example.myapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SavedScholarshipsActivity extends AppCompatActivity {
    private final List<Scholarship> all = new ArrayList<>(), visible = new ArrayList<>();
    private ScholarshipResultAdapter adapter;
    private String query = "";
    private boolean loading;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_saved_scholarships);
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
        findViewById(R.id.btn_back_saved_scholarships).setOnClickListener(v -> finish());
        findViewById(R.id.chip_bookmarked_posts).setVisibility(View.GONE);
        findViewById(R.id.chip_followed_scholarships).setOnClickListener(v -> load());
        RecyclerView list = findViewById(R.id.recycler_saved_scholarships);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ScholarshipResultAdapter(visible, id -> remove(id));
        list.setAdapter(adapter);
        ((EditText) findViewById(R.id.et_saved_search))
                .addTextChangedListener(
                        new TextWatcher() {
                            public void beforeTextChanged(
                                    CharSequence s, int start, int count, int after) {}

                            public void afterTextChanged(Editable s) {}

                            public void onTextChanged(
                                    CharSequence s, int start, int before, int count) {
                                query = s.toString();
                                filter();
                            }
                        });
        findViewById(R.id.layout_empty_saved).setOnClickListener(v -> load());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ApiUi.requireLogin(this)) load();
    }

    private void load() {
        if (loading || !new TokenManager(this).isLoggedIn()) return;
        loading = true;
        ((TextView) findViewById(R.id.tv_empty_saved_text)).setText("Đang tải…");
        ApiUi.request(
                this,
                ApiClient.getApiService().getSavedScholarships(ApiUi.bearer(this)),
                result -> {
                    loading = false;
                    all.clear();
                    if (result.getData() != null) all.addAll(result.getData());
                    filter();
                },
                message -> {
                    loading = false;
                    all.clear();
                    filter();
                    ((TextView) findViewById(R.id.tv_empty_saved_text))
                            .setText(message + "\nChạm để thử lại.");
                });
    }

    private void filter() {
        visible.clear();
        String needle = query.toLowerCase(java.util.Locale.ROOT);
        for (Scholarship s : all)
            if ((s.getTitle() + " " + s.getOrganization())
                    .toLowerCase(java.util.Locale.ROOT)
                    .contains(needle)) visible.add(s);
        adapter.notifyDataSetChanged();
        findViewById(R.id.layout_empty_saved)
                .setVisibility(visible.isEmpty() ? View.VISIBLE : View.GONE);
        ((TextView) findViewById(R.id.tv_empty_saved_text))
                .setText(
                        all.isEmpty()
                                ? "Bạn chưa lưu học bổng nào. Chạm để tải lại."
                                : "Không có kết quả phù hợp.");
    }

    private void remove(String id) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage("Bỏ lưu học bổng này?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton(
                        "Bỏ lưu",
                        (dialog, which) ->
                                ApiUi.request(
                                        this,
                                        ApiClient.getApiService()
                                                .unsaveScholarship(ApiUi.bearer(this), id),
                                        result -> {
                                            all.removeIf(s -> s.getId().equals(id));
                                            filter();
                                        },
                                        message -> ApiUi.error(this, message)))
                .show();
    }
}
