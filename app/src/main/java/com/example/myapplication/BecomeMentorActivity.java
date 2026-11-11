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

    private List<ScholarshipVerifyItem> allScholarships;
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
            ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
                Insets systemBars = insets.getInsets(
                        WindowInsetsCompat.Type.systemBars()
                );

                v.setPadding(
                        systemBars.left,
                        systemBars.top,
                        systemBars.right,
                        systemBars.bottom
                );

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
        loadReferenceScholarships();

        filteredScholarships = new ArrayList<>(allScholarships);
        RecyclerView recyclerView = findViewById(R.id.recycler_scholarship_verify);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new ScholarshipVerifyAdapter(filteredScholarships);
            recyclerView.setAdapter(adapter);
        }

        // Search Listener
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
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
        allScholarships = new ArrayList<>();
        allScholarships.add(new ScholarshipVerifyItem(
                "1",
                "Global STEM Scholarship 2026",
                "University of Technology",
                "Not verified"
        ));
        allScholarships.add(new ScholarshipVerifyItem(
                "2",
                "Future Leaders Grant 2026",
                "International Business Academy",
                "Not verified"
        ));
        allScholarships.add(new ScholarshipVerifyItem(
                "3",
                "Excellence Award for Women in Tech 2026",
                "City Innovation Institute",
                "Not verified"
        ));
    }

    private void showStatusFilterDialog() {
        String[] statuses = {"All", "Not verified", "Pending verification", "Verified"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Verification Status");
        builder.setItems(statuses, (dialog, which) -> {
            selectedStatusFilter = statuses[which];
            if (btnVerificationStatus != null) {
                btnVerificationStatus.setText(
                        "All".equalsIgnoreCase(selectedStatusFilter)
                                ? getString(R.string.verification_status_filter)
                                : "Status: " + selectedStatusFilter
                );
            }
            applyFilterAndSearch();
        });
        builder.show();
    }

    private void applyFilterAndSearch() {
        filteredScholarships.clear();

        for (ScholarshipVerifyItem item : allScholarships) {
            // Search query filter
            if (!TextUtils.isEmpty(currentSearchQuery)) {
                boolean matchesTitle = item.getTitle().toLowerCase().contains(currentSearchQuery.toLowerCase());
                boolean matchesInst = item.getInstitution().toLowerCase().contains(currentSearchQuery.toLowerCase());
                if (!matchesTitle && !matchesInst) {
                    continue;
                }
            }

            // Status filter
            if (!"All".equalsIgnoreCase(selectedStatusFilter) && !item.getVerificationStatus().equalsIgnoreCase(selectedStatusFilter)) {
                continue;
            }

            filteredScholarships.add(item);
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
