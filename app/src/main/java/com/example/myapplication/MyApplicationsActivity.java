package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyApplicationsActivity extends AppCompatActivity {

    private List<MyApplicationItem> allApplications = new ArrayList<>();
    private List<MyApplicationItem> filteredApplications;
    private MyApplicationAdapter adapter;

    private String currentSearchQuery = "";
    private String currentStatusFilter = "All";

    private TextView filterAll, filterPending, filterAccepted, filterRejected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_applications);

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
        ImageButton btnBack = findViewById(R.id.btn_back_my_applications);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Search Input
        EditText etSearch = findViewById(R.id.et_app_search);

        // Filter Tabs
        filterAll = findViewById(R.id.filter_all);
        filterPending = findViewById(R.id.filter_pending);
        filterAccepted = findViewById(R.id.filter_accepted);
        filterRejected = findViewById(R.id.filter_rejected);

        if (filterAll != null) filterAll.setOnClickListener(v -> selectFilter("All", filterAll));
        if (filterPending != null)
            filterPending.setOnClickListener(v -> selectFilter("Đang chờ phản hồi", filterPending));
        if (filterAccepted != null)
            filterAccepted.setOnClickListener(v -> selectFilter("Đã được nhận", filterAccepted));
        if (filterRejected != null)
            filterRejected.setOnClickListener(v -> selectFilter("Chưa phù hợp", filterRejected));

        // Load Applications

        filteredApplications = new ArrayList<>(allApplications);
        RecyclerView recyclerView = findViewById(R.id.recycler_my_applications);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new MyApplicationAdapter(filteredApplications);
            recyclerView.setAdapter(adapter);
        }

        // Search TextWatcher
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
                            applySearchAndFilter();
                        }

                        @Override
                        public void afterTextChanged(Editable s) {}
                    });
        }

        if (ApiUi.requireLogin(this)) loadApplications();
        findViewById(R.id.nav_profile)
                .setOnLongClickListener(
                        v -> {
                            loadApplications();
                            return true;
                        });
        // Setup Bottom Navigation
        setupBottomNavigation();
    }

    private void loadApplications() {
        ApiUi.request(
                this,
                ApiClient.getApiService().myApplications(ApiUi.bearer(this)),
                result -> {
                    allApplications.clear();
                    if (result.getData() != null)
                        for (ApplicationData row : result.getData()) {
                            String status =
                                    "accepted".equals(row.status)
                                            ? "Đã được nhận"
                                            : "pending".equals(row.status)
                                                    ? "Đang chờ phản hồi"
                                                    : "rejected".equals(row.status)
                                                            ? "Chưa phù hợp"
                                                            : row.status;
                            allApplications.add(
                                    new MyApplicationItem(
                                            row.id,
                                            row.mentor_name,
                                            row.scholarship_title,
                                            row.created_at,
                                            status,
                                            row.goal,
                                            R.drawable.ic_profile_placeholder));
                        }
                    applySearchAndFilter();
                },
                message -> ApiUi.error(this, message));
    }

    private void selectFilter(String status, TextView selectedTab) {
        currentStatusFilter = status;

        // Reset all tabs background and text color
        resetTabStyle(filterAll);
        resetTabStyle(filterPending);
        resetTabStyle(filterAccepted);
        resetTabStyle(filterRejected);

        // Highlight selected tab
        if (selectedTab != null) {
            selectedTab.setBackgroundResource(R.drawable.bg_welcome_button_primary);
            selectedTab.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        }

        applySearchAndFilter();
    }

    private void resetTabStyle(TextView tab) {
        if (tab != null) {
            tab.setBackgroundResource(R.drawable.bg_input_field);
            tab.setTextColor(ContextCompat.getColor(this, R.color.color_text_primary));
        }
    }

    private void applySearchAndFilter() {
        filteredApplications.clear();

        for (MyApplicationItem item : allApplications) {
            // Search query filter
            if (!TextUtils.isEmpty(currentSearchQuery)) {
                boolean matchesName =
                        item.getMentorName()
                                .toLowerCase()
                                .contains(currentSearchQuery.toLowerCase());
                boolean matchesOrg =
                        item.getOrganization()
                                .toLowerCase()
                                .contains(currentSearchQuery.toLowerCase());
                boolean matchesObj =
                        item.getObjective()
                                .toLowerCase()
                                .contains(currentSearchQuery.toLowerCase());
                if (!matchesName && !matchesOrg && !matchesObj) {
                    continue;
                }
            }

            // Status filter
            if (!"All".equalsIgnoreCase(currentStatusFilter)
                    && !item.getStatus().equalsIgnoreCase(currentStatusFilter)) {
                continue;
            }

            filteredApplications.add(item);
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void setupBottomNavigation() {
        View navHome = findViewById(R.id.nav_home);
        View navRanking = findViewById(R.id.nav_mentor_ranking);
        View navCommunity = findViewById(R.id.nav_community);
        View navMessages = findViewById(R.id.nav_messages);
        View navProfile = findViewById(R.id.nav_profile);

        if (navHome != null) {
            navHome.setOnClickListener(
                    v -> {
                        Intent intent = new Intent(MyApplicationsActivity.this, HomeActivity.class);
                        intent.setFlags(
                                Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    });
        }

        if (navRanking != null) {
            navRanking.setOnClickListener(
                    v -> {
                        Intent intent =
                                new Intent(
                                        MyApplicationsActivity.this, MentorRankingActivity.class);
                        startActivity(intent);
                        finish();
                    });
        }

        if (navCommunity != null) {
            navCommunity.setOnClickListener(
                    v -> {
                        Intent intent =
                                new Intent(MyApplicationsActivity.this, CommunityActivity.class);
                        startActivity(intent);
                        finish();
                    });
        }

        if (navMessages != null) {
            navMessages.setOnClickListener(
                    v -> {
                        Intent intent =
                                new Intent(MyApplicationsActivity.this, MessagesActivity.class);
                        startActivity(intent);
                        finish();
                    });
        }

        if (navProfile != null) {
            navProfile.setOnClickListener(
                    v -> {
                        Intent intent =
                                new Intent(MyApplicationsActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        finish();
                    });
        }
    }
}
