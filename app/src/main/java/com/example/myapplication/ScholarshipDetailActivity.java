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

public class ScholarshipDetailActivity extends AppCompatActivity {

    public static final String EXTRA_SCHOLARSHIP_TITLE = "extra_scholarship_title";
    public static final String EXTRA_SCHOLARSHIP_CATEGORY = "extra_scholarship_category";
    public static final String EXTRA_SCHOLARSHIP_IMAGE = "extra_scholarship_image";

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
        String title = getIntent().getStringExtra(EXTRA_SCHOLARSHIP_TITLE);
        String category = getIntent().getStringExtra(EXTRA_SCHOLARSHIP_CATEGORY);
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

        // Apply Now Button
        Button btnApplyNow = findViewById(R.id.btn_apply_now);
        if (btnApplyNow != null) {
            btnApplyNow.setOnClickListener(v -> 
                Toast.makeText(this, "Chuyển tới trang đăng ký học bổng chính thức...", Toast.LENGTH_SHORT).show()
            );
        }

        // Download Form Button
        Button btnDownloadForm = findViewById(R.id.btn_download_form);
        if (btnDownloadForm != null) {
            btnDownloadForm.setOnClickListener(v -> 
                Toast.makeText(this, "Đang tải xuống mẫu đơn ứng tuyển (PDF)...", Toast.LENGTH_SHORT).show()
            );
        }
    }
}
