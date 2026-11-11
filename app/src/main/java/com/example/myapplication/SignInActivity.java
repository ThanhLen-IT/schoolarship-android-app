package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
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

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);

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

        TextView tvSubtitle = findViewById(R.id.tv_sign_in_subtitle);
        if (tvSubtitle != null) {
            tvSubtitle.setText(R.string.sign_in_subtitle);
        }

        findViewById(R.id.btn_google_sign_in).setVisibility(View.GONE);
        findViewById(R.id.btn_facebook_sign_in).setVisibility(View.GONE);
        // Log In button -> API Integration
        Button btnLogin = findViewById(R.id.btn_login);
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> performApiLogin());
        }

        TextView tvForgotPassword = findViewById(R.id.tv_forgot_password);
        if (tvForgotPassword != null) {
            tvForgotPassword.setOnClickListener(
                    v -> {
                        Intent intent =
                                new Intent(SignInActivity.this, ForgotPasswordActivity.class);
                        startActivity(intent);
                    });
        }

        Button btnGoogle = findViewById(R.id.btn_google_sign_in);
        if (btnGoogle != null) {
            btnGoogle.setOnClickListener(
                    v ->
                            Toast.makeText(this, "Continue with Google clicked", Toast.LENGTH_SHORT)
                                    .show());
        }

        Button btnFacebook = findViewById(R.id.btn_facebook_sign_in);
        if (btnFacebook != null) {
            btnFacebook.setOnClickListener(
                    v ->
                            Toast.makeText(
                                            this,
                                            "Continue with Facebook clicked",
                                            Toast.LENGTH_SHORT)
                                    .show());
        }

        TextView tvSignUp = findViewById(R.id.tv_sign_up);
        if (tvSignUp != null) {
            tvSignUp.setOnClickListener(
                    v -> {
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
        String password = etPassword.getText().toString();

        Toast.makeText(this, "Đang kết nối API đăng nhập...", Toast.LENGTH_SHORT).show();

        LoginRequest loginRequest = new LoginRequest(email, password);

        android.view.View submit = findViewById(R.id.btn_login);
        submit.setEnabled(false);
        ApiUi.request(
                this,
                ApiClient.getApiService().login(loginRequest),
                result -> {
                    submit.setEnabled(true);
                    AuthData data = result.getData();
                    if (data == null
                            || data.getToken() == null
                            || data.getToken().isEmpty()
                            || data.getUser() == null) {
                        ApiUi.error(this, "Phản hồi đăng nhập không hợp lệ.");
                        return;
                    }
                    TokenManager manager = new TokenManager(this);
                    manager.saveToken(data.getToken());
                    manager.saveUser(data.getUser().getEmail(), data.getUser().getFullName());
                    ProfileStore.save(this, data.getUser());
                    startActivity(
                            new Intent(this, HomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                },
                message -> {
                    submit.setEnabled(true);
                    ApiUi.error(this, message);
                });
    }
}
