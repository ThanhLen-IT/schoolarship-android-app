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
import android.widget.TextView;
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

public class MentorRankingActivity extends AppCompatActivity {

    public static final String EXTRA_SCHOLARSHIP_NAME = "extra_scholarship_name";

    private List<MentorRankingItem> allMentors;
    private List<MentorRankingItem> filteredMentors;
    private MentorRankingAdapter adapter;

    private TextView tvScholarshipTitle;
    private EditText etFilterScholarship, etFilterMentor;

    private String currentScholarshipQuery = "";
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

        // Title
        tvScholarshipTitle = findViewById(R.id.tv_scholarship_ranking_title);
        String passedScholarship = getIntent().getStringExtra(EXTRA_SCHOLARSHIP_NAME);
        if (tvScholarshipTitle != null && !TextUtils.isEmpty(passedScholarship)) {
            tvScholarshipTitle.setText(passedScholarship);
        }

        // Filters
        etFilterScholarship = findViewById(R.id.et_filter_scholarship);
        etFilterMentor = findViewById(R.id.et_filter_mentor);
        Button btnResetScholarship = findViewById(R.id.btn_reset_scholarship);
        Button btnResetMentor = findViewById(R.id.btn_reset_mentor);

        if (btnResetScholarship != null) {
            btnResetScholarship.setOnClickListener(v -> {
                if (etFilterScholarship != null) etFilterScholarship.setText("");
            });
        }

        if (btnResetMentor != null) {
            btnResetMentor.setOnClickListener(v -> {
                if (etFilterMentor != null) etFilterMentor.setText("");
            });
        }

        // Load Data
        loadMentorData();

        filteredMentors = new ArrayList<>(allMentors);
        RecyclerView recyclerView = findViewById(R.id.recycler_mentor_ranking);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new MentorRankingAdapter(filteredMentors);
            recyclerView.setAdapter(adapter);
        }

        // Filter TextWatchers
        setupFilterListeners();

        // Setup Bottom Navigation
        setupBottomNavigation();
    }

    private void loadMentorData() {
        allMentors = new ArrayList<>();
        allMentors.add(new MentorRankingItem(1, "Nguyen Kim Loan", "92%", "4.9/5", "96%", "98%", "3 years"));
        allMentors.add(new MentorRankingItem(2, "Minh An", "88%", "4.8/5", "93%", "95%", "2 years"));
        allMentors.add(new MentorRankingItem(3, "Ha Linh", "85%", "4.7/5", "91%", "94%", "2 years"));
        allMentors.add(new MentorRankingItem(4, "Tran Minh Hieu", "82%", "4.6/5", "89%", "91%", "3 years"));
        allMentors.add(new MentorRankingItem(5, "Le Thu Mai", "80%", "4.5/5", "88%", "90%", "2 years"));
        allMentors.add(new MentorRankingItem(6, "Pham Thi Hoa", "78%", "4.5/5", "85%", "89%", "1 year"));
        allMentors.add(new MentorRankingItem(7, "Nguyen Anh", "76%", "4.4/5", "84%", "87%", "2 years"));
        allMentors.add(new MentorRankingItem(8, "Tran Hoang Nam", "74%", "4.3/5", "82%", "85%", "1 year"));
        allMentors.add(new MentorRankingItem(9, "Kim Ngan", "72%", "4.2/5", "80%", "83%", "1 year"));
        allMentors.add(new MentorRankingItem(10, "Thu Tram", "70%", "4.1/5", "78%", "82%", "1 year"));
    }

    private void setupFilterListeners() {
        if (etFilterScholarship != null) {
            etFilterScholarship.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    currentScholarshipQuery = s.toString().trim();
                    if (!TextUtils.isEmpty(currentScholarshipQuery) && tvScholarshipTitle != null) {
                        tvScholarshipTitle.setText(currentScholarshipQuery);
                    } else if (tvScholarshipTitle != null) {
                        tvScholarshipTitle.setText(R.string.default_scholarship_ranking_name);
                    }
                    applyFilters();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

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
                if (!item.getMentorName().toLowerCase().contains(currentMentorQuery.toLowerCase())) {
                    continue;
                }
            }
            filteredMentors.add(item);
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
