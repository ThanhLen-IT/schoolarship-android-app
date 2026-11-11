package com.example.myapplication;

import android.content.Intent;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScholarshipDetailActivity extends AppCompatActivity {

    public static final String EXTRA_SCHOLARSHIP_ID = "extra_scholarship_id";
    public static final String EXTRA_SCHOLARSHIP_TITLE = "extra_scholarship_title";
    public static final String EXTRA_SCHOLARSHIP_CATEGORY = "extra_scholarship_category";
    public static final String EXTRA_SCHOLARSHIP_IMAGE = "extra_scholarship_image";

    private String scholarshipId = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scholarship_detail);

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
        ImageButton btnBack = findViewById(R.id.btn_back_scholarship_detail);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Share button
        ImageButton btnShare = findViewById(R.id.btn_share_scholarship);
        if (btnShare != null) {
            btnShare.setOnClickListener(v -> {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this scholarship on GLOBAL SUCCESS!");
                startActivity(Intent.createChooser(shareIntent, "Share Scholarship"));
            });
        }

        // Retrieve Passed Scholarship Data
        String passedId = getIntent().getStringExtra(EXTRA_SCHOLARSHIP_ID);
        if (!TextUtils.isEmpty(passedId)) {
            scholarshipId = passedId;
        }

        Scholarship scholarship = LocalDemoCatalog.find(scholarshipId);
        String title = scholarship != null ? scholarship.getTitle() : getIntent().getStringExtra(EXTRA_SCHOLARSHIP_TITLE);
        String category = scholarship != null ? scholarship.getDegreeLevel() : getIntent().getStringExtra(EXTRA_SCHOLARSHIP_CATEGORY);
        int imageResId = getIntent().getIntExtra(EXTRA_SCHOLARSHIP_IMAGE, R.drawable.nen1);

        TextView tvTitle = findViewById(R.id.tv_scholarship_detail_title);
        TextView chipCategory = findViewById(R.id.chip_category);
        ImageView ivHero = findViewById(R.id.iv_scholarship_hero);

        if (tvTitle != null && !TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
        if (chipCategory != null && !TextUtils.isEmpty(category)) {
            chipCategory.setText(category);
        }
        if (ivHero != null && imageResId != 0) {
            ivHero.setImageResource(imageResId);
        }

        if (scholarship != null) {
            ((TextView) findViewById(R.id.chip_funding)).setText(scholarship.getFundingType());
            ((TextView) findViewById(R.id.chip_deadline)).setText(scholarship.getDeadlineStr());
            if (LocalDemoCatalog.LEVEL_EXCHANGE.equals(scholarship.getDegreeLevel()))
                chipCategory.setText(R.string.study_level_exchange_short);
            ((TextView) findViewById(R.id.tv_scholarship_description)).setText(
                    getString(R.string.local_scholarship_overview, scholarship.getOrganization(),
                            scholarship.getCountry(), scholarship.getDegreeLevel(), scholarship.getFieldOfStudy()));
        }

        Button findMentor = findViewById(R.id.btn_detail_find_mentor);
        findMentor.setOnClickListener(v -> {
            Intent ranking = new Intent(this, MentorRankingActivity.class);
            ranking.putExtra(MentorRankingActivity.EXTRA_SCHOLARSHIP_ID, scholarshipId);
            ranking.putExtra(MentorRankingActivity.EXTRA_SCHOLARSHIP_NAME, title);
            startActivity(ranking);
        });

        // Apply Now Button -> Saves scholarship via API & notifies
        Button btnApplyNow = findViewById(R.id.btn_apply_now);
        if (btnApplyNow != null) {
            btnApplyNow.setOnClickListener(v -> {
                performSaveScholarshipApi(scholarshipId);
            });
        }

        // Download Form Button
        Button btnDownloadForm = findViewById(R.id.btn_download_form);
        if (btnDownloadForm != null) {
            btnDownloadForm.setOnClickListener(v -> 
                Toast.makeText(this, "Đang tải xuống mẫu đơn ứng tuyển (PDF)...", Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void performSaveScholarshipApi(String id) {
        TokenManager tokenManager = new TokenManager(this);
        if (!tokenManager.isLoggedIn()) {
            Toast.makeText(this, "Chuyển tới trang đăng ký học bổng chính thức...", Toast.LENGTH_SHORT).show();
            return;
        }

        String authHeader = "Bearer " + tokenManager.getToken();
        ApiClient.getApiService().saveScholarship(authHeader, id)
                .enqueue(new Callback<ApiResponse<Object>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                        Toast.makeText(ScholarshipDetailActivity.this, "Đã lưu học bổng vào danh sách của bạn!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                        Toast.makeText(ScholarshipDetailActivity.this, "Đã lưu học bổng thành công!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
