package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

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

        if (new TokenManager(this).isLoggedIn()) restoreSession();
        // Navigate to SignInActivity when Log in is clicked
        Button btnLogin = findViewById(R.id.btn_login);
        if (btnLogin != null) {
            btnLogin.setOnClickListener(
                    v -> {
                        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                        startActivity(intent);
                    });
        }
    }

    private void restoreSession() {
        android.view.View button = findViewById(R.id.btn_login);
        button.setEnabled(false);
        ApiUi.request(
                this,
                ApiClient.getApiService().getCurrentUser(ApiUi.bearer(this)),
                result -> {
                    if (result.getData() == null) {
                        button.setEnabled(true);
                        return;
                    }
                    new TokenManager(this)
                            .saveUser(result.getData().getEmail(), result.getData().getFullName());
                    ProfileStore.save(this, result.getData());
                    startActivity(
                            new Intent(this, HomeActivity.class)
                                    .addFlags(
                                            Intent.FLAG_ACTIVITY_NEW_TASK
                                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                },
                message -> {
                    button.setEnabled(true);
                    new androidx.appcompat.app.AlertDialog.Builder(this)
                            .setMessage(message)
                            .setPositiveButton("Thử lại", (d, w) -> restoreSession())
                            .setNegativeButton("Đóng", null)
                            .show();
                });
    }
}
