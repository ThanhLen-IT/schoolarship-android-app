package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
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

public class HelpCenterActivity extends AppCompatActivity {

    private String selectedFeedbackTopic = "Đề xuất tính năng";

    private TextView chipFeature, chipBug, chipUx;
    private View faqRow1, faqRow2, faqRow3, faqRow4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_help_center);

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
        ImageButton btnBack = findViewById(R.id.btn_back_help_center);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Search Input Filtering
        EditText etSearch = findViewById(R.id.et_help_search);
        faqRow1 = findViewById(R.id.faq_row_1);
        faqRow2 = findViewById(R.id.faq_row_2);
        faqRow3 = findViewById(R.id.faq_row_3);
        faqRow4 = findViewById(R.id.faq_row_4);

        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterFaqList(s.toString().trim());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        // FAQ Click Listeners
        setupFaqClickListeners();

        // Contact Support Cards
        View cardEmail = findViewById(R.id.card_email_support);
        if (cardEmail != null) {
            cardEmail.setOnClickListener(v -> 
                Toast.makeText(this, "Mở Email hỗ trợ", Toast.LENGTH_SHORT).show()
            );
        }

        View cardReport = findViewById(R.id.card_report_issue);
        if (cardReport != null) {
            cardReport.setOnClickListener(v -> 
                Toast.makeText(this, "Mở Báo cáo sự cố", Toast.LENGTH_SHORT).show()
            );
        }

        // Feedback Topic Chips
        chipFeature = findViewById(R.id.chip_feedback_feature);
        chipBug = findViewById(R.id.chip_feedback_bug);
        chipUx = findViewById(R.id.chip_feedback_ux);

        if (chipFeature != null) {
            chipFeature.setOnClickListener(v -> selectFeedbackTopic("Đề xuất tính năng", chipFeature));
        }

        if (chipBug != null) {
            chipBug.setOnClickListener(v -> selectFeedbackTopic("Báo lỗi ứng dụng", chipBug));
        }

        if (chipUx != null) {
            chipUx.setOnClickListener(v -> selectFeedbackTopic("Góp ý trải nghiệm", chipUx));
        }

        // Feedback Submit Button
        Button btnSubmitFeedback = findViewById(R.id.btn_submit_feedback);
        if (btnSubmitFeedback != null) {
            btnSubmitFeedback.setOnClickListener(v -> 
                Toast.makeText(this, "Cảm ơn bạn đã góp ý chủ đề: " + selectedFeedbackTopic, Toast.LENGTH_SHORT).show()
            );
        }

        // Setup Bottom Navigation
        setupBottomNavigation();
    }

    private void filterFaqList(String query) {
        if (query.isEmpty()) {
            if (faqRow1 != null) faqRow1.setVisibility(View.VISIBLE);
            if (faqRow2 != null) faqRow2.setVisibility(View.VISIBLE);
            if (faqRow3 != null) faqRow3.setVisibility(View.VISIBLE);
            if (faqRow4 != null) faqRow4.setVisibility(View.VISIBLE);
        } else {
            String lower = query.toLowerCase();
            if (faqRow1 != null) faqRow1.setVisibility(getString(R.string.faq_1).toLowerCase().contains(lower) ? View.VISIBLE : View.GONE);
            if (faqRow2 != null) faqRow2.setVisibility(getString(R.string.faq_2).toLowerCase().contains(lower) ? View.VISIBLE : View.GONE);
            if (faqRow3 != null) faqRow3.setVisibility(getString(R.string.faq_3).toLowerCase().contains(lower) ? View.VISIBLE : View.GONE);
            if (faqRow4 != null) faqRow4.setVisibility(getString(R.string.faq_4).toLowerCase().contains(lower) ? View.VISIBLE : View.GONE);
        }
    }

    private void setupFaqClickListeners() {
        if (faqRow1 != null) {
            faqRow1.setOnClickListener(v -> Toast.makeText(this, getString(R.string.faq_1), Toast.LENGTH_SHORT).show());
        }
        if (faqRow2 != null) {
            faqRow2.setOnClickListener(v -> Toast.makeText(this, getString(R.string.faq_2), Toast.LENGTH_SHORT).show());
        }
        if (faqRow3 != null) {
            faqRow3.setOnClickListener(v -> Toast.makeText(this, getString(R.string.faq_3), Toast.LENGTH_SHORT).show());
        }
        if (faqRow4 != null) {
            faqRow4.setOnClickListener(v -> Toast.makeText(this, getString(R.string.faq_4), Toast.LENGTH_SHORT).show());
        }

        View tvViewAllFaq = findViewById(R.id.tv_view_all_faq);
        if (tvViewAllFaq != null) {
            tvViewAllFaq.setOnClickListener(v -> Toast.makeText(this, "Xem tất cả câu hỏi thường gặp", Toast.LENGTH_SHORT).show());
        }
    }

    private void selectFeedbackTopic(String topic, TextView selectedChip) {
        selectedFeedbackTopic = topic;

        if (chipFeature != null) chipFeature.setTextColor(ContextCompat.getColor(this, R.color.color_text_primary));
        if (chipBug != null) chipBug.setTextColor(ContextCompat.getColor(this, R.color.color_text_primary));
        if (chipUx != null) chipUx.setTextColor(ContextCompat.getColor(this, R.color.color_text_primary));

        if (selectedChip != null) {
            selectedChip.setTextColor(ContextCompat.getColor(this, R.color.color_primary));
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
                Intent intent = new Intent(HelpCenterActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        if (navRanking != null) {
            navRanking.setOnClickListener(v -> {
                Intent intent = new Intent(HelpCenterActivity.this, MentorRankingActivity.class);
                startActivity(intent);
                finish();
            });
        }

        if (navCommunity != null) {
            navCommunity.setOnClickListener(v -> {
                Intent intent = new Intent(HelpCenterActivity.this, CommunityActivity.class);
                startActivity(intent);
                finish();
            });
        }

        if (navMessages != null) {
            navMessages.setOnClickListener(v -> {
                Intent intent = new Intent(HelpCenterActivity.this, MessagesActivity.class);
                startActivity(intent);
                finish();
            });
        }

        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                Intent intent = new Intent(HelpCenterActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }
}
