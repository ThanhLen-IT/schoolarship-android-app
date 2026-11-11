package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CreatePostActivity extends AppCompatActivity {

    private EditText etPostContent, titleInput;
    private String imageUrl = "";
    private ImageView ivMediaPreview;
    private Uri selectedImageUri = null;

    private ActivityResultLauncher<Intent> photoPickerLauncher;
    private ActivityResultLauncher<Intent> textComposerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_post);

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

        // Back gesture callback
        getOnBackPressedDispatcher()
                .addCallback(
                        this,
                        new OnBackPressedCallback(true) {
                            @Override
                            public void handleOnBackPressed() {
                                handleBackNavigation();
                            }
                        });

        // Back button
        ImageButton btnBack = findViewById(R.id.btn_back_create_post);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> handleBackNavigation());
        }

        ApiUi.requireLogin(this);
        // Inputs & Preview
        etPostContent = findViewById(R.id.et_post_content);
        ivMediaPreview = findViewById(R.id.iv_selected_media_preview);
        titleInput = findViewById(R.id.et_post_title);
        if (savedInstanceState != null) {
            imageUrl = savedInstanceState.getString("draft_image_url", "");
            RemoteImage.show(ivMediaPreview, imageUrl);
            ivMediaPreview.setVisibility(imageUrl.isEmpty() ? View.GONE : View.VISIBLE);
        }
        findViewById(R.id.chip_location).setVisibility(View.GONE);
        findViewById(R.id.chip_mood).setVisibility(View.GONE);

        if (etPostContent != null) {
            etPostContent.setOnClickListener(v -> openTextComposer());
            etPostContent.setOnFocusChangeListener(
                    (v, hasFocus) -> {
                        if (hasFocus) {
                            openTextComposer();
                        }
                    });
        }

        // Text Composer Launcher
        textComposerLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if (result.getResultCode() == Activity.RESULT_OK
                                    && result.getData() != null) {
                                String updatedText =
                                        result.getData()
                                                .getStringExtra(
                                                        TextComposerActivity.EXTRA_DRAFT_TEXT);
                                if (updatedText != null && etPostContent != null) {
                                    etPostContent.setText(updatedText);
                                    etPostContent.setSelection(updatedText.length());
                                }
                            }
                        });

        // Photo Picker launcher
        photoPickerLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if (result.getResultCode() == Activity.RESULT_OK
                                    && result.getData() != null) {
                                selectedImageUri = result.getData().getData();
                                if (selectedImageUri != null && ivMediaPreview != null) {
                                    ivMediaPreview.setImageURI(selectedImageUri);
                                    ivMediaPreview.setVisibility(View.VISIBLE);
                                }
                            }
                        });

        // Image Selection Button
        ImageButton btnAddImage = findViewById(R.id.btn_add_post_image);
        if (btnAddImage != null) {
            btnAddImage.setOnClickListener(v -> openPhotoPicker());
        }

        // Utility Chips
        View chipAudience = findViewById(R.id.chip_audience);
        if (chipAudience != null) {
            chipAudience.setOnClickListener(v -> showAudienceDialog());
        }

        View chipLocation = findViewById(R.id.chip_location);
        if (chipLocation != null) {
            chipLocation.setOnClickListener(
                    v -> Toast.makeText(this, "Vị trí selected", Toast.LENGTH_SHORT).show());
        }

        View chipMood = findViewById(R.id.chip_mood);
        if (chipMood != null) {
            chipMood.setOnClickListener(
                    v ->
                            Toast.makeText(this, "Cảm xúc/hoạt động selected", Toast.LENGTH_SHORT)
                                    .show());
        }

        // Publish Button
        Button btnPublish = findViewById(R.id.btn_publish_post);
        if (btnPublish != null) {
            btnPublish.setOnClickListener(v -> attemptPublish());
        }
    }

    private void openTextComposer() {
        Intent intent = new Intent(this, TextComposerActivity.class);
        String currentText = etPostContent != null ? etPostContent.getText().toString() : "";
        intent.putExtra(TextComposerActivity.EXTRA_DRAFT_TEXT, currentText);
        textComposerLauncher.launch(intent);
    }

    private void openPhotoPicker() {
        EditText url = new EditText(this);
        url.setHint("https://…");
        url.setText(imageUrl);
        new AlertDialog.Builder(this)
                .setTitle("URL ảnh công khai")
                .setMessage("Backend chưa hỗ trợ tải ảnh từ điện thoại.")
                .setView(url)
                .setNegativeButton("Hủy", null)
                .setPositiveButton(
                        "Dùng ảnh",
                        (d, w) -> {
                            String value = url.getText().toString().trim();
                            Uri parsed = Uri.parse(value);
                            if (!value.isEmpty()
                                    && (!("https".equalsIgnoreCase(parsed.getScheme())
                                                    || "http".equalsIgnoreCase(parsed.getScheme()))
                                            || parsed.getHost() == null)) {
                                ApiUi.error(this, "URL ảnh không hợp lệ.");
                                return;
                            }
                            imageUrl = value;
                            RemoteImage.show(ivMediaPreview, value);
                            ivMediaPreview.setVisibility(
                                    value.isEmpty() ? View.GONE : View.VISIBLE);
                        })
                .show();
    }

    private void showAudienceDialog() {
        ApiUi.error(this, "Bài viết được đăng công khai trên cộng đồng.");
    }

    private void attemptPublish() {
        if (!ApiUi.requireLogin(this)) return;
        String content = etPostContent.getText().toString().trim(),
                title = titleInput.getText().toString().trim();
        if (title.isEmpty() || title.length() > 255) {
            titleInput.setError("Tiêu đề cần từ 1–255 ký tự.");
            return;
        }
        if (content.isEmpty() || content.length() > 20000) {
            etPostContent.setError("Nội dung cần từ 1–20000 ký tự.");
            return;
        }
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("title", title);
        body.put("content", content);
        if (!imageUrl.isEmpty()) body.put("image_url", imageUrl);
        findViewById(R.id.btn_publish_post).setEnabled(false);
        ApiUi.request(
                this,
                ApiClient.getApiService().createPost(ApiUi.bearer(this), body),
                result -> {
                    ApiUi.error(this, "Đã đăng bài viết trên máy chủ.");
                    finish();
                },
                message -> {
                    findViewById(R.id.btn_publish_post).setEnabled(true);
                    ApiUi.error(this, message);
                });
    }

    private void handleBackNavigation() {
        String content = etPostContent != null ? etPostContent.getText().toString().trim() : "";

        if (!TextUtils.isEmpty(content) || selectedImageUri != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bỏ bài viết?");
            builder.setMessage("Nội dung bài viết chưa được lưu. Bạn có chắc muốn thoát?");
            builder.setPositiveButton("Bỏ", (dialog, which) -> finish());
            builder.setNegativeButton("Tiếp tục sửa", (dialog, which) -> dialog.dismiss());
            builder.show();
        } else {
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        state.putString("draft_image_url", imageUrl);
        super.onSaveInstanceState(state);
    }
}
