package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileActivity extends AppCompatActivity {

    private boolean isVerifiedMentor = false;

    private View layoutMentorBadge;
    private View cardMentorRanking;
    private TextView tvMenuRoleLabel;
    private TextView tvProfileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

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

        // Initialize Views
        layoutMentorBadge = findViewById(R.id.layout_mentor_badge);
        cardMentorRanking = findViewById(R.id.card_mentor_ranking);
        tvMenuRoleLabel = findViewById(R.id.tv_menu_role_label);
        tvProfileName = findViewById(R.id.tv_profile_name);

        if (tvProfileName != null) {
            tvProfileName.setText(R.string.profile_name_default);
        }

        View btnNotifications = findViewById(R.id.btn_profile_notifications);
        if (btnNotifications != null) {
            btnNotifications.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, NotificationsActivity.class);
                startActivity(intent);
            });
        }

        // Update UI according to user mode
        updateProfileModeUI();

        // Mode Toggle Button (Demo Mentee vs Verified Mentor)
        Button btnToggleMode = findViewById(R.id.btn_toggle_user_mode);
        if (btnToggleMode != null) {
            btnToggleMode.setOnClickListener(v -> {
                isVerifiedMentor = !isVerifiedMentor;
                updateProfileModeUI();
                Toast.makeText(this, isVerifiedMentor ? "Đã chuyển sang: Verified Mentor Mode" : "Đã chuyển sang: Mentee Mode", Toast.LENGTH_SHORT).show();
            });
        }

        // Menu click listeners
        View menuRoleAction = findViewById(R.id.menu_role_action);
        if (menuRoleAction != null) {
            menuRoleAction.setOnClickListener(v -> {
                if (isVerifiedMentor) {
                    Toast.makeText(this, "Quản lý Mentee", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(ProfileActivity.this, BecomeMentorActivity.class);
                    startActivity(intent);
                }
            });
        }

        View menuMyApps = findViewById(R.id.menu_my_applications);
        if (menuMyApps != null) {
            menuMyApps.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, MyApplicationsActivity.class);
                startActivity(intent);
            });
        }

        View menuSaved = findViewById(R.id.menu_saved_items);
        if (menuSaved != null) {
            menuSaved.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, SavedScholarshipsActivity.class);
                startActivity(intent);
            });
        }

        View menuAccount = findViewById(R.id.menu_account_settings);
        if (menuAccount != null) {
            menuAccount.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, AccountSettingsActivity.class);
                startActivity(intent);
            });
        }

        View menuHelp = findViewById(R.id.menu_help_center);
        if (menuHelp != null) {
            menuHelp.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, HelpCenterActivity.class);
                startActivity(intent);
            });
        }

        // Setup Bottom Navigation
        setupBottomNavigation();
    }

    private void updateProfileModeUI() {
        if (isVerifiedMentor) {
            if (layoutMentorBadge != null) layoutMentorBadge.setVisibility(View.VISIBLE);
            if (cardMentorRanking != null) cardMentorRanking.setVisibility(View.VISIBLE);
            if (tvMenuRoleLabel != null) tvMenuRoleLabel.setText(R.string.menu_manage_mentee);
        } else {
            if (layoutMentorBadge != null) layoutMentorBadge.setVisibility(View.GONE);
            if (cardMentorRanking != null) cardMentorRanking.setVisibility(View.GONE);
            if (tvMenuRoleLabel != null) tvMenuRoleLabel.setText(R.string.menu_register_mentor);
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
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        if (navRanking != null) {
            navRanking.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, MentorRankingActivity.class);
                startActivity(intent);
                finish();
            });
        }

        if (navCommunity != null) {
            navCommunity.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, CommunityActivity.class);
                startActivity(intent);
                finish();
            });
        }

        if (navMessages != null) {
            navMessages.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, MessagesActivity.class);
                startActivity(intent);
                finish();
            });
        }

        if (navProfile != null) {
            navProfile.setOnClickListener(v -> Toast.makeText(this, "Already on Profile", Toast.LENGTH_SHORT).show());
        }
    }
}
