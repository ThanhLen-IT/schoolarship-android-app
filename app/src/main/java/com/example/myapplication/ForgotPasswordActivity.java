package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

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

        // Reset Password button -> navigate to VerifyCodeActivity
        Button btnResetPassword = findViewById(R.id.btn_reset_password);
        TextInputEditText etResetEmail = findViewById(R.id.et_reset_email);

        if (btnResetPassword != null && etResetEmail != null) {
            btnResetPassword.setOnClickListener(v -> {
                String email = etResetEmail.getText() != null ? etResetEmail.getText().toString().trim() : "";
                if (TextUtils.isEmpty(email)) {
                    etResetEmail.setError(getString(R.string.email_required_message));
                    etResetEmail.requestFocus();
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    etResetEmail.setError(getString(R.string.invalid_email_message));
                    etResetEmail.requestFocus();
                } else {
                    etResetEmail.setError(null);
                    Intent intent = new Intent(ForgotPasswordActivity.this, VerifyCodeActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                }
            });
        }
    }
}
