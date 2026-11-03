package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AccountSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_settings);

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

        // Back button
        ImageButton btnBack = findViewById(R.id.btn_back_account_settings);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Section 1: Thông tin hồ sơ
        View rowPersonalInfo = findViewById(R.id.row_personal_info);
        if (rowPersonalInfo != null) {
            rowPersonalInfo.setOnClickListener(v -> 
                Toast.makeText(this, "Thông tin cá nhân", Toast.LENGTH_SHORT).show()
            );
        }

        View rowEnableMentor = findViewById(R.id.row_enable_mentor_mode);
        if (rowEnableMentor != null) {
            rowEnableMentor.setOnClickListener(v -> 
                Toast.makeText(this, "Bật chế độ Mentor", Toast.LENGTH_SHORT).show()
            );
        }

        // Section 2: Hiển thị
        View rowDisplayMode = findViewById(R.id.row_dark_light_mode);
        if (rowDisplayMode != null) {
            rowDisplayMode.setOnClickListener(v -> 
                Toast.makeText(this, "Chế độ tối/sáng", Toast.LENGTH_SHORT).show()
            );
        }

        // Section 3: Tài khoản
        View rowLogout = findViewById(R.id.row_logout);
        if (rowLogout != null) {
            rowLogout.setOnClickListener(v -> showLogoutConfirmationDialog());
        }

        View rowChangePassword = findViewById(R.id.row_change_password);
        if (rowChangePassword != null) {
            rowChangePassword.setOnClickListener(v -> {
                Intent intent = new Intent(AccountSettingsActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            });
        }

        View rowDeleteAccount = findViewById(R.id.row_delete_account);
        if (rowDeleteAccount != null) {
            rowDeleteAccount.setOnClickListener(v -> showDeleteAccountConfirmationDialog());
        }
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.logout_dialog_title);
        builder.setMessage(R.string.logout_dialog_msg);
        builder.setNegativeButton(R.string.cancel_button, (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton(R.string.menu_logout, (dialog, which) -> {
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AccountSettingsActivity.this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        builder.show();
    }

    private void showDeleteAccountConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_account_dialog_title);
        builder.setMessage(R.string.delete_account_dialog_msg);
        builder.setNegativeButton(R.string.cancel_button, (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton(R.string.menu_delete_account, (dialog, which) -> {
            Toast.makeText(this, "Đã xóa tài khoản thành công", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AccountSettingsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        builder.show();
    }
}
