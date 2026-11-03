package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
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

public class CommunityActivity extends AppCompatActivity {

    private List<CommunityPost> allPosts;
    private List<CommunityPost> filteredPosts;
    private CommunityPostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_community);

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

        // Top Action: Write post
        View btnWritePost = findViewById(R.id.btn_write_post);
        if (btnWritePost != null) {
            btnWritePost.setOnClickListener(v -> {
                Intent intent = new Intent(CommunityActivity.this, CreatePostActivity.class);
                startActivity(intent);
            });
        }

        // Search Bar
        EditText etSearch = findViewById(R.id.et_community_search);

        // Load Sample Posts
        loadSamplePosts();

        filteredPosts = new ArrayList<>(allPosts);
        RecyclerView recyclerView = findViewById(R.id.recycler_community_feed);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new CommunityPostAdapter(filteredPosts);
            recyclerView.setAdapter(adapter);
        }

        // Search Listener
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterPosts(s.toString().trim());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        // Setup Bottom Navigation
        setupBottomNavigation();
    }

    private void loadSamplePosts() {
        allPosts = new ArrayList<>();

        allPosts.add(new CommunityPost(
                "1",
                "Minh An",
                R.drawable.avata_nam,
                "2 giờ trước",
                true,
                getString(R.string.sample_post_content_a),
                R.drawable.nen1,
                188,
                342,
                718,
                false,
                false
        ));

        allPosts.add(new CommunityPost(
                "2",
                "Hà Linh",
                R.drawable.avata_nu,
                "5 giờ trước",
                true,
                getString(R.string.sample_post_content_b),
                R.drawable.nen2,
                245,
                180,
                512,
                true,
                true
        ));
    }

    private void filterPosts(String query) {
        filteredPosts.clear();
        if (TextUtils.isEmpty(query)) {
            filteredPosts.addAll(allPosts);
        } else {
            for (CommunityPost post : allPosts) {
                if (post.getContent().toLowerCase().contains(query.toLowerCase()) ||
                    post.getAuthorName().toLowerCase().contains(query.toLowerCase())) {
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
            navHome.setOnClickListener(v -> {
                Intent intent = new Intent(CommunityActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        if (navRanking != null) {
            navRanking.setOnClickListener(v -> {
                Intent intent = new Intent(CommunityActivity.this, MentorRankingActivity.class);
                startActivity(intent);
                finish();
            });
        }

        if (navCommunity != null) {
            navCommunity.setOnClickListener(v -> Toast.makeText(this, "Already on Community", Toast.LENGTH_SHORT).show());
        }

        if (navMessages != null) {
            navMessages.setOnClickListener(v -> {
                Intent intent = new Intent(CommunityActivity.this, MessagesActivity.class);
                startActivity(intent);
                finish();
            });
        }

        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                Intent intent = new Intent(CommunityActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }
}
