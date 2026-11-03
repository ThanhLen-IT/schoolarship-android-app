package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

        // Log In button -> navigate to HomeActivity
        Button btnLogin = findViewById(R.id.btn_login);
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> {
                Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
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
}
