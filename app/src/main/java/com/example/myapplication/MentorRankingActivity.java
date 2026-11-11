package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MentorRankingActivity extends AppCompatActivity {

    public static final String EXTRA_SCHOLARSHIP_NAME = "extra_scholarship_name";

    private List<MentorRankingItem> allMentors;
    private List<MentorRankingItem> filteredMentors;
    private MentorRankingAdapter adapter;

    private android.widget.TextView tvScholarshipTitle;
    public static final String EXTRA_SCHOLARSHIP_ID = "extra_scholarship_id";
    private String selectedScholarshipId = "";
    private EditText etFilterScholarship, etFilterMentor;

    private String currentMentorQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mentor_ranking);

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
        ImageButton btnBack = findViewById(R.id.btn_back_mentor_ranking);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Scholarship selector and mentor search
        tvScholarshipTitle = findViewById(R.id.tv_scholarship_ranking_title);
        etFilterScholarship = findViewById(R.id.et_filter_scholarship);
        etFilterMentor = findViewById(R.id.et_filter_mentor);

        if (savedInstanceState != null) {
            selectedScholarshipId = savedInstanceState.getString("scholarship_id", "");
        } else {
            selectedScholarshipId = getIntent().getStringExtra(EXTRA_SCHOLARSHIP_ID);
            if (selectedScholarshipId == null) selectedScholarshipId = "";
            if (selectedScholarshipId.isEmpty()) {
                String name = getIntent().getStringExtra(EXTRA_SCHOLARSHIP_NAME);
                for (Scholarship scholarship : LocalDemoCatalog.scholarships()) {
                    if (scholarship.getTitle().equals(name)) selectedScholarshipId = scholarship.getId();
                }
            }
        }
        etFilterScholarship.setFocusable(false);
        etFilterScholarship.setOnClickListener(v -> showScholarshipPicker());
        tvScholarshipTitle.setOnClickListener(v -> showScholarshipPicker());
        findViewById(R.id.btn_reset_scholarship).setOnClickListener(v -> {
            selectedScholarshipId = "";
            updateScholarship();
        });
        Button btnResetMentor = findViewById(R.id.btn_reset_mentor);

        if (btnResetMentor != null) {
            btnResetMentor.setOnClickListener(v -> {
                if (etFilterMentor != null) etFilterMentor.setText("");
            });
        }

        // Load Data
        allMentors = new ArrayList<>();

        filteredMentors = new ArrayList<>(allMentors);
        RecyclerView recyclerView = findViewById(R.id.recycler_mentor_ranking);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new MentorRankingAdapter(filteredMentors);
            recyclerView.setAdapter(adapter);
        }

        // Filter TextWatchers
        setupFilterListeners();
        updateScholarship();

        // Setup Bottom Navigation
        setupBottomNavigation();
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        state.putString("scholarship_id", selectedScholarshipId);
        super.onSaveInstanceState(state);
    }

    private void showScholarshipPicker() {
        List<Scholarship> scholarships = LocalDemoCatalog.scholarships();
        String[] titles = new String[scholarships.size()];
        int checked = -1;
        for (int i = 0; i < titles.length; i++) {
            titles[i] = scholarships.get(i).getTitle();
            if (scholarships.get(i).getId().equals(selectedScholarshipId)) checked = i;
        }
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(R.string.choose_scholarship)
                .setSingleChoiceItems(titles, checked, (dialog, which) -> {
                    selectedScholarshipId = scholarships.get(which).getId();
                    updateScholarship();
                    dialog.dismiss();
                }).setNegativeButton(R.string.cancel_button, null).show();
    }

    private void updateScholarship() {
        Scholarship scholarship = LocalDemoCatalog.find(selectedScholarshipId);
        String title = scholarship == null ? getString(R.string.choose_scholarship) : scholarship.getTitle();
        tvScholarshipTitle.setText(title);
        etFilterScholarship.setText(title);
        allMentors = LocalDemoCatalog.mentors(selectedScholarshipId);
        applyFilters();

        if (!selectedScholarshipId.isEmpty()) {
            try {
                int scholarshipNumId = Integer.parseInt(selectedScholarshipId);
                fetchMentorRankingFromApi(scholarshipNumId);
            } catch (NumberFormatException ignored) {}
        }
    }

    private void fetchMentorRankingFromApi(int scholarshipId) {
        ApiClient.getApiService().getMentorRanking(scholarshipId, 1, 20)
                .enqueue(new Callback<ApiResponse<List<MentorRankingItem>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<MentorRankingItem>>> call, Response<ApiResponse<List<MentorRankingItem>>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            List<MentorRankingItem> apiList = response.body().getData();
                            if (apiList != null && !apiList.isEmpty()) {
                                allMentors.clear();
                                allMentors.addAll(apiList);
                                applyFilters();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<MentorRankingItem>>> call, Throwable t) {
                        // Fallback to local catalog
                    }
                });
    }

    private void setupFilterListeners() {
        if (etFilterMentor != null) {
            etFilterMentor.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    currentMentorQuery = s.toString().trim();
                    applyFilters();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void applyFilters() {
        filteredMentors.clear();

        for (MentorRankingItem item : allMentors) {
            if (!TextUtils.isEmpty(currentMentorQuery)) {
                if (!item.getMentorName().toLowerCase(java.util.Locale.ROOT).contains(currentMentorQuery.toLowerCase(java.util.Locale.ROOT))) {
                    continue;
                }
            }
            filteredMentors.add(item);
        }

        if (adapter != null) adapter.notifyDataSetChanged();
        android.widget.TextView empty = findViewById(R.id.tv_ranking_empty);
        empty.setText(selectedScholarshipId.isEmpty() ? R.string.ranking_choose_first :
                (allMentors.isEmpty() ? R.string.ranking_no_mentors : R.string.ranking_no_match));
        empty.setVisibility(filteredMentors.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void setupBottomNavigation() {
        View navHome = findViewById(R.id.nav_home);
        View navRanking = findViewById(R.id.nav_mentor_ranking);
        View navCommunity = findViewById(R.id.nav_community);
        View navMessages = findViewById(R.id.nav_messages);
        View navProfile = findViewById(R.id.nav_profile);

        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                Intent intent = new Intent(MentorRankingActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        if (navRanking != null) {
            navRanking.setOnClickListener(v -> Toast.makeText(this, "Already on Mentor Ranking", Toast.LENGTH_SHORT).show());
        }

        if (navCommunity != null) {
            navCommunity.setOnClickListener(v -> {
                Intent intent = new Intent(MentorRankingActivity.this, CommunityActivity.class);
                startActivity(intent);
                finish();
            });
        }

        if (navMessages != null) {
            navMessages.setOnClickListener(v -> {
                Intent intent = new Intent(MentorRankingActivity.this, MessagesActivity.class);
                startActivity(intent);
                finish();
            });
        }

        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                Intent intent = new Intent(MentorRankingActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }
}
