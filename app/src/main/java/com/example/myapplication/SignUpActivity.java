package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

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
            etBirthDate.setOnClickListener(
                    v -> {
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog datePickerDialog =
                                new DatePickerDialog(
                                        this,
                                        (view, selectedYear, selectedMonth, selectedDay) -> {
                                            String formattedDate =
                                                    String.format(
                                                            Locale.getDefault(),
                                                            "%02d/%02d/%d",
                                                            selectedDay,
                                                            (selectedMonth + 1),
                                                            selectedYear);
                                            etBirthDate.setText(formattedDate);
                                        },
                                        year,
                                        month,
                                        day);
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
                || !FormValidation.password(etPassword)) {
            return;
        }

        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        // Generate username from email prefix
        String username = email.contains("@") ? email.split("@")[0] : email;

        String university = "";
        String major = "";

        Toast.makeText(this, "Đang kết nối API đăng ký...", Toast.LENGTH_SHORT).show();

        RegisterRequest registerRequest =
                new RegisterRequest(email, password, fullName, null, university, major);

        android.view.View submit = findViewById(R.id.btn_register);
        submit.setEnabled(false);
        ApiUi.request(
                this,
                ApiClient.getApiService().register(registerRequest),
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
                            new Intent(this, RegistrationSuccessActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                },
                message -> {
                    submit.setEnabled(true);
                    ApiUi.error(this, message);
                });
    }
}
