package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);

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

        TextView tvSubtitle = findViewById(R.id.tv_sign_in_subtitle);
        if (tvSubtitle != null) {
            tvSubtitle.setText(R.string.sign_in_subtitle);
        }

        // Log In button -> API Integration
        Button btnLogin = findViewById(R.id.btn_login);
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> performApiLogin());
        }

        TextView tvForgotPassword = findViewById(R.id.tv_forgot_password);
        if (tvForgotPassword != null) {
            tvForgotPassword.setOnClickListener(v -> {
                Intent intent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            });
        }

        Button btnGoogle = findViewById(R.id.btn_google_sign_in);
        if (btnGoogle != null) {
            btnGoogle.setOnClickListener(v -> Toast.makeText(this, "Continue with Google clicked", Toast.LENGTH_SHORT).show());
        }

        Button btnFacebook = findViewById(R.id.btn_facebook_sign_in);
        if (btnFacebook != null) {
            btnFacebook.setOnClickListener(v -> Toast.makeText(this, "Continue with Facebook clicked", Toast.LENGTH_SHORT).show());
        }

        TextView tvSignUp = findViewById(R.id.tv_sign_up);
        if (tvSignUp != null) {
            tvSignUp.setOnClickListener(v -> {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            });
        }
    }

    private void performApiLogin() {
        EditText etEmail = findViewById(R.id.et_email);
        EditText etPassword = findViewById(R.id.et_password);

        if (!FormValidation.email(etEmail) || !FormValidation.password(etPassword)) {
            return;
        }

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        Toast.makeText(this, "Đang kết nối API đăng nhập...", Toast.LENGTH_SHORT).show();

        LoginRequest loginRequest = new LoginRequest(email, password);

        ApiClient.getApiService().login(loginRequest).enqueue(new Callback<ApiResponse<AuthData>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthData>> call, Response<ApiResponse<AuthData>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    AuthData data = response.body().getData();

                    if (data != null) {
                        TokenManager tokenManager = new TokenManager(SignInActivity.this);
                        tokenManager.saveToken(data.getToken());

                        if (data.getUser() != null) {
                            tokenManager.saveUser(data.getUser().getEmail(), data.getUser().getFullName());
                        }
                    }

                    Toast.makeText(SignInActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMsg = "Email hoặc mật khẩu không chính xác";
                    if (response.body() != null && !TextUtils.isEmpty(response.body().getMessage())) {
                        errorMsg = response.body().getMessage();
                    }
                    Toast.makeText(SignInActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthData>> call, Throwable t) {
                Toast.makeText(SignInActivity.this, "Lỗi kết nối máy chủ: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
