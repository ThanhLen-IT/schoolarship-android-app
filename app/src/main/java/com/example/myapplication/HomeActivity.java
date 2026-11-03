package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

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

        // Notifications button
        View btnNotifications = findViewById(R.id.btn_notifications);
        if (btnNotifications != null) {
            btnNotifications.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, NotificationsActivity.class);
                startActivity(intent);
            });
        }

        // Setup Banner Carousel (3 banners: slide_1, nen1, nen2)
        setupBannerCarousel();

        // Setup Scholarship Cards click listeners -> open ScholarshipDiscoveryActivity
        View cardScholarship1 = findViewById(R.id.card_scholarship_1);
        View cardScholarship2 = findViewById(R.id.card_scholarship_2);
        if (cardScholarship1 != null) {
            cardScholarship1.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, ScholarshipDiscoveryActivity.class);
                startActivity(intent);
            });
        }
        if (cardScholarship2 != null) {
            cardScholarship2.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, ScholarshipDiscoveryActivity.class);
                startActivity(intent);
            });
        }

        // Setup Mentor Cards click listeners
        View cardMentor1 = findViewById(R.id.card_mentor_1);
        View cardMentor2 = findViewById(R.id.card_mentor_2);
        if (cardMentor1 != null) {
            cardMentor1.setOnClickListener(v -> Toast.makeText(this, "Mentor 1 clicked", Toast.LENGTH_SHORT).show());
        }
        if (cardMentor2 != null) {
            cardMentor2.setOnClickListener(v -> Toast.makeText(this, "Mentor 2 clicked", Toast.LENGTH_SHORT).show());
        }

        // View All Scholarships -> open ScholarshipDiscoveryActivity
        TextView tvViewAllScholarships = findViewById(R.id.tv_view_all_scholarships);
        if (tvViewAllScholarships != null) {
            tvViewAllScholarships.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, ScholarshipDiscoveryActivity.class);
                startActivity(intent);
            });
        }

        TextView tvViewAllMentors = findViewById(R.id.tv_view_all_mentors);
        if (tvViewAllMentors != null) {
            tvViewAllMentors.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, MentorRankingActivity.class);
                startActivity(intent);
            });
        }

        // Bottom Navigation Tabs
        setupBottomNavigation();
    }

    private void setupBannerCarousel() {
        ViewPager2 viewPagerBanners = findViewById(R.id.view_pager_banners);
        LinearLayout layoutDots = findViewById(R.id.layout_pagination_dots);

        if (viewPagerBanners == null || layoutDots == null) return;

        List<HomeSliderItem> sliderItems = Arrays.asList(
                new HomeSliderItem(
                        "Kết nối hôm nay,\nkiến tạo tương lai",
                        "Cùng bạn khám phá học bổng và\ntìm Mentor phù hợp nhất.",
                        "Khám phá cơ hội",
                        R.drawable.slide_1
                ),
                new HomeSliderItem(
                        "Khám phá học bổng",
                        "Tìm kiếm cơ hội học tập phù hợp với bạn.",
                        "Khám phá học bổng",
                        R.drawable.nen1
                ),
                new HomeSliderItem(
                        "Kết nối cùng Mentor",
                        "Cùng Mentor phát triển hành trình học tập và sự nghiệp.",
                        "Tìm Mentor",
                        R.drawable.nen2
                )
        );

        HomeSliderAdapter adapter = new HomeSliderAdapter(sliderItems, (item, position) -> {
            Intent intent = new Intent(HomeActivity.this, ScholarshipDiscoveryActivity.class);
            startActivity(intent);
        });
        viewPagerBanners.setAdapter(adapter);

        // Setup 3 pagination dots
        ImageView[] dots = new ImageView[sliderItems.size()];
        layoutDots.removeAllViews();

        for (int i = 0; i < sliderItems.size(); i++) {
            dots[i] = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(6, 0, 6, 0);
            dots[i].setLayoutParams(params);

            dots[i].setImageResource(i == 0 ? android.R.drawable.presence_online : android.R.drawable.presence_invisible);
            layoutDots.addView(dots[i]);
        }

        viewPagerBanners.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                for (int i = 0; i < dots.length; i++) {
                    dots[i].setImageResource(i == position ? android.R.drawable.presence_online : android.R.drawable.presence_invisible);
                }
            }
        });
    }

    private void setupBottomNavigation() {
        View navHome = findViewById(R.id.nav_home);
        View navRanking = findViewById(R.id.nav_mentor_ranking);
        View navCommunity = findViewById(R.id.nav_community);
        View navMessages = findViewById(R.id.nav_messages);
        View navProfile = findViewById(R.id.nav_profile);

        if (navHome != null) navHome.setOnClickListener(v -> Toast.makeText(this, "Already on Home", Toast.LENGTH_SHORT).show());
        if (navRanking != null) {
            navRanking.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, MentorRankingActivity.class);
                startActivity(intent);
            });
        }
        if (navCommunity != null) navCommunity.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CommunityActivity.class);
            startActivity(intent);
        });
        if (navMessages != null) {
            navMessages.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, MessagesActivity.class);
                startActivity(intent);
            });
        }
        if (navProfile != null) navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    // Banner Adapter for ViewPager2
    private static class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
        private final List<Integer> images;

        public BannerAdapter(List<Integer> images) {
            this.images = images;
        }

        @NonNull
        @Override
        public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
            view.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            return new BannerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
            holder.imageView.setImageResource(images.get(position));
            holder.ctaButton.setOnClickListener(v -> Toast.makeText(v.getContext(), "Banner CTA clicked", Toast.LENGTH_SHORT).show());
        }

        @Override
        public int getItemCount() {
            return images.size();
        }

        static class BannerViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView ctaButton;

            public BannerViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.iv_banner_image);
                ctaButton = itemView.findViewById(R.id.btn_banner_cta);
            }
        }
    }
}
