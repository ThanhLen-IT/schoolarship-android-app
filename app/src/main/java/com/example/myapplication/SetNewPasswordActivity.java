package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class SetNewPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_set_new_password);

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

        // Back button navigation
        ImageButton btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Update Password button -> validate and navigate to PasswordChangeSuccessActivity
        Button btnUpdatePassword = findViewById(R.id.btn_update_password);
        TextInputEditText etNewPassword = findViewById(R.id.et_new_password);
        TextInputEditText etConfirmPassword = findViewById(R.id.et_confirm_password);

        if (btnUpdatePassword != null && etNewPassword != null && etConfirmPassword != null) {
            btnUpdatePassword.setOnClickListener(v -> {
                String newPass = etNewPassword.getText() != null ? etNewPassword.getText().toString().trim() : "";
                String confirmPass = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

                if (TextUtils.isEmpty(newPass)) {
                    etNewPassword.setError(getString(R.string.password_required));
                    etNewPassword.requestFocus();
                } else if (newPass.length() < 8) {
                    etNewPassword.setError(getString(R.string.password_too_short));
                    etNewPassword.requestFocus();
                } else if (TextUtils.isEmpty(confirmPass)) {
                    etConfirmPassword.setError(getString(R.string.confirm_password_required));
                    etConfirmPassword.requestFocus();
                } else if (!newPass.equals(confirmPass)) {
                    etConfirmPassword.setError(getString(R.string.passwords_do_not_match));
                    etConfirmPassword.requestFocus();
                } else {
                    etNewPassword.setError(null);
                    etConfirmPassword.setError(null);
                    Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show();

                    // Navigate to PasswordChangeSuccessActivity
                    Intent intent = new Intent(SetNewPasswordActivity.this, PasswordChangeSuccessActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}
