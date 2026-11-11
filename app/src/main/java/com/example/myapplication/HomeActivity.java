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

        // Notifications button
        View btnNotifications = findViewById(R.id.btn_notifications);
        if (btnNotifications != null) {
            btnNotifications.setOnClickListener(
                    v -> {
                        Intent intent = new Intent(HomeActivity.this, NotificationsActivity.class);
                        startActivity(intent);
                    });
        }

        findViewById(R.id.btn_home_search)
                .setOnClickListener(
                        v -> {
                            Intent intent = new Intent(this, ScholarshipDiscoveryActivity.class);
                            intent.putExtra(ScholarshipDiscoveryActivity.EXTRA_FOCUS_SEARCH, true);
                            startActivity(intent);
                        });

        // Setup Banner Carousel (3 banners: slide_1, nen1, nen2)
        setupBannerCarousel();

        findViewById(R.id.card_scholarship_1).setVisibility(View.GONE);
        findViewById(R.id.card_scholarship_2).setVisibility(View.GONE);
        // Mentor suggestions need a scholarship context; use the real ranking picker.
        findViewById(R.id.card_mentor_1).setVisibility(View.GONE);
        findViewById(R.id.card_mentor_2).setVisibility(View.GONE);
        loadScholarships();

        // View All Scholarships -> open ScholarshipDiscoveryActivity
        TextView tvViewAllScholarships = findViewById(R.id.tv_view_all_scholarships);
        if (tvViewAllScholarships != null) {
            tvViewAllScholarships.setOnClickListener(
                    v -> {
                        Intent intent =
                                new Intent(HomeActivity.this, ScholarshipDiscoveryActivity.class);
                        startActivity(intent);
                    });
        }

        TextView tvViewAllMentors = findViewById(R.id.tv_view_all_mentors);
        if (tvViewAllMentors != null) {
            tvViewAllMentors.setOnClickListener(
                    v -> {
                        Intent intent = new Intent(HomeActivity.this, MentorRankingActivity.class);
                        startActivity(intent);
                    });
        }

        // Bottom Navigation Tabs
        setupBottomNavigation();
    }

    private void loadScholarships() {
        ApiUi.request(
                this,
                ApiClient.getApiService()
                        .getScholarships(
                                null, null, null, 1, 2, "deadline_asc", null, null, null, null),
                result -> {
                    List<Scholarship> items = result.getData();
                    TextView heading = findViewById(R.id.tv_view_all_scholarships);
                    heading.setText(
                            items == null || items.isEmpty()
                                    ? "Chưa có học bổng · Tải lại"
                                    : "Xem tất cả");
                    heading.setOnClickListener(
                            v -> {
                                if (items == null || items.isEmpty()) loadScholarships();
                                else
                                    startActivity(
                                            new Intent(this, ScholarshipDiscoveryActivity.class));
                            });
                    if (items != null && !items.isEmpty())
                        bindScholarshipCard(
                                R.id.card_scholarship_1,
                                items.get(0),
                                R.drawable.nen1,
                                R.id.home_scholarship_title_a,
                                R.id.home_scholarship_type_a,
                                R.id.home_scholarship_deadline_a,
                                R.id.home_scholarship_meta_a,
                                R.id.home_scholarship_image_a);
                    if (items != null && items.size() > 1)
                        bindScholarshipCard(
                                R.id.card_scholarship_2,
                                items.get(1),
                                R.drawable.nen2,
                                R.id.home_scholarship_title_b,
                                R.id.home_scholarship_type_b,
                                R.id.home_scholarship_deadline_b,
                                R.id.home_scholarship_meta_b,
                                R.id.home_scholarship_image_b);
                },
                message -> {
                    TextView retry = findViewById(R.id.tv_view_all_scholarships);
                    retry.setText("Lỗi tải · Chạm thử lại");
                    retry.setOnClickListener(v -> loadScholarships());
                    ApiUi.error(this, message);
                });
    }

    private void bindScholarshipCard(
            int cardId,
            Scholarship scholarship,
            int imageResId,
            int titleId,
            int typeId,
            int deadlineId,
            int metaId,
            int imageId) {
        View card = findViewById(cardId);

        if (card == null) return;
        if (scholarship == null) {
            card.setVisibility(View.GONE);
            return;
        }
        card.setVisibility(View.VISIBLE);
        RemoteImage.show(findViewById(imageId), scholarship.getImageUrl());
        ((TextView) findViewById(titleId)).setText(scholarship.getTitle());
        ((TextView) findViewById(typeId)).setText(scholarship.getFundingType());
        ((TextView) findViewById(deadlineId))
                .setText(
                        getString(
                                R.string.scholarship_deadline_value, scholarship.getDeadlineStr()));
        ((TextView) findViewById(metaId))
                .setText(scholarship.getCountry() + " · " + scholarship.getDegreeLevel());
        findViewById(imageId).setContentDescription(scholarship.getTitle());
        card.setOnClickListener(
                v -> {
                    Intent intent = new Intent(this, ScholarshipDetailActivity.class);
                    intent.putExtra(
                            ScholarshipDetailActivity.EXTRA_SCHOLARSHIP_ID, scholarship.getId());
                    intent.putExtra(
                            ScholarshipDetailActivity.EXTRA_SCHOLARSHIP_TITLE,
                            scholarship.getTitle());
                    intent.putExtra(
                            ScholarshipDetailActivity.EXTRA_SCHOLARSHIP_CATEGORY,
                            scholarship.getDegreeLevel());
                    intent.putExtra(ScholarshipDetailActivity.EXTRA_SCHOLARSHIP_IMAGE, imageResId);
                    startActivity(intent);
                });
    }

    private void setupBannerCarousel() {
        ViewPager2 viewPagerBanners = findViewById(R.id.view_pager_banners);
        LinearLayout layoutDots = findViewById(R.id.layout_pagination_dots);

        if (viewPagerBanners == null || layoutDots == null) return;

        List<HomeSliderItem> sliderItems =
                Arrays.asList(
                        new HomeSliderItem(
                                "Kết nối hôm nay,\nkiến tạo tương lai",
                                "Cùng bạn khám phá học bổng và\ntìm Mentor phù hợp nhất.",
                                "Khám phá cơ hội",
                                R.drawable.slide_1),
                        new HomeSliderItem(
                                "Khám phá học bổng",
                                "Tìm kiếm cơ hội học tập phù hợp với bạn.",
                                "Khám phá học bổng",
                                R.drawable.nen1),
                        new HomeSliderItem(
                                "Kết nối cùng Mentor",
                                "Cùng Mentor phát triển hành trình học tập và sự nghiệp.",
                                "Tìm Mentor",
                                R.drawable.nen2));

        HomeSliderAdapter adapter =
                new HomeSliderAdapter(
                        sliderItems,
                        (item, position) -> {
                            Intent intent =
                                    new Intent(
                                            HomeActivity.this, ScholarshipDiscoveryActivity.class);
                            startActivity(intent);
                        });
        viewPagerBanners.setAdapter(adapter);

        // Setup 3 pagination dots
        ImageView[] dots = new ImageView[sliderItems.size()];
        layoutDots.removeAllViews();

        for (int i = 0; i < sliderItems.size(); i++) {
            dots[i] = new ImageView(this);
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(6, 0, 6, 0);
            dots[i].setLayoutParams(params);

            dots[i].setImageResource(
                    i == 0
                            ? android.R.drawable.presence_online
                            : android.R.drawable.presence_invisible);
            layoutDots.addView(dots[i]);
        }

        viewPagerBanners.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        for (int i = 0; i < dots.length; i++) {
                            dots[i].setImageResource(
                                    i == position
                                            ? android.R.drawable.presence_online
                                            : android.R.drawable.presence_invisible);
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

        if (navHome != null)
            navHome.setOnClickListener(
                    v -> Toast.makeText(this, "Already on Home", Toast.LENGTH_SHORT).show());
        if (navRanking != null) {
            navRanking.setOnClickListener(
                    v -> {
                        Intent intent = new Intent(HomeActivity.this, MentorRankingActivity.class);
                        startActivity(intent);
                    });
        }
        if (navCommunity != null)
            navCommunity.setOnClickListener(
                    v -> {
                        Intent intent = new Intent(HomeActivity.this, CommunityActivity.class);
                        startActivity(intent);
                    });
        if (navMessages != null) {
            navMessages.setOnClickListener(
                    v -> {
                        Intent intent = new Intent(HomeActivity.this, MessagesActivity.class);
                        startActivity(intent);
                    });
        }
        if (navProfile != null)
            navProfile.setOnClickListener(
                    v -> {
                        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                        startActivity(intent);
                    });
    }

    // Banner Adapter for ViewPager2
    private static class BannerAdapter
            extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
        private final List<Integer> images;

        public BannerAdapter(List<Integer> images) {
            this.images = images;
        }

        @NonNull
        @Override
        public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view =
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_banner, parent, false);
            view.setLayoutParams(
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
            return new BannerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
            holder.imageView.setImageResource(images.get(position));
            holder.ctaButton.setOnClickListener(
                    v ->
                            Toast.makeText(v.getContext(), "Banner CTA clicked", Toast.LENGTH_SHORT)
                                    .show());
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
