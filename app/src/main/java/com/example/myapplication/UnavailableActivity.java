package com.example.myapplication;

public abstract class UnavailableActivity extends androidx.appcompat.app.AppCompatActivity {
    protected abstract String feature();

    @Override
    protected void onCreate(android.os.Bundle state) {
        super.onCreate(state);
        android.widget.LinearLayout panel = new android.widget.LinearLayout(this);
        panel.setOrientation(1);
        panel.setPadding(32, 64, 32, 32);
        android.widget.TextView message = new android.widget.TextView(this);
        message.setTextSize(18);
        message.setText(
                feature()
                        + " chưa có API trên máy chủ. Chức năng sẽ khả dụng sau khi backend được bổ"
                        + " sung.");
        panel.addView(message);
        android.widget.Button back = new android.widget.Button(this);
        back.setText("Quay lại");
        back.setOnClickListener(v -> finish());
        panel.addView(back);
        setContentView(panel);
    }
}
