package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

public class SubmitVerificationActivity extends AppCompatActivity {

    public static final String EXTRA_SCHOLARSHIP_ITEM = "extra_scholarship_item";
    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB

    private ScholarshipVerifyItem scholarshipItem;
    private Uri selectedPdfUri = null;

    private TextView tvUploadTitle, tvUploadSubtitle;
    private Button btnSubmitDocument;

    private ActivityResultLauncher<Intent> documentPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_submit_verification);

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
        ImageButton btnBack = findViewById(R.id.btn_back_submit_verification);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Retrieve passed scholarship
        scholarshipItem = (ScholarshipVerifyItem) getIntent().getSerializableExtra(EXTRA_SCHOLARSHIP_ITEM);

        TextView tvScholarshipTitle = findViewById(R.id.tv_selected_scholarship_title);
        TextView tvInstitutionName = findViewById(R.id.tv_selected_institution_name);

        if (scholarshipItem != null) {
            if (tvScholarshipTitle != null) tvScholarshipTitle.setText(scholarshipItem.getTitle());
            if (tvInstitutionName != null) tvInstitutionName.setText(scholarshipItem.getInstitution());
        }

        // Upload Area Views
        tvUploadTitle = findViewById(R.id.tv_upload_title);
        tvUploadSubtitle = findViewById(R.id.tv_upload_subtitle);
        btnSubmitDocument = findViewById(R.id.btn_submit_document);

        LinearLayout layoutUploadArea = findViewById(R.id.layout_upload_area);
        if (layoutUploadArea != null) {
            layoutUploadArea.setOnClickListener(v -> openDocumentPicker());
        }

        // Document Picker launcher
        documentPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            handleSelectedDocument(uri);
                        }
                    }
                }
        );

        // Submit Button Click
        if (btnSubmitDocument != null) {
            btnSubmitDocument.setOnClickListener(v -> attemptSubmitDocument());
        }
    }

    private void openDocumentPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        documentPickerLauncher.launch(Intent.createChooser(intent, "Select PDF Document"));
    }

    private void handleSelectedDocument(Uri uri) {
        String fileName = getFileNameFromUri(uri);
        long fileSize = getFileSizeFromUri(uri);

        // Validate File Extension / Type
        if (fileName != null && !fileName.toLowerCase(Locale.ROOT).endsWith(".pdf")) {
            Toast.makeText(this, R.string.error_invalid_file_type, Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate File Size (<= 5MB)
        if (fileSize > MAX_FILE_SIZE_BYTES) {
            Toast.makeText(this, R.string.error_file_too_large, Toast.LENGTH_SHORT).show();
            return;
        }

        selectedPdfUri = uri;

        // Update UI (State B - File Selected)
        if (tvUploadTitle != null) {
            tvUploadTitle.setText(fileName != null ? fileName : "document.pdf");
        }
        if (tvUploadSubtitle != null) {
            double sizeInMb = fileSize > 0 ? (double) fileSize / (1024 * 1024) : 0.0;
            tvUploadSubtitle.setText(String.format(Locale.ROOT, "PDF selected (%.2f MB)", sizeInMb));
        }

        // Enable Submit Button
        if (btnSubmitDocument != null) {
            btnSubmitDocument.setEnabled(true);
            btnSubmitDocument.setAlpha(1.0f);
        }
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

    private long getFileSizeFromUri(Uri uri) {
        long size = 0;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                    if (sizeIndex != -1) {
                        size = cursor.getLong(sizeIndex);
                    }
                }
            } catch (Exception ignored) {}
        }
        return size;
    }

    private void attemptSubmitDocument() {
        if (selectedPdfUri == null) {
            Toast.makeText(this, "Please select a PDF document first", Toast.LENGTH_SHORT).show();
            return;
        }

        if (scholarshipItem != null) {
            scholarshipItem.setVerificationStatus("Pending verification");
        }

        Toast.makeText(this, "Document submitted! Pending verification review.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(SubmitVerificationActivity.this, VerificationStatusActivity.class);
        if (scholarshipItem != null) {
            intent.putExtra(VerificationStatusActivity.EXTRA_NEW_SUBMISSION, scholarshipItem);
        }
        startActivity(intent);
        finish();
    }
}
