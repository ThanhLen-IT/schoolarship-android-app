package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class NotificationsActivity extends AppCompatActivity {

    private List<NotificationItem> notificationList;
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notifications);

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
        ImageButton btnBack = findViewById(R.id.btn_back_notifications);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Load Sample Notifications
        loadNotifications();

        RecyclerView recyclerView = findViewById(R.id.recycler_notifications);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new NotificationAdapter(notificationList);
            recyclerView.setAdapter(adapter);
        }

        // Mark All as Read
        Button btnMarkAllRead = findViewById(R.id.btn_mark_all_read);
        if (btnMarkAllRead != null) {
            btnMarkAllRead.setOnClickListener(v -> {
                for (NotificationItem item : notificationList) {
                    item.setRead(true);
                }
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                Toast.makeText(this, "Đã đánh dấu tất cả là đã đọc", Toast.LENGTH_SHORT).show();
            });
        }

        // Setup Bottom Navigation
        setupBottomNavigation();
    }

    private void loadNotifications() {
        notificationList = new ArrayList<>();

        // Notification 1 (Unread)
        notificationList.add(new NotificationItem(
                "1",
                "Chúc mừng! Bạn vừa đạt Top #99 Học bổng NUS Singapore.",
                "5 phút trước",
                R.drawable.ic_star,
                false
        ));

        // Notification 2 (Unread)
        notificationList.add(new NotificationItem(
                "2",
                "Bạn đã giữ Streak 7 ngày liên tiếp và nhận thêm 15 XP.",
                "2 giờ trước",
                R.drawable.ic_star,
                false
        ));

        // Notification 3 (Unread)
        notificationList.add(new NotificationItem(
                "3",
                "Minh Anh đã thả tim bài viết \"Kinh nghiệm xin LOR\" của bạn.",
                "18 phút trước",
                R.drawable.ic_star,
                false
        ));

        // Notification 4 (Unread)
        notificationList.add(new NotificationItem(
                "4",
                "Học bổng MEXT Nhật Bản bạn theo dõi sắp hết hạn trong 3 ngày nữa!",
                "1 giờ trước",
                R.drawable.ic_calendar,
                false
        ));

        // Notification 5 (Read)
        notificationList.add(new NotificationItem(
                "5",
                "Bạn đã lọt Top 100 Mentor tuần này với thứ hạng #42.",
                "Hôm qua",
                R.drawable.ic_star,
                true
        ));

        // Notification 6 (Read)
        notificationList.add(new NotificationItem(
                "6",
                "Bùi Khánh Linh đã thả tim bài viết \"Lời khuyên chuẩn bị phỏng vấn\" của bạn.",
                "Hôm qua",
                R.drawable.ic_star,
                true
        ));

        // Notification 7 (Read)
        notificationList.add(new NotificationItem(
                "7",
                "Học bổng Fulbright Việt Nam đã mở nhận hồ sơ cho mùa tuyển sinh mới.",
                "2 ngày trước",
                R.drawable.ic_calendar,
                true
        ));
    }

    private void setupBottomNavigation() {
        View navHome = findViewById(R.id.nav_home);
        View navRanking = findViewById(R.id.nav_mentor_ranking);
        View navCommunity = findViewById(R.id.nav_community);
        View navMessages = findViewById(R.id.nav_messages);
        View navProfile = findViewById(R.id.nav_profile);

        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                Intent intent = new Intent(NotificationsActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        if (navRanking != null) {
            navRanking.setOnClickListener(v -> {
                Intent intent = new Intent(NotificationsActivity.this, MentorRankingActivity.class);
                startActivity(intent);
                finish();
            });
        }

        if (navCommunity != null) {
            navCommunity.setOnClickListener(v -> {
                Intent intent = new Intent(NotificationsActivity.this, CommunityActivity.class);
                startActivity(intent);
                finish();
            });
        }

        if (navMessages != null) {
            navMessages.setOnClickListener(v -> {
                Intent intent = new Intent(NotificationsActivity.this, MessagesActivity.class);
                startActivity(intent);
                finish();
            });
        }

        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                Intent intent = new Intent(NotificationsActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }
}
