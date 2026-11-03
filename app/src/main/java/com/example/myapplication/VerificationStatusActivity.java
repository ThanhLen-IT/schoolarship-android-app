package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VerificationStatusActivity extends AppCompatActivity {

    public static final String EXTRA_NEW_SUBMISSION = "extra_new_submission";

    private List<VerificationStatusRecord> recordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verification_status);

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
        ImageButton btnBack = findViewById(R.id.btn_back_verification_status);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Load reference verification records
        loadVerificationRecords();

        // Check if a new submission was passed
        ScholarshipVerifyItem newSubmission = (ScholarshipVerifyItem) getIntent().getSerializableExtra(EXTRA_NEW_SUBMISSION);
        if (newSubmission != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
            String todayStr = "Submitted on " + dateFormat.format(new Date());
            recordList.add(0, new VerificationStatusRecord(
                    newSubmission.getId(),
                    newSubmission.getTitle(),
                    newSubmission.getInstitution(),
                    "Pending Review",
                    todayStr
            ));
        }

        View layoutEmptyState = findViewById(R.id.layout_empty_state);
        RecyclerView recyclerView = findViewById(R.id.recycler_verification_status);

        if (recordList.isEmpty()) {
            if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.VISIBLE);
            if (recyclerView != null) recyclerView.setVisibility(View.GONE);
        } else {
            if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.GONE);
            if (recyclerView != null) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                VerificationStatusAdapter adapter = new VerificationStatusAdapter(recordList);
                recyclerView.setAdapter(adapter);
            }
        }
    }

    private void loadVerificationRecords() {
        recordList = new ArrayList<>();
        recordList.add(new VerificationStatusRecord(
                "1",
                "UIT Excellence 2025",
                "University of Technology",
                "Approved",
                "Verified on Jan 12, 2025"
        ));
        recordList.add(new VerificationStatusRecord(
                "2",
                "ABC Foundation Grant",
                "International Business Academy",
                "Pending Review",
                "Submitted on Feb 08, 2025"
        ));
        recordList.add(new VerificationStatusRecord(
                "3",
                "Community Tech Fund",
                "City Innovation Institute",
                "Failed / Rejected",
                "Needs updated proof"
        ));
    }
}
