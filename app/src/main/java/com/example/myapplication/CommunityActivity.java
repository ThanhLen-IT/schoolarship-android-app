package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CommunityActivity extends AppCompatActivity {

    private List<CommunityPost> allPosts;
    private String serverQuery = "";
    private int page = 0, pages = 0;
    private boolean loading;
    private retrofit2.Call<ApiResponse<List<CommunityPost>>> feedCall;
    private final android.os.Handler handler =
            new android.os.Handler(android.os.Looper.getMainLooper());
    private final Runnable searchTask = () -> loadPosts(1);
    private List<CommunityPost> filteredPosts;
    private CommunityPostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_community);

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

        // Top Action: Write post
        View btnWritePost = findViewById(R.id.btn_write_post);
        if (btnWritePost != null) {
            btnWritePost.setOnClickListener(
                    v -> {
                        Intent intent =
                                new Intent(CommunityActivity.this, CreatePostActivity.class);
                        startActivity(intent);
                    });
        }

        // Search Bar
        EditText etSearch = findViewById(R.id.et_community_search);

        // Load Sample Posts
        allPosts = new ArrayList<>();

        filteredPosts = new ArrayList<>(allPosts);
        RecyclerView recyclerView = findViewById(R.id.recycler_community_feed);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new CommunityPostAdapter(filteredPosts, () -> loadPosts(1));
            recyclerView.setAdapter(adapter);
        }

        recyclerView.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView view, int dx, int dy) {
                        if (dy > 0 && !view.canScrollVertically(1) && !loading && page < pages)
                            loadPosts(page + 1);
                    }
                });
        findViewById(R.id.nav_community)
                .setOnLongClickListener(
                        v -> {
                            loadPosts(1);
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
                            serverQuery = s.toString().trim();
                            if (feedCall != null) feedCall.cancel();
                            handler.removeCallbacks(searchTask);
                            handler.postDelayed(searchTask, 350);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {}
                    });
        }

        // Setup Bottom Navigation
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPosts(1);
    }

    private void loadPosts(int requested) {
        if (feedCall != null) feedCall.cancel();
        loading = true;
        feedCall =
                ApiUi.request(
                        this,
                        ApiClient.getApiService()
                                .getPosts(
                                        serverQuery.isEmpty() ? null : serverQuery, requested, 20),
                        result -> {
                            loading = false;
                            page = requested;
                            pages =
                                    result.getTotalPages() == null
                                            ? requested
                                            : result.getTotalPages();
                            if (requested == 1) allPosts.clear();
                            if (result.getData() != null) allPosts.addAll(result.getData());
                            filterPosts("");
                            if (allPosts.isEmpty()) ApiUi.error(this, "Chưa có bài viết phù hợp.");
                        },
                        message -> {
                            loading = false;
                            ApiUi.error(this, message + " Giữ tab Cộng đồng để thử lại.");
                        });
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        if (feedCall != null) feedCall.cancel();
        super.onDestroy();
    }

    private void filterPosts(String query) {
        filteredPosts.clear();
        if (TextUtils.isEmpty(query)) {
            filteredPosts.addAll(allPosts);
        } else {
            for (CommunityPost post : allPosts) {
                if (post.getContent().toLowerCase().contains(query.toLowerCase())
                        || post.getAuthorName().toLowerCase().contains(query.toLowerCase())) {
                    filteredPosts.add(post);
                }
            }
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
                        Intent intent = new Intent(CommunityActivity.this, HomeActivity.class);
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
                                new Intent(CommunityActivity.this, MentorRankingActivity.class);
                        startActivity(intent);
                        finish();
                    });
        }

        if (navCommunity != null) {
            navCommunity.setOnClickListener(v -> getWindow().getDecorView().requestFocus());
        }

        if (navMessages != null) {
            navMessages.setOnClickListener(
                    v -> {
                        Intent intent = new Intent(CommunityActivity.this, MessagesActivity.class);
                        startActivity(intent);
                        finish();
                    });
        }

        if (navProfile != null) {
            navProfile.setOnClickListener(
                    v -> {
                        Intent intent = new Intent(CommunityActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        finish();
                    });
        }
    }
}
