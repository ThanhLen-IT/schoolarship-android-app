package com.example.myapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

public class SavedScholarshipsActivity extends AppCompatActivity {

    private List<SavedScholarshipItem> allSavedScholarships;
    private List<SavedScholarshipItem> filteredSavedScholarships;
    private SavedScholarshipAdapter scholarshipAdapter;

    private List<BookmarkedPostItem> allBookmarkedPosts;
    private List<BookmarkedPostItem> filteredBookmarkedPosts;
    private BookmarkedPostAdapter postAdapter;

    private String currentSearchQuery = "";
    private String currentFilterTab = "Scholarships"; // "Scholarships" or "Posts"

    private TextView chipFollowedScholarships, chipBookmarkedPosts;
    private View layoutEmptyState;
    private TextView tvEmptySavedText;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_saved_scholarships);

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
        ImageButton btnBack = findViewById(R.id.btn_back_saved_scholarships);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Search Field
        EditText etSearch = findViewById(R.id.et_saved_search);

        // Filter Chips & Views
        chipFollowedScholarships = findViewById(R.id.chip_followed_scholarships);
        chipBookmarkedPosts = findViewById(R.id.chip_bookmarked_posts);
        layoutEmptyState = findViewById(R.id.layout_empty_saved);
        tvEmptySavedText = findViewById(R.id.tv_empty_saved_text);
        recyclerView = findViewById(R.id.recycler_saved_scholarships);

        if (chipFollowedScholarships != null) {
            chipFollowedScholarships.setOnClickListener(v -> selectFilterTab("Scholarships"));
        }
        if (chipBookmarkedPosts != null) {
            chipBookmarkedPosts.setOnClickListener(v -> selectFilterTab("Posts"));
        }

        // Load Datasets
        loadSampleSavedScholarships();
        loadSampleBookmarkedPosts();

        filteredSavedScholarships = new ArrayList<>(allSavedScholarships);
        filteredBookmarkedPosts = new ArrayList<>(allBookmarkedPosts);

        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            scholarshipAdapter = new SavedScholarshipAdapter(filteredSavedScholarships);
            
            postAdapter = new BookmarkedPostAdapter(filteredBookmarkedPosts, (item, position) -> {
                allBookmarkedPosts.remove(item);
                filteredBookmarkedPosts.remove(item);
                if (postAdapter != null) {
                    postAdapter.notifyItemRemoved(position);
                    postAdapter.notifyItemRangeChanged(position, filteredBookmarkedPosts.size());
                }
                Toast.makeText(SavedScholarshipsActivity.this, "Đã bỏ lưu bài viết", Toast.LENGTH_SHORT).show();
                checkEmptyState();
            });

            recyclerView.setAdapter(scholarshipAdapter);
        }

        // Search Listener
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    currentSearchQuery = s.toString().trim();
                    applySearchAndFilter();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void loadSampleSavedScholarships() {
        allSavedScholarships = new ArrayList<>();
        allSavedScholarships.add(new SavedScholarshipItem(
                "1",
                "Học bổng Toàn phần Rhodes tại Oxford",
                "Anh Quốc",
                "Còn 12 ngày",
                "Bs Follow"
        ));
        allSavedScholarships.add(new SavedScholarshipItem(
                "2",
                "Học bổng Thạc sĩ Fulbright Việt Nam",
                "Hoa Kỳ",
                "Còn 45 ngày",
                "Bs Follow"
        ));
    }

    private void loadSampleBookmarkedPosts() {
        allBookmarkedPosts = new ArrayList<>();
        allBookmarkedPosts.add(new BookmarkedPostItem(
                "p1",
                "Kinh nghiệm apply NUS: xây dựng hồ sơ nhật đạt quán",
                "Dr. Nguyen",
                "#HocBongNUS"
        ));
        allBookmarkedPosts.add(new BookmarkedPostItem(
                "p2",
                "Viết bài luận Rhodes từ câu chuyện của bạn",
                "Minh Tran",
                "#HocBongOxford"
        ));
        allBookmarkedPosts.add(new BookmarkedPostItem(
                "p3",
                "Hành trình chinh phục học bổng Fulbright: từ hồ sơ đến phỏng vấn",
                "Thu La",
                "#HocBongFulbright"
        ));
        allBookmarkedPosts.add(new BookmarkedPostItem(
                "p4",
                "4 bài luận Chevening: kể câu chuyện lãnh đạo của chính mình",
                "Hoang Anh",
                "#HocBongChevening"
        ));
    }

    private void selectFilterTab(String tab) {
        currentFilterTab = tab;

        if ("Scholarships".equalsIgnoreCase(tab)) {
            if (chipFollowedScholarships != null) {
                chipFollowedScholarships.setBackgroundResource(R.drawable.bg_welcome_button_primary);
                chipFollowedScholarships.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            }
            if (chipBookmarkedPosts != null) {
                chipBookmarkedPosts.setBackgroundResource(R.drawable.bg_input_field);
                chipBookmarkedPosts.setTextColor(ContextCompat.getColor(this, R.color.color_text_primary));
            }
            if (recyclerView != null) {
                recyclerView.setAdapter(scholarshipAdapter);
            }
        } else {
            if (chipFollowedScholarships != null) {
                chipFollowedScholarships.setBackgroundResource(R.drawable.bg_input_field);
                chipFollowedScholarships.setTextColor(ContextCompat.getColor(this, R.color.color_text_primary));
            }
            if (chipBookmarkedPosts != null) {
                chipBookmarkedPosts.setBackgroundResource(R.drawable.bg_welcome_button_primary);
                chipBookmarkedPosts.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            }
            if (recyclerView != null) {
                recyclerView.setAdapter(postAdapter);
            }
        }

        applySearchAndFilter();
    }

    private void applySearchAndFilter() {
        if ("Scholarships".equalsIgnoreCase(currentFilterTab)) {
            filteredSavedScholarships.clear();
            for (SavedScholarshipItem item : allSavedScholarships) {
                if (!TextUtils.isEmpty(currentSearchQuery)) {
                    boolean matchesTitle = item.getTitle().toLowerCase().contains(currentSearchQuery.toLowerCase());
                    boolean matchesOrg = item.getOrganization().toLowerCase().contains(currentSearchQuery.toLowerCase());
                    if (!matchesTitle && !matchesOrg) {
                        continue;
                    }
                }
                filteredSavedScholarships.add(item);
            }
            if (scholarshipAdapter != null) {
                scholarshipAdapter.notifyDataSetChanged();
            }
        } else {
            filteredBookmarkedPosts.clear();
            for (BookmarkedPostItem item : allBookmarkedPosts) {
                if (!TextUtils.isEmpty(currentSearchQuery)) {
                    boolean matchesTitle = item.getTitle().toLowerCase().contains(currentSearchQuery.toLowerCase());
                    boolean matchesAuthor = item.getAuthor().toLowerCase().contains(currentSearchQuery.toLowerCase());
                    boolean matchesHashtag = item.getHashtag().toLowerCase().contains(currentSearchQuery.toLowerCase());
                    if (!matchesTitle && !matchesAuthor && !matchesHashtag) {
                        continue;
                    }
                }
                filteredBookmarkedPosts.add(item);
            }
            if (postAdapter != null) {
                postAdapter.notifyDataSetChanged();
            }
        }

        checkEmptyState();
    }

    private void checkEmptyState() {
        boolean isEmpty;
        if ("Scholarships".equalsIgnoreCase(currentFilterTab)) {
            isEmpty = filteredSavedScholarships.isEmpty();
            if (tvEmptySavedText != null) {
                tvEmptySavedText.setText(!TextUtils.isEmpty(currentSearchQuery)
                        ? getString(R.string.empty_search_results_msg)
                        : getString(R.string.empty_saved_scholarships_msg));
            }
        } else {
            isEmpty = filteredBookmarkedPosts.isEmpty();
            if (tvEmptySavedText != null) {
                tvEmptySavedText.setText(!TextUtils.isEmpty(currentSearchQuery)
                        ? getString(R.string.empty_search_posts_msg)
                        : getString(R.string.empty_bookmarked_posts_msg));
            }
        }

        if (isEmpty) {
            if (recyclerView != null) recyclerView.setVisibility(View.GONE);
            if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            if (recyclerView != null) recyclerView.setVisibility(View.VISIBLE);
            if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.GONE);
        }
    }
}
