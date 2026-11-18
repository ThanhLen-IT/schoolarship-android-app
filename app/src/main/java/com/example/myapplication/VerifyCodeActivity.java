package com.example.myapplication;

public class VerifyCodeActivity extends androidx.appcompat.app.AppCompatActivity {
    @Override
    protected void onCreate(android.os.Bundle state) {
        super.onCreate(state);
        startActivity(
                new android.content.Intent(this, SetNewPasswordActivity.class)
                        .putExtra("email", getIntent().getStringExtra("email")));
        finish();
    }
}
