package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TextComposerActivity extends AppCompatActivity {

    public static final String EXTRA_DRAFT_TEXT = "extra_draft_text";

    private EditText etComposerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_text_composer);

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

        etComposerText = findViewById(R.id.et_composer_text);
        TextView btnDone = findViewById(R.id.btn_composer_done);

        // Restore initial draft text if passed
        String initialText = getIntent().getStringExtra(EXTRA_DRAFT_TEXT);
        if (initialText != null && etComposerText != null) {
            etComposerText.setText(initialText);
            etComposerText.setSelection(initialText.length());
        }

        // Show soft keyboard automatically
        if (etComposerText != null) {
            etComposerText.requestFocus();
            etComposerText.postDelayed(() -> {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(etComposerText, 0);
                }
            }, 200);
        }

        // Done button click
        if (btnDone != null) {
            btnDone.setOnClickListener(v -> returnResultAndFinish());
        }

        // Back gesture handling
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                returnResultAndFinish();
            }
        });
    }

    private void returnResultAndFinish() {
        String currentText = etComposerText != null ? etComposerText.getText().toString() : "";
        Intent data = new Intent();
        data.putExtra(EXTRA_DRAFT_TEXT, currentText);
        setResult(RESULT_OK, data);

        // Hide keyboard
        if (etComposerText != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(etComposerText.getWindowToken(), 0);
            }
        }

        finish();
    }
}
