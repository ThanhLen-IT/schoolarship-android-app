package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MenteeRegistrationActivity extends AppCompatActivity {

    private String selectedCountry = "Vietnam";
    private String selectedGender = "Nam";
    private Uri selectedCvUri = null;

    private TextView tvSelectedCountry, tvSelectedGender, tvCvFileName;
    private ActivityResultLauncher<Intent> cvPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mentee_registration);

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
        ImageButton btnBack = findViewById(R.id.btn_back_mentee_registration);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Dropdown Pickers
        tvSelectedCountry = findViewById(R.id.tv_selected_country);
        tvSelectedGender = findViewById(R.id.tv_selected_gender);

        View layoutSelectCountry = findViewById(R.id.layout_select_country);
        if (layoutSelectCountry != null) {
            layoutSelectCountry.setOnClickListener(v -> showCountryPicker());
        }

        View layoutSelectGender = findViewById(R.id.layout_select_gender);
        if (layoutSelectGender != null) {
            layoutSelectGender.setOnClickListener(v -> showGenderPicker());
        }

        // CV Upload
        tvCvFileName = findViewById(R.id.tv_cv_filename);
        View btnUploadCv = findViewById(R.id.btn_upload_cv);

        cvPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            handleSelectedCv(uri);
                        }
                    }
                }
        );

        if (btnUploadCv != null) {
            btnUploadCv.setOnClickListener(v -> openCvPicker());
        }

        // Submit Button
        Button btnSubmit = findViewById(R.id.btn_submit_mentee_registration);
        if (btnSubmit != null) {
            btnSubmit.setOnClickListener(v -> attemptSubmitForm());
        }
    }

    private void showCountryPicker() {
        String[] countries = {"Vietnam", "Singapore", "Japan", "South Korea", "United States", "United Kingdom"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.label_country);
        builder.setItems(countries, (dialog, which) -> {
            selectedCountry = countries[which];
            if (tvSelectedCountry != null) {
                tvSelectedCountry.setText(selectedCountry);
            }
        });
        builder.show();
    }

    private void showGenderPicker() {
        String[] genders = {"Nam", "Nữ", "Khác"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.label_gender);
        builder.setItems(genders, (dialog, which) -> {
            selectedGender = genders[which];
            if (tvSelectedGender != null) {
                tvSelectedGender.setText(selectedGender);
            }
        });
        builder.show();
    }

    private void openCvPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        String[] mimeTypes = {"application/pdf", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/msword"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        cvPickerLauncher.launch(Intent.createChooser(intent, "Select CV File"));
    }

    private void handleSelectedCv(Uri uri) {
        String fileName = getFileNameFromUri(uri);
        selectedCvUri = uri;

        if (tvCvFileName != null) {
            tvCvFileName.setText(fileName != null ? fileName : "CV_Uploaded.pdf");
        }
        Toast.makeText(this, "Đã chọn CV: " + (fileName != null ? fileName : "CV_Uploaded.pdf"), Toast.LENGTH_SHORT).show();
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

    private void attemptSubmitForm() {
        EditText etFullName = findViewById(R.id.et_full_name);
        EditText etLocation = findViewById(R.id.et_location);
        EditText etGmail = findViewById(R.id.et_gmail);
        EditText etBirthYear = findViewById(R.id.et_birth_year);
        EditText etPhone = findViewById(R.id.et_phone_number);
        EditText etUniversity = findViewById(R.id.et_university);
        EditText etScholarship = findViewById(R.id.et_target_scholarship);
        EditText etReason = findViewById(R.id.et_mentor_reason);
        EditText etIntro = findViewById(R.id.et_self_introduction);

        if (!FormValidation.required(etFullName)
                || !FormValidation.required(etLocation)
                || !FormValidation.email(etGmail)
                || !FormValidation.required(etBirthYear)
                || !FormValidation.phone(etPhone)
                || !FormValidation.required(etUniversity)
                || !FormValidation.required(etScholarship)
                || !FormValidation.required(etReason)
                || !FormValidation.required(etIntro)) {
            return;
        }

        if (selectedCvUri == null) {
            Toast.makeText(this, "Vui lòng tải lên file CV trước khi gửi đơn", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Đơn ứng tuyển Mentee đã được gửi thành công!", Toast.LENGTH_LONG).show();
        finish();
    }
}
