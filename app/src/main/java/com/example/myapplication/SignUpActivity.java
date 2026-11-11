package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

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

        TextView tvSubtitle = findViewById(R.id.tv_sign_up_subtitle);
        if (tvSubtitle != null) {
            tvSubtitle.setText(R.string.sign_up_subtitle);
        }

        // Back button navigation
        ImageButton btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Birth Date Picker Dialog
        TextInputEditText etBirthDate = findViewById(R.id.et_birth_date);
        if (etBirthDate != null) {
            etBirthDate.setOnClickListener(v -> {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        this,
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", selectedDay, (selectedMonth + 1), selectedYear);
                            etBirthDate.setText(formattedDate);
                        },
                        year, month, day
                );
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            });
        }

        // Register button -> API Integration
        Button btnRegister = findViewById(R.id.btn_register);
        if (btnRegister != null) {
            btnRegister.setOnClickListener(v -> performApiRegistration());
        }

        // Login prompt navigation
        TextView tvLogin = findViewById(R.id.tv_login);
        if (tvLogin != null) {
            tvLogin.setOnClickListener(v -> finish());
        }
    }

    private void performApiRegistration() {
        EditText etFullName = findViewById(R.id.et_full_name);
        EditText etEmail = findViewById(R.id.et_email);
        TextInputEditText etBirthDate = findViewById(R.id.et_birth_date);
        EditText etPhone = findViewById(R.id.et_phone);
        EditText etPassword = findViewById(R.id.et_password);

        if (!FormValidation.required(etFullName)
                || !FormValidation.email(etEmail)
                || !FormValidation.required(etBirthDate)
                || !FormValidation.phone(etPhone)
                || !FormValidation.password(etPassword)) {
            return;
        }

        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Generate username from email prefix
        String username = email.contains("@") ? email.split("@")[0] : email;

        String university = "UIT";
        String major = "Công nghệ thông tin";

        Toast.makeText(this, "Đang kết nối API đăng ký...", Toast.LENGTH_SHORT).show();

        RegisterRequest registerRequest = new RegisterRequest(
                email, password, fullName, username, university, major
        );

        ApiClient.getApiService().register(registerRequest).enqueue(new Callback<ApiResponse<AuthData>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthData>> call, Response<ApiResponse<AuthData>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    AuthData data = response.body().getData();

                    if (data != null) {
                        TokenManager tokenManager = new TokenManager(SignUpActivity.this);
                        if (data.getToken() != null) {
                            tokenManager.saveToken(data.getToken());
                        }
                        if (data.getUser() != null) {
                            tokenManager.saveUser(data.getUser().getEmail(), data.getUser().getFullName());
                        }
                    }

                    Toast.makeText(SignUpActivity.this, "Đăng ký tài khoản thành công!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SignUpActivity.this, RegistrationSuccessActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMsg = "Đăng ký không thành công. Kiểm tra lại thông tin!";
                    if (response.body() != null && !TextUtils.isEmpty(response.body().getMessage())) {
                        errorMsg = response.body().getMessage();
                    }
                    Toast.makeText(SignUpActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthData>> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "Lỗi kết nối máy chủ: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
