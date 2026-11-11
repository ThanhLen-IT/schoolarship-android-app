package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PersonalProfileActivity extends AppCompatActivity {
    private final String[] keys = {"name", "university", "major", "bio", "skills"};
    private final ExecutorService photoWorker = Executors.newSingleThreadExecutor();
    private EditText[] fields;
    private KeyListener[] fieldKeyListeners;
    private String avatar = "";
    private boolean editing;
    private boolean importingAvatar;
    private int importVersion;
    private final ActivityResultLauncher<String[]> pickAvatar = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri != null && editing) importAvatar(uri);
            });

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_personal_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
        fields = new EditText[]{findViewById(R.id.et_profile_name), findViewById(R.id.et_profile_university),
                findViewById(R.id.et_profile_major), findViewById(R.id.et_profile_bio), findViewById(R.id.et_profile_skills)};
        fieldKeyListeners = new KeyListener[fields.length];
        for (int i = 0; i < fields.length; i++) fieldKeyListeners[i] = fields[i].getKeyListener();
        loadSaved();
        if (state != null) avatar = state.getString("draft_avatar", avatar);
        LocalProfileStore.showAvatar(findViewById(R.id.iv_edit_avatar), avatar);
        setEditing(state != null && state.getBoolean("editing"));
        findViewById(R.id.btn_profile_edit).setOnClickListener(v -> setEditing(true));
        findViewById(R.id.btn_profile_cancel).setOnClickListener(v -> discardOrFinish(false));
        findViewById(R.id.btn_profile_back).setOnClickListener(v -> discardOrFinish(true));
        findViewById(R.id.btn_pick_avatar).setOnClickListener(v -> pickAvatar.launch(new String[]{"image/*"}));
        findViewById(R.id.iv_edit_avatar).setOnClickListener(v -> {
            if (importingAvatar) return;
            if (!editing) setEditing(true);
            pickAvatar.launch(new String[]{"image/*"});
        });
        findViewById(R.id.btn_profile_save).setOnClickListener(v -> saveProfile());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() { discardOrFinish(true); }
        });
    }

    private void importAvatar(android.net.Uri uri) {
        int version = ++importVersion;
        importingAvatar = true;
        updatePhotoControls();
        photoWorker.execute(() -> {
            String imported = null;
            try {
                imported = LocalAvatarStorage.importPhoto(getApplicationContext(), uri);
            } catch (java.io.IOException | RuntimeException ignored) { /* Report failure on the UI thread. */ }
            final String result = imported;
            runOnUiThread(() -> {
                if (isDestroyed() || isFinishing() || version != importVersion || !editing) {
                    if (result != null) LocalAvatarStorage.deleteCopy(getApplicationContext(), result);
                    return;
                }
                importingAvatar = false;
                updatePhotoControls();
                if (result == null) {
                    Toast.makeText(this, R.string.profile_avatar_error, Toast.LENGTH_LONG).show();
                    return;
                }
                deleteUnsavedAvatar();
                avatar = result;
                LocalProfileStore.showAvatar(findViewById(R.id.iv_edit_avatar), avatar);
            });
        });
    }

    private void saveProfile() {
        if (importingAvatar || !editing || !FormValidation.required(fields[0])) return;
        SharedPreferences prefs = LocalProfileStore.prefs(this);
        String oldAvatar = prefs.getString("avatar", "");
        SharedPreferences.Editor writer = prefs.edit();
        for (int i = 0; i < keys.length; i++) writer.putString(keys[i], fields[i].getText().toString().trim());
        // Report success only after both profile fields and the image reference are persisted.
        if (!writer.putString("avatar", avatar).commit()) {
            Toast.makeText(this, R.string.profile_save_error, Toast.LENGTH_LONG).show();
            return;
        }
        if (!oldAvatar.equals(avatar)) LocalAvatarStorage.deleteCopy(this, oldAvatar);
        loadSaved();
        setEditing(false);
        Toast.makeText(this, R.string.profile_saved, Toast.LENGTH_SHORT).show();
    }

    private void loadSaved() {
        SharedPreferences prefs = LocalProfileStore.prefs(this);
        for (int i = 0; i < keys.length; i++) {
            fields[i].setText(prefs.getString(keys[i], i == 0 ? LocalProfileStore.name(this) : ""));
            fields[i].setError(null);
        }
        avatar = prefs.getString("avatar", "");
        LocalProfileStore.showAvatar(findViewById(R.id.iv_edit_avatar), avatar);
        updateSummary();
    }

    private void updateSummary() {
        ((TextView) findViewById(R.id.tv_profile_summary_name)).setText(fields[0].getText().toString().trim());
        String school = fields[1].getText().toString().trim();
        String major = fields[2].getText().toString().trim();
        String summary = school.isEmpty() ? major : major.isEmpty() ? school : school + " · " + major;
        ((TextView) findViewById(R.id.tv_profile_summary_subtitle)).setText(
                summary.isEmpty() ? getString(R.string.profile_summary_empty) : summary);
    }

    private void setEditing(boolean value) {
        editing = value;
        int padding = (int) (12 * getResources().getDisplayMetrics().density);
        for (int i = 0; i < fields.length; i++) {
            fields[i].setKeyListener(value ? fieldKeyListeners[i] : null);
            fields[i].setFocusable(value);
            fields[i].setFocusableInTouchMode(value);
            fields[i].setCursorVisible(value);
            fields[i].setBackgroundResource(value ? R.drawable.bg_input_field : 0);
            fields[i].setPadding(value ? padding : 0, value ? padding : 0, value ? padding : 0, value ? padding : 0);
        }
        fields[3].setMinLines(value ? 3 : 1);
        findViewById(R.id.layout_profile_actions).setVisibility(value ? View.VISIBLE : View.GONE);
        findViewById(R.id.btn_pick_avatar).setVisibility(value ? View.VISIBLE : View.GONE);
        findViewById(R.id.tv_profile_skills_help).setVisibility(value ? View.VISIBLE : View.GONE);
        findViewById(R.id.btn_profile_edit).setVisibility(value ? View.GONE : View.VISIBLE);
        updatePhotoControls();
        if (!value) {
            findViewById(R.id.main).requestFocus();
            androidx.core.view.WindowCompat.getInsetsController(getWindow(), findViewById(R.id.main))
                    .hide(WindowInsetsCompat.Type.ime());
        }
    }

    private void updatePhotoControls() {
        findViewById(R.id.progress_avatar).setVisibility(importingAvatar ? View.VISIBLE : View.GONE);
        findViewById(R.id.btn_pick_avatar).setEnabled(!importingAvatar);
        findViewById(R.id.btn_profile_save).setEnabled(!importingAvatar);
    }

    private void deleteUnsavedAvatar() {
        if (!avatar.equals(LocalProfileStore.prefs(this).getString("avatar", "")))
            LocalAvatarStorage.deleteCopy(this, avatar);
    }

    private void discardDraft() {
        importVersion++;
        importingAvatar = false;
        deleteUnsavedAvatar();
        loadSaved();
        setEditing(false);
    }

    private boolean hasChanges() {
        if (importingAvatar) return true;
        SharedPreferences prefs = LocalProfileStore.prefs(this);
        if (!avatar.equals(prefs.getString("avatar", ""))) return true;
        for (int i = 0; i < keys.length; i++) {
            if (!fields[i].getText().toString().trim().equals(prefs.getString(keys[i],
                    i == 0 ? LocalProfileStore.name(this) : ""))) return true;
        }
        return false;
    }

    private void discardOrFinish(boolean leave) {
        if (editing && hasChanges()) {
            new AlertDialog.Builder(this).setTitle(R.string.profile_discard_title)
                    .setMessage(R.string.profile_discard_message)
                    .setNegativeButton(R.string.profile_keep_editing, null)
                    .setPositiveButton(R.string.profile_discard, (dialog, which) -> {
                        discardDraft();
                        if (leave) finish();
                    }).show();
        } else {
            discardDraft();
            if (leave) finish();
        }
    }

    @Override protected void onSaveInstanceState(Bundle state) {
        state.putString("draft_avatar", avatar);
        state.putBoolean("editing", editing);
        super.onSaveInstanceState(state);
    }

    @Override protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        updateSummary();
    }

    @Override protected void onDestroy() {
        importVersion++;
        photoWorker.shutdown();
        super.onDestroy();
    }
}
