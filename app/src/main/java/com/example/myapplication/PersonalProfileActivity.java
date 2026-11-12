package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class PersonalProfileActivity extends AppCompatActivity {
    private final androidx.activity.result.ActivityResultLauncher<String> choosePhoto =
            registerForActivityResult(
                    new androidx.activity.result.contract.ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) uploadPhoto(uri);
                    });
    private EditText[] fields;
    private android.text.method.KeyListener[] listeners;
    private String avatar = "";
    private boolean editing, loading, saving;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_personal_profile);
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (view, insets) -> {
                    androidx.core.graphics.Insets bars =
                            insets.getInsets(
                                    androidx.core.view.WindowInsetsCompat.Type.systemBars()
                                            | androidx.core.view.WindowInsetsCompat.Type.ime());
                    view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                    return insets;
                });
        fields =
                new EditText[] {
                    findViewById(R.id.et_profile_name),
                    findViewById(R.id.et_profile_university),
                    findViewById(R.id.et_profile_major),
                    findViewById(R.id.et_profile_bio),
                    findViewById(R.id.et_profile_skills)
                };
        listeners = new android.text.method.KeyListener[fields.length];
        for (int i = 0; i < fields.length; i++) listeners[i] = fields[i].getKeyListener();
        findViewById(R.id.btn_profile_edit).setOnClickListener(v -> setEditing(true));
        findViewById(R.id.btn_profile_cancel).setOnClickListener(v -> discard(false));
        findViewById(R.id.btn_profile_back).setOnClickListener(v -> discard(true));
        findViewById(R.id.btn_profile_save).setOnClickListener(v -> save());
        findViewById(R.id.btn_pick_avatar).setOnClickListener(v -> editAvatar());
        findViewById(R.id.iv_edit_avatar)
                .setOnClickListener(
                        v -> {
                            if (editing && !saving) editAvatar();
                        });
        getOnBackPressedDispatcher()
                .addCallback(
                        this,
                        new androidx.activity.OnBackPressedCallback(true) {
                            @Override
                            public void handleOnBackPressed() {
                                discard(true);
                            }
                        });
        showCached();
        setEditing(false);
        if (ApiUi.requireLogin(this)) {
            if (state != null && state.getBoolean("draft_editing")) {
                String[] draft = state.getStringArray("draft_fields");
                if (draft != null && draft.length == fields.length)
                    for (int i = 0; i < fields.length; i++) fields[i].setText(draft[i]);
                avatar = state.getString("draft_avatar", avatar);
                RemoteImage.show(findViewById(R.id.iv_edit_avatar), avatar);
                setEditing(true);
            } else load();
        }
    }

    private void showCached() {
        String[] keys = {"name", "university", "major", "bio", "skills"};
        for (int i = 0; i < keys.length; i++)
            fields[i].setText(ProfileStore.prefs(this).getString(keys[i], ""));
        avatar = ProfileStore.prefs(this).getString("avatar", "");
        RemoteImage.show(findViewById(R.id.iv_edit_avatar), avatar);
        ((TextView) findViewById(R.id.tv_profile_summary_name)).setText(fields[0].getText());
        ((TextView) findViewById(R.id.tv_profile_summary_subtitle))
                .setText(fields[1].getText() + " · " + fields[2].getText());
    }

    private void load() {
        loading = true;
        setEditing(false);
        ApiUi.request(
                this,
                ApiClient.getApiService().getCurrentUser(ApiUi.bearer(this)),
                result -> {
                    loading = false;
                    if (result.getData() == null) {
                        ApiUi.error(this, "Không có dữ liệu hồ sơ.");
                        return;
                    }
                    ProfileStore.save(this, result.getData());
                    showCached();
                    setEditing(false);
                },
                message -> {
                    loading = false;
                    setEditing(false);
                    ApiUi.error(this, message);
                });
    }

    private void editAvatar() {
        new AlertDialog.Builder(this)
                .setTitle("Cập nhật ảnh đại diện")
                .setMessage("Ảnh được cập nhật ngay sau khi tải lên thành công.")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Chọn ảnh", (d, w) -> choosePhoto.launch("image/*"))
                .show();
    }

    private void uploadPhoto(android.net.Uri uri) {
        saving = true;
        setEditing(true);
        MobileFiles.avatar(
                this,
                uri,
                url -> {
                    saving = false;
                    avatar = url;
                    ProfileStore.prefs(this).edit().putString("avatar", url).apply();
                    RemoteImage.show(findViewById(R.id.iv_edit_avatar), url);
                    setEditing(true);
                    ApiUi.error(this, "Đã cập nhật ảnh đại diện.");
                },
                message -> {
                    saving = false;
                    setEditing(true);
                    ApiUi.error(this, message);
                });
    }

    private void setEditing(boolean value) {
        editing = value;
        for (int i = 0; i < fields.length; i++) {
            fields[i].setKeyListener(value && !saving ? listeners[i] : null);
            fields[i].setFocusableInTouchMode(value && !saving);
        }
        findViewById(R.id.layout_profile_actions).setVisibility(value ? View.VISIBLE : View.GONE);
        findViewById(R.id.btn_profile_edit).setVisibility(value ? View.GONE : View.VISIBLE);
        findViewById(R.id.btn_profile_edit).setEnabled(!loading);
        findViewById(R.id.btn_pick_avatar).setVisibility(value ? View.VISIBLE : View.GONE);
        ((TextView) findViewById(R.id.btn_pick_avatar)).setText("Đổi ảnh đại diện");
        findViewById(R.id.btn_profile_save).setEnabled(!saving);
        findViewById(R.id.btn_profile_cancel).setEnabled(!saving);
        findViewById(R.id.btn_pick_avatar).setEnabled(!saving);
    }

    private void save() {
        if (saving || !editing || !FormValidation.required(fields[0])) return;
        Map<String, Object> body = new HashMap<>();
        String[] keys = {"full_name", "university", "major", "bio"};
        for (int i = 0; i < keys.length; i++)
            body.put(keys[i], fields[i].getText().toString().trim());
        java.util.List<String> skills = new java.util.ArrayList<>();
        for (String skill : fields[4].getText().toString().split(","))
            if (!skill.trim().isEmpty()) skills.add(skill.trim());
        body.put("skills", skills);
        body.put("avatar_url", avatar);
        saving = true;
        setEditing(true);
        ApiUi.request(
                this,
                ApiClient.getApiService().updateProfile(ApiUi.bearer(this), body),
                result -> {
                    saving = false;
                    if (result.getData() == null) {
                        setEditing(true);
                        ApiUi.error(this, "Phản hồi hồ sơ không hợp lệ.");
                        return;
                    }
                    ProfileStore.save(this, result.getData());
                    showCached();
                    setEditing(false);
                    ApiUi.error(this, "Đã cập nhật hồ sơ trên máy chủ.");
                },
                message -> {
                    saving = false;
                    setEditing(true);
                    ApiUi.error(this, message);
                });
    }

    private void discard(boolean finish) {
        if (saving) return;
        if (editing)
            new AlertDialog.Builder(this)
                    .setMessage("Bỏ các thay đổi chưa lưu?")
                    .setNegativeButton("Tiếp tục sửa", null)
                    .setPositiveButton(
                            "Bỏ thay đổi",
                            (d, w) -> {
                                showCached();
                                setEditing(false);
                                if (finish) finish();
                            })
                    .show();
        else if (finish) finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        state.putBoolean("draft_editing", editing && !saving);
        String[] values = new String[fields.length];
        for (int i = 0; i < fields.length; i++) values[i] = fields[i].getText().toString();
        state.putStringArray("draft_fields", values);
        state.putString("draft_avatar", avatar);
        super.onSaveInstanceState(state);
    }
}
