package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class VerifyCodeActivity extends AppCompatActivity {

    private EditText etCode1, etCode2, etCode3, etCode4, etCode5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_code);

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

        // Get email from intent extra and display in subtitle message
        String email = getIntent().getStringExtra("email");
        TextView tvVerifyMessage = findViewById(R.id.tv_verify_message);
        if (tvVerifyMessage != null) {
            if (!TextUtils.isEmpty(email)) {
                String message = getString(R.string.verify_email_message, email);
                tvVerifyMessage.setText(message);
            } else {
                tvVerifyMessage.setText(R.string.verify_email_message_generic);
            }
        }

        // Back button navigation
        ImageButton btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Initialize code boxes
        etCode1 = findViewById(R.id.et_code_1);
        etCode2 = findViewById(R.id.et_code_2);
        etCode3 = findViewById(R.id.et_code_3);
        etCode4 = findViewById(R.id.et_code_4);
        etCode5 = findViewById(R.id.et_code_5);

        setupOtpPinViewNavigation();

        // Verify Code button -> navigate to PasswordResetConfirmationActivity
        Button btnVerifyCode = findViewById(R.id.btn_verify_code);
        if (btnVerifyCode != null) {
            btnVerifyCode.setOnClickListener(v -> {
                String code = getEnteredCode();
                if (code.length() < 5) {
                    Toast.makeText(this, R.string.verification_code_required, Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(VerifyCodeActivity.this, PasswordResetConfirmationActivity.class);
                    startActivity(intent);
                }
            });
        }

        // Resend email link
        TextView tvResendEmail = findViewById(R.id.tv_resend_email);
        if (tvResendEmail != null) {
            tvResendEmail.setOnClickListener(v -> Toast.makeText(this, "Verification code resent", Toast.LENGTH_SHORT).show());
        }
    }

    private void setupOtpPinViewNavigation() {
        if (etCode1 == null || etCode2 == null || etCode3 == null || etCode4 == null || etCode5 == null) {
            return;
        }

        setupBox(etCode1, null, etCode2);
        setupBox(etCode2, etCode1, etCode3);
        setupBox(etCode3, etCode2, etCode4);
        setupBox(etCode4, etCode3, etCode5);
        setupBox(etCode5, etCode4, null);
    }

    private void setupBox(EditText current, EditText previous, EditText next) {
        current.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1 && next != null) {
                    next.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        current.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == android.view.KeyEvent.KEYCODE_DEL && event.getAction() == android.view.KeyEvent.ACTION_DOWN) {
                if (current.getText().toString().isEmpty() && previous != null) {
                    previous.requestFocus();
                    previous.setText("");
                    return true;
                }
            }
            return false;
        });
    }

    private String getEnteredCode() {
        StringBuilder sb = new StringBuilder();
        if (etCode1 != null) sb.append(etCode1.getText().toString().trim());
        if (etCode2 != null) sb.append(etCode2.getText().toString().trim());
        if (etCode3 != null) sb.append(etCode3.getText().toString().trim());
        if (etCode4 != null) sb.append(etCode4.getText().toString().trim());
        if (etCode5 != null) sb.append(etCode5.getText().toString().trim());
        return sb.toString();
    }
}
