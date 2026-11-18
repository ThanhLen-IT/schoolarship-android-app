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

import retrofit2.Call;

import java.util.ArrayList;
import java.util.List;

public class MentorRankingActivity extends AppCompatActivity {

    public static final String EXTRA_SCHOLARSHIP_NAME = "extra_scholarship_name";

    private final List<Scholarship> scholarshipOptions = new ArrayList<>();
    private int scholarshipPage = 0, scholarshipPages = 1;
    private boolean picking;
    private Call<ApiResponse<List<MentorRankingItem>>> rankingCall;
    private int rankingPage = 0, rankingPages = 0;
    private boolean loadingRanking;
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
        }
        etFilterScholarship.setFocusable(false);
        etFilterScholarship.setOnClickListener(v -> showScholarshipPicker());
        tvScholarshipTitle.setOnClickListener(v -> showScholarshipPicker());
        findViewById(R.id.btn_reset_scholarship)
                .setOnClickListener(
                        v -> {
                            selectedScholarshipId = "";
                            updateScholarship();
                        });
        Button btnResetMentor = findViewById(R.id.btn_reset_mentor);

        if (btnResetMentor != null) {
            btnResetMentor.setOnClickListener(
                    v -> {
                        if (etFilterMentor != null) etFilterMentor.setText("");
                    });
        }

        // Load Data
        allMentors = new ArrayList<>();

        filteredMentors = new ArrayList<>(allMentors);
        RecyclerView recyclerView = findViewById(R.id.recycler_mentor_ranking);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter =
                    new MentorRankingAdapter(
                            filteredMentors,
                            item ->
                                    startActivity(
                                            new Intent(this, MentorDetailActivity.class)
                                                    .putExtra("mentor_id", item.getMentorId())
                                                    .putExtra(
                                                            "scholarship_id",
                                                            selectedScholarshipId)));
            recyclerView.setAdapter(adapter);
        }

        // Filter TextWatchers
        findViewById(R.id.btn_more_ranking)
                .setOnClickListener(
                        v -> {
                            if (!loadingRanking
                                    && !selectedScholarshipId.isEmpty()
                                    && rankingPage < rankingPages)
                                fetchMentorRankingFromApi(
                                        Integer.parseInt(selectedScholarshipId), rankingPage + 1);
                        });
        setupFilterListeners();
        if (selectedScholarshipId.isEmpty()) updateScholarship();
        else {
            tvScholarshipTitle.setText(getIntent().getStringExtra(EXTRA_SCHOLARSHIP_NAME));
            updateScholarship();
        }
        recyclerView.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView view, int dx, int dy) {
                        if (dy > 0
                                && !view.canScrollVertically(1)
                                && !loadingRanking
                                && rankingPage < rankingPages)
                            fetchMentorRankingFromApi(
                                    Integer.parseInt(selectedScholarshipId), rankingPage + 1);
                    }
                });
        tvScholarshipTitle.setOnLongClickListener(
                v -> {
                    if (!loadingRanking
                            && !selectedScholarshipId.isEmpty()
                            && rankingPage < rankingPages)
                        fetchMentorRankingFromApi(
                                Integer.parseInt(selectedScholarshipId), rankingPage + 1);
                    return true;
                });

        // Setup Bottom Navigation
        setupBottomNavigation();
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        state.putString("scholarship_id", selectedScholarshipId);
        super.onSaveInstanceState(state);
    }

    private void showScholarshipPicker() {
        if (scholarshipOptions.isEmpty()) {
            loadScholarshipOptions(1);
            return;
        }
        List<String> titles = new ArrayList<>();
        for (Scholarship item : scholarshipOptions) titles.add(item.getTitle());
        boolean more = scholarshipPage < scholarshipPages;
        if (more) titles.add("Tải thêm học bổng…");
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(R.string.choose_scholarship)
                .setItems(
                        titles.toArray(new String[0]),
                        (dialog, which) -> {
                            if (which == scholarshipOptions.size()) {
                                loadScholarshipOptions(scholarshipPage + 1);
                                return;
                            }
                            selectedScholarshipId = scholarshipOptions.get(which).getId();
                            updateScholarship();
                        })
                .setNegativeButton(R.string.cancel_button, null)
                .show();
    }

    private void loadScholarshipOptions(int page) {
        if (picking) return;
        picking = true;
        ApiUi.request(
                this,
                ApiClient.getApiService()
                        .getScholarships(
                                null, null, null, page, 100, "title_asc", null, null, null, null),
                result -> {
                    picking = false;
                    if (page == 1) scholarshipOptions.clear();
                    if (result.getData() != null) scholarshipOptions.addAll(result.getData());
                    scholarshipPage = page;
                    scholarshipPages =
                            result.getTotalPages() == null ? page : result.getTotalPages();
                    if (scholarshipOptions.isEmpty())
                        ApiUi.error(this, "Chưa có học bổng công khai để xem ranking.");
                    else showScholarshipPicker();
                },
                message -> {
                    picking = false;
                    ApiUi.error(this, message);
                });
    }

    private void updateScholarship() {
        if (rankingCall != null) rankingCall.cancel();
        loadingRanking = false;
        rankingPage = rankingPages = 0;
        allMentors.clear();
        String title = getString(R.string.choose_scholarship);
        for (Scholarship item : scholarshipOptions)
            if (item.getId().equals(selectedScholarshipId)) title = item.getTitle();
        if (!selectedScholarshipId.isEmpty()
                && title.equals(getString(R.string.choose_scholarship)))
            title = getIntent().getStringExtra(EXTRA_SCHOLARSHIP_NAME);
        tvScholarshipTitle.setText(
                title == null ? "Ranking học bổng #" + selectedScholarshipId : title);
        etFilterScholarship.setText(tvScholarshipTitle.getText());
        applyFilters();
        if (!selectedScholarshipId.isEmpty()) {
            try {
                fetchMentorRankingFromApi(Integer.parseInt(selectedScholarshipId), 1);
            } catch (NumberFormatException error) {
                ApiUi.error(this, "ID học bổng không hợp lệ.");
            }
        }
    }

    private void fetchMentorRankingFromApi(int scholarshipId, int page) {
        loadingRanking = true;
        android.widget.TextView empty = findViewById(R.id.tv_ranking_empty);
        empty.setText("Đang tải ranking…");
        empty.setVisibility(filteredMentors.isEmpty() ? View.VISIBLE : View.GONE);
        rankingCall =
                ApiUi.request(
                        this,
                        ApiClient.getApiService().getMentorRanking(scholarshipId, page, 20),
                        result -> {
                            loadingRanking = false;
                            rankingPage = page;
                            rankingPages =
                                    result.getTotalPages() == null ? page : result.getTotalPages();
                            if (result.getData() != null) allMentors.addAll(result.getData());
                            applyFilters();
                        },
                        message -> {
                            loadingRanking = false;
                            empty.setText(message + "\nChạm để thử lại.");
                            empty.setVisibility(View.VISIBLE);
                            empty.setOnClickListener(
                                    v -> fetchMentorRankingFromApi(scholarshipId, page));
                        });
    }

    @Override
    protected void onDestroy() {
        if (rankingCall != null) rankingCall.cancel();
        super.onDestroy();
    }

    private void setupFilterListeners() {
        if (etFilterMentor != null) {
            etFilterMentor.addTextChangedListener(
                    new TextWatcher() {
                        @Override
                        public void beforeTextChanged(
                                CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(
                                CharSequence s, int start, int before, int count) {
                            currentMentorQuery = s.toString().trim();
                            applyFilters();
                        }

                        @Override
                        public void afterTextChanged(Editable s) {}
                    });
        }
    }

    private void applyFilters() {
        findViewById(R.id.btn_more_ranking)
                .setVisibility(
                        !loadingRanking && rankingPage < rankingPages ? View.VISIBLE : View.GONE);
        filteredMentors.clear();

        for (MentorRankingItem item : allMentors) {
            if (!TextUtils.isEmpty(currentMentorQuery)) {
                if (!item.getMentorName()
                        .toLowerCase(java.util.Locale.ROOT)
                        .contains(currentMentorQuery.toLowerCase(java.util.Locale.ROOT))) {
                    continue;
                }
            }
            filteredMentors.add(item);
        }

        if (adapter != null) adapter.notifyDataSetChanged();
        android.widget.TextView empty = findViewById(R.id.tv_ranking_empty);
        empty.setOnClickListener(
                v -> {
                    if (selectedScholarshipId.isEmpty()) showScholarshipPicker();
                    else updateScholarship();
                });
        empty.setText(
                selectedScholarshipId.isEmpty()
                        ? R.string.ranking_choose_first
                        : (allMentors.isEmpty()
                                ? R.string.ranking_no_mentors
                                : R.string.ranking_no_match));
        empty.setVisibility(filteredMentors.isEmpty() ? View.VISIBLE : View.GONE);
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
                        Intent intent = new Intent(MentorRankingActivity.this, HomeActivity.class);
                        intent.setFlags(
                                Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    });
        }

        if (navRanking != null) {
            navRanking.setOnClickListener(
                    v ->
                            Toast.makeText(this, "Already on Mentor Ranking", Toast.LENGTH_SHORT)
                                    .show());
        }

        if (navCommunity != null) {
            navCommunity.setOnClickListener(
                    v -> {
                        Intent intent =
                                new Intent(MentorRankingActivity.this, CommunityActivity.class);
                        startActivity(intent);
                        finish();
                    });
        }

        if (navMessages != null) {
            navMessages.setOnClickListener(
                    v -> {
                        Intent intent =
                                new Intent(MentorRankingActivity.this, MessagesActivity.class);
                        startActivity(intent);
                        finish();
                    });
        }

        if (navProfile != null) {
            navProfile.setOnClickListener(
                    v -> {
                        Intent intent =
                                new Intent(MentorRankingActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        finish();
                    });
        }
    }
}
