package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PersonalProfileActivity extends AppCompatActivity {

    private boolean isFollowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_personal_profile);

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
        ImageButton btnBack = findViewById(R.id.btn_profile_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Settings button -> AccountSettingsActivity
        ImageButton btnSettings = findViewById(R.id.btn_profile_settings);
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> {
                Intent intent = new Intent(PersonalProfileActivity.this, AccountSettingsActivity.class);
                startActivity(intent);
            });
        }

        // Follow Button
        Button btnFollow = findViewById(R.id.btn_follow_user);
        if (btnFollow != null) {
            btnFollow.setOnClickListener(v -> {
                isFollowing = !isFollowing;
                btnFollow.setText(isFollowing ? "Đang Theo dõi" : "+ Follow");
                Toast.makeText(this, isFollowing ? "Đã theo dõi Loan Nguyễn Kim" : "Bỏ theo dõi", Toast.LENGTH_SHORT).show();
            });
        }

        // Message Button -> MessagesActivity
        Button btnMessage = findViewById(R.id.btn_message_user);
        if (btnMessage != null) {
            btnMessage.setOnClickListener(v -> {
                Intent intent = new Intent(PersonalProfileActivity.this, MessagesActivity.class);
                startActivity(intent);
            });
        }

        // Register Mentee Button -> BecomeMentorActivity
        Button btnRegisterMentee = findViewById(R.id.btn_register_mentee);
        if (btnRegisterMentee != null) {
            btnRegisterMentee.setOnClickListener(v -> {
                Intent intent = new Intent(PersonalProfileActivity.this, BecomeMentorActivity.class);
                startActivity(intent);
            });
        }

        // Load saved profile data if available
        loadSavedProfileData();
    }

    private void loadSavedProfileData() {
        SharedPreferences prefs = LocalProfileStore.prefs(this);
        String name = prefs.getString("name", "Loan Nguyễn Kim");
        String school = prefs.getString("university", "University of Information Technology");
        String major = prefs.getString("major", "Computer Networks and Communications");
        String bio = prefs.getString("bio", "I am a highly adaptable and challenge-driven individual.");
        String avatar = prefs.getString("avatar", "");

        TextView tvName = findViewById(R.id.tv_profile_name);
        TextView tvBio = findViewById(R.id.tv_profile_bio);
        TextView tvSchool = findViewById(R.id.tv_education_school);
        TextView tvMajor = findViewById(R.id.tv_education_major);
        ImageView ivAvatar = findViewById(R.id.iv_profile_avatar);

        if (tvName != null && !TextUtils.isEmpty(name)) {
            tvName.setText(name);
        }
        if (tvBio != null && !TextUtils.isEmpty(bio)) {
            tvBio.setText(bio);
        }
        if (tvSchool != null && !TextUtils.isEmpty(school)) {
            tvSchool.setText(school);
        }
        if (tvMajor != null && !TextUtils.isEmpty(major)) {
            tvMajor.setText(major);
        }
        if (ivAvatar != null && !TextUtils.isEmpty(avatar)) {
            LocalProfileStore.showAvatar(ivAvatar, avatar);
        }
    }
}
