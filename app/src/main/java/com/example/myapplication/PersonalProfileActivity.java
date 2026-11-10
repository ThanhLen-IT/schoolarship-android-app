package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

public class PersonalProfileActivity extends AppCompatActivity {
    private final String[] keys = {"name", "university", "major", "bio", "skills"};
    private EditText[] fields;
    private String avatar = "";
    private boolean editing;
    private final ActivityResultLauncher<String[]> pickAvatar = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri == null) return;
                try {
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    avatar = uri.toString();
                    LocalProfileStore.showAvatar(findViewById(R.id.iv_edit_avatar), avatar);
                } catch (SecurityException e) {
                    Toast.makeText(this, R.string.profile_avatar_error, Toast.LENGTH_LONG).show();
                }
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
        loadSaved();
        if (state != null) avatar = state.getString("draft_avatar", avatar);
        LocalProfileStore.showAvatar(findViewById(R.id.iv_edit_avatar), avatar);
        setEditing(state != null && state.getBoolean("editing"));
        findViewById(R.id.btn_profile_edit).setOnClickListener(v -> setEditing(true));
        findViewById(R.id.btn_profile_cancel).setOnClickListener(v -> discardOrFinish(false));
        findViewById(R.id.btn_profile_back).setOnClickListener(v -> discardOrFinish(true));
        findViewById(R.id.btn_pick_avatar).setOnClickListener(v -> pickAvatar.launch(new String[]{"image/*"}));
        findViewById(R.id.btn_profile_save).setOnClickListener(v -> {
            if (!FormValidation.required(fields[0])) return;
            SharedPreferences.Editor writer = LocalProfileStore.prefs(this).edit();
            for (int i = 0; i < keys.length; i++) writer.putString(keys[i], fields[i].getText().toString().trim());
            writer.putString("avatar", avatar).apply();
            loadSaved();
            setEditing(false);
            Toast.makeText(this, R.string.profile_saved, Toast.LENGTH_SHORT).show();
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() { discardOrFinish(true); }
        });
    }

    private void loadSaved() {
        SharedPreferences prefs = LocalProfileStore.prefs(this);
        for (int i = 0; i < keys.length; i++) {
            fields[i].setText(prefs.getString(keys[i], i == 0 ? LocalProfileStore.name(this) : ""));
            fields[i].setError(null);
        }
        avatar = prefs.getString("avatar", "");
        LocalProfileStore.showAvatar(findViewById(R.id.iv_edit_avatar), avatar);
    }

    private void setEditing(boolean value) {
        editing = value;
        for (EditText field : fields) field.setEnabled(value);
        findViewById(R.id.btn_pick_avatar).setVisibility(value ? View.VISIBLE : View.GONE);
        findViewById(R.id.btn_profile_save).setVisibility(value ? View.VISIBLE : View.GONE);
        findViewById(R.id.btn_profile_cancel).setVisibility(value ? View.VISIBLE : View.GONE);
        findViewById(R.id.btn_profile_edit).setVisibility(value ? View.GONE : View.VISIBLE);
    }

    private boolean hasChanges() {
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
                        loadSaved();
                        setEditing(false);
                        if (leave) finish();
                    }).show();
        } else {
            loadSaved();
            setEditing(false);
            if (leave) finish();
        }
    }

    @Override protected void onSaveInstanceState(Bundle state) {
        state.putString("draft_avatar", avatar);
        state.putBoolean("editing", editing);
        super.onSaveInstanceState(state);
    }
}
