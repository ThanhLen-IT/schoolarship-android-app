package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

/** Accepts the server's emailed reset token, never a simulated numeric OTP. */
public class SetNewPasswordActivity extends AppCompatActivity {
    private EditText tokenInput;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_set_new_password);
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
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE);
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        tokenInput = findViewById(R.id.et_reset_token);
        ((TextView) findViewById(R.id.tv_set_password_subtitle))
                .setText(
                        "Dán token hoặc liên kết đặt lại mật khẩu trong email. Liên kết có hiệu lực"
                                + " 15 phút.");
        if (getIntent().getData() != null)
            tokenInput.setText(getIntent().getData().getQueryParameter("token"));
        Button submit = findViewById(R.id.btn_update_password);
        EditText password = findViewById(R.id.et_new_password),
                confirm = findViewById(R.id.et_confirm_password);
        submit.setOnClickListener(
                v -> {
                    String token = tokenInput.getText().toString().trim();
                    if (token.contains("://")) {
                        try {
                            token = android.net.Uri.parse(token).getQueryParameter("token");
                        } catch (RuntimeException ignored) {
                            token = null;
                        }
                    }
                    if (token == null || !token.matches("[a-f0-9]{64}")) {
                        tokenInput.setError("Token phải gồm 64 ký tự lấy từ email.");
                        return;
                    }
                    String value = password.getText().toString();
                    int bytes = value.getBytes(java.nio.charset.StandardCharsets.UTF_8).length;
                    if (bytes < 8 || bytes > 72) {
                        password.setError("Mật khẩu cần từ 8–72 byte UTF-8.");
                        return;
                    }
                    if (!value.equals(confirm.getText().toString())) {
                        confirm.setError("Mật khẩu không khớp.");
                        return;
                    }
                    Map<String, String> body = new HashMap<>();
                    body.put("token", token);
                    body.put("new_password", value);
                    submit.setEnabled(false);
                    ApiUi.request(
                            this,
                            ApiClient.getApiService().resetPassword(body),
                            result -> {
                                SessionStore.clear(this);
                                tokenInput.setText("");
                                password.setText("");
                                confirm.setText("");
                                startActivity(
                                        new Intent(this, PasswordChangeSuccessActivity.class)
                                                .addFlags(
                                                        Intent.FLAG_ACTIVITY_NEW_TASK
                                                                | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finish();
                            },
                            message -> {
                                submit.setEnabled(true);
                                ApiUi.error(this, message);
                            });
                });
    }
}
