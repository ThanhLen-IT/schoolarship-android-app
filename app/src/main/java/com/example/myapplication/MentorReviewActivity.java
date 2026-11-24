package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class MentorReviewActivity extends AppCompatActivity {

    public static final String EXTRA_MENTOR_NAME = "extra_mentor_name";
    public static final String EXTRA_SCHOLARSHIP_NAME = "extra_scholarship_name";
    public static final String EXTRA_MENTOR_SERVICES = "extra_mentor_services";

    private int selectedRating = 5; // Default 5 stars
    private Uri evidenceUri = null;

    private ImageView[] starViews;
    private TextView tvEvidenceFileName, tvCharCounter;
    private EditText etReviewContent;
    private CheckBox cbAnonymous;

    private ActivityResultLauncher<Intent> evidencePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mentor_review);

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

        // Back Button
        ImageButton btnBack = findViewById(R.id.btn_back_mentor_review);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Retrieve Passed Scholarship / Mentor Extras
        String scholarshipName = getIntent().getStringExtra(EXTRA_SCHOLARSHIP_NAME);
        String services = getIntent().getStringExtra(EXTRA_MENTOR_SERVICES);

        TextView tvScholarshipName = findViewById(R.id.tv_scholarship_name);
        TextView tvMentorServices = findViewById(R.id.tv_mentor_services);

        if (tvScholarshipName != null && !TextUtils.isEmpty(scholarshipName)) {
            tvScholarshipName.setText(scholarshipName);
        }
        if (tvMentorServices != null && !TextUtils.isEmpty(services)) {
            tvMentorServices.setText(services);
        }

        // Setup 5-Star Rating System
        setupStarRating();

        // Evidence Picker Setup
        tvEvidenceFileName = findViewById(R.id.tv_evidence_filename);
        View btnUploadEvidence = findViewById(R.id.btn_upload_evidence);

        evidencePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            handleSelectedEvidence(uri);
                        }
                    }
                }
        );

        if (btnUploadEvidence != null) {
            btnUploadEvidence.setOnClickListener(v -> openEvidencePicker());
        }

        // Review Text Field & Character Counter
        etReviewContent = findViewById(R.id.et_review_content);
        tvCharCounter = findViewById(R.id.tv_review_char_counter);
        cbAnonymous = findViewById(R.id.cb_anonymous_review);

        if (etReviewContent != null && tvCharCounter != null) {
            etReviewContent.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    int length = s.length();
                    tvCharCounter.setText(String.format(Locale.getDefault(), "%d ký tự", length));
                    if (length >= 50) {
                        tvCharCounter.setTextColor(Color.parseColor("#15803D"));
                    } else {
                        tvCharCounter.setTextColor(Color.parseColor("#64748B"));
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        // Submit Button
        Button btnSubmit = findViewById(R.id.btn_submit_review);
        if (btnSubmit != null) {
            btnSubmit.setOnClickListener(v -> submitReview());
        }
    }

    private void setupStarRating() {
        starViews = new ImageView[5];
        starViews[0] = findViewById(R.id.iv_star_1);
        starViews[1] = findViewById(R.id.iv_star_2);
        starViews[2] = findViewById(R.id.iv_star_3);
        starViews[3] = findViewById(R.id.iv_star_4);
        starViews[4] = findViewById(R.id.iv_star_5);

        for (int i = 0; i < 5; i++) {
            final int starIndex = i + 1;
            if (starViews[i] != null) {
                starViews[i].setOnClickListener(v -> updateStarRating(starIndex));
            }
        }

        updateStarRating(5);
    }

    private void updateStarRating(int rating) {
        selectedRating = rating;
        for (int i = 0; i < 5; i++) {
            if (starViews[i] != null) {
                if (i < selectedRating) {
                    starViews[i].setColorFilter(Color.parseColor("#FFB51B"));
                } else {
                    starViews[i].setColorFilter(Color.parseColor("#D5D9E2"));
                }
            }
        }
    }

    private void openEvidencePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        String[] mimeTypes = {"image/*", "video/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        evidencePickerLauncher.launch(Intent.createChooser(intent, "Chọn ảnh/video minh chứng"));
    }

    private void handleSelectedEvidence(Uri uri) {
        evidenceUri = uri;
        String fileName = getFileNameFromUri(uri);
        if (tvEvidenceFileName != null) {
            tvEvidenceFileName.setText(fileName != null ? fileName : "Evidence_Uploaded");
        }
        Toast.makeText(this, "Đã đính kèm minh chứng", Toast.LENGTH_SHORT).show();
    }

    private String getFileNameFromUri(Uri uri) {
        String fileName = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex);
                    }
                }
            } catch (Exception ignored) {}
        }
        if (fileName == null) {
            fileName = uri.getPath();
            if (fileName != null) {
                int cut = fileName.lastIndexOf('/');
                if (cut != -1) {
                    fileName = fileName.substring(cut + 1);
                }
            }
        }
        return fileName;
    }

    private void submitReview() {
        String content = etReviewContent != null ? etReviewContent.getText().toString().trim() : "";

        if (content.length() < 50) {
            Toast.makeText(this, R.string.error_min_50_chars, Toast.LENGTH_LONG).show();
            if (etReviewContent != null) {
                etReviewContent.requestFocus();
            }
            return;
        }

        boolean isAnonymous = cbAnonymous != null && cbAnonymous.isChecked();

        Toast.makeText(this, "Gửi đánh giá Mentor " + selectedRating + "⭐ thành công!", Toast.LENGTH_LONG).show();
        finish();
    }
}
