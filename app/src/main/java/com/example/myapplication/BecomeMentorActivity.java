package com.example.myapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BecomeMentorActivity extends AppCompatActivity {

    private List<ScholarshipVerifyItem> allScholarships = new ArrayList<>();
    private int page = 0, pages = 1;
    private boolean loading;
    private List<ScholarshipVerifyItem> filteredScholarships;
    private ScholarshipVerifyAdapter adapter;

    private String currentSearchQuery = "";
    private String selectedStatusFilter = "All";

    private TextView btnVerificationStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_become_mentor);

        View rootView = findViewById(R.id.main);
        if (rootView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(
                    rootView,
                    (v, insets) -> {
                        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

                        v.setPadding(
                                systemBars.left,
                                systemBars.top,
                                systemBars.right,
                                systemBars.bottom);

                        return insets;
                    });
        }

        // Back button
        ImageButton btnBack = findViewById(R.id.btn_back_become_mentor);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Initialize Search & Filter
        EditText etSearch = findViewById(R.id.et_scholarship_search);
        btnVerificationStatus = findViewById(R.id.btn_verification_status);

        // Load 3 reference scholarships

        filteredScholarships = new ArrayList<>(allScholarships);
        RecyclerView recyclerView = findViewById(R.id.recycler_scholarship_verify);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new ScholarshipVerifyAdapter(filteredScholarships);
            recyclerView.setAdapter(adapter);
        }

        if (ApiUi.requireLogin(this)) loadReferenceScholarships();
        recyclerView.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView view, int dx, int dy) {
                        if (dy > 0 && !view.canScrollVertically(1) && !loading && page < pages)
                            loadReferenceScholarships();
                    }
                });
        btnVerificationStatus.setOnLongClickListener(
                v -> {
                    startActivity(
                            new android.content.Intent(this, VerificationStatusActivity.class));
                    return true;
                });

        // Search Listener
        if (etSearch != null) {
            etSearch.addTextChangedListener(
                    new TextWatcher() {
                        @Override
                        public void beforeTextChanged(
                                CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(
                                CharSequence s, int start, int before, int count) {
                            currentSearchQuery = s.toString().trim();
                            applyFilterAndSearch();
                        }

                        @Override
                        public void afterTextChanged(Editable s) {}
                    });
        }

        // Verification Status Filter Button
        if (btnVerificationStatus != null) {
            btnVerificationStatus.setOnClickListener(v -> showStatusFilterDialog());
        }
    }

    private void loadReferenceScholarships() {
        if (loading) return;
        loading = true;
        ApiUi.request(
                this,
                ApiClient.getApiService()
                        .getScholarships(
                                null,
                                null,
                                null,
                                page + 1,
                                20,
                                "title_asc",
                                null,
                                null,
                                null,
                                null),
                result -> {
                    loading = false;
                    page++;
                    pages = result.getTotalPages() == null ? page : result.getTotalPages();
                    if (result.getData() != null)
                        for (Scholarship item : result.getData())
                            allScholarships.add(
                                    new ScholarshipVerifyItem(
                                            item.getId(),
                                            item.getTitle(),
                                            item.getOrganization(),
                                            "Chọn để nộp hồ sơ"));
                    applyFilterAndSearch();
                    if (allScholarships.isEmpty())
                        ApiUi.error(this, "Chưa có học bổng công khai để nộp hồ sơ.");
                },
                message -> {
                    loading = false;
                    ApiUi.error(this, message);
                });
    }

    private void showStatusFilterDialog() {
        new AlertDialog.Builder(this)
                .setItems(
                        new String[] {"Xem trạng thái hồ sơ gần nhất", "Tải thêm / thử lại"},
                        (d, w) -> {
                            if (w == 0)
                                startActivity(
                                        new android.content.Intent(
                                                this, VerificationStatusActivity.class));
                            else loadReferenceScholarships();
                        })
                .show();
    }

    private void applyFilterAndSearch() {
        filteredScholarships.clear();

        for (ScholarshipVerifyItem item : allScholarships) {
            // Search query filter
            if (!TextUtils.isEmpty(currentSearchQuery)) {
                boolean matchesTitle =
                        item.getTitle().toLowerCase().contains(currentSearchQuery.toLowerCase());
                boolean matchesInst =
                        item.getInstitution()
                                .toLowerCase()
                                .contains(currentSearchQuery.toLowerCase());
                if (!matchesTitle && !matchesInst) {
                    continue;
                }
            }

            // Status filter
            if (!"All".equalsIgnoreCase(selectedStatusFilter)
                    && !item.getVerificationStatus().equalsIgnoreCase(selectedStatusFilter)) {
                continue;
            }

            filteredScholarships.add(item);
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
