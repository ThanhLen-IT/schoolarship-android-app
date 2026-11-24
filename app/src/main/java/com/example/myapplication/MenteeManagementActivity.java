package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MenteeManagementActivity extends AppCompatActivity {

    private List<MenteeApplication> allApplications;
    private List<MenteeApplication> filteredApplications;
    private MenteeApplicationAdapter adapter;

    private String currentSearchQuery = "";
    private String currentFilterStatus = "ALL"; // "ALL", "PENDING", "APPROVED", "REJECTED"

    private TextView tvCountPending, tvCountApproved;
    private TextView chipFilterAll, chipFilterPending, chipFilterApproved, chipFilterRejected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mentee_management);

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

        // Back Button
        ImageButton btnBack = findViewById(R.id.btn_back_mentee_mgmt);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Notifications Button
        ImageButton btnNotifications = findViewById(R.id.btn_notifications);
        if (btnNotifications != null) {
            btnNotifications.setOnClickListener(v -> {
                Intent intent = new Intent(MenteeManagementActivity.this, NotificationsActivity.class);
                startActivity(intent);
            });
        }

        tvCountPending = findViewById(R.id.tv_count_pending);
        tvCountApproved = findViewById(R.id.tv_count_approved);

        chipFilterAll = findViewById(R.id.chip_filter_all);
        chipFilterPending = findViewById(R.id.chip_filter_pending);
        chipFilterApproved = findViewById(R.id.chip_filter_approved);
        chipFilterRejected = findViewById(R.id.chip_filter_rejected);

        if (chipFilterAll != null) chipFilterAll.setOnClickListener(v -> selectFilter("ALL"));
        if (chipFilterPending != null) chipFilterPending.setOnClickListener(v -> selectFilter("PENDING"));
        if (chipFilterApproved != null) chipFilterApproved.setOnClickListener(v -> selectFilter("APPROVED"));
        if (chipFilterRejected != null) chipFilterRejected.setOnClickListener(v -> selectFilter("REJECTED"));

        // Search Input Listener
        EditText etSearch = findViewById(R.id.et_mentee_search);
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    currentSearchQuery = s.toString().trim();
                    applySearchAndFilter();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        // Load Initial Sample Applications
        loadSampleApplications();

        filteredApplications = new ArrayList<>(allApplications);
        RecyclerView recyclerView = findViewById(R.id.recycler_mentee_applications);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new MenteeApplicationAdapter(filteredApplications, new MenteeApplicationAdapter.OnApplicationActionListener() {
                @Override
                public void onRejectClick(MenteeApplication application, int position) {
                    RejectMenteeBottomSheet bottomSheet = RejectMenteeBottomSheet.newInstance(application, (app, reason) -> {
                        app.setStatus(MenteeApplication.Status.REJECTED);
                        updateCountsAndFilter();
                    });
                    bottomSheet.show(getSupportFragmentManager(), "reject_sheet");
                }

                @Override
                public void onApproveClick(MenteeApplication application, int position) {
                    ApproveMenteeBottomSheet bottomSheet = ApproveMenteeBottomSheet.newInstance(application, app -> {
                        app.setStatus(MenteeApplication.Status.APPROVED);
                        updateCountsAndFilter();

                        // Navigate to Chat Detail with Mentee
                        Intent intent = new Intent(MenteeManagementActivity.this, ChatDetailActivity.class);
                        intent.putExtra(ChatDetailActivity.EXTRA_CHAT_NAME, app.getFullName());
                        startActivity(intent);
                    });
                    bottomSheet.show(getSupportFragmentManager(), "approve_sheet");
                }
            });
            recyclerView.setAdapter(adapter);
        }

        updateCountsAndFilter();
    }

    private void loadSampleApplications() {
        allApplications = new ArrayList<>();
        allApplications.add(new MenteeApplication(
                "m1",
                "Nguyễn Văn A",
                "NVA",
                "Sinh viên ĐH Công nghệ Thông tin - ĐHQG-HCM",
                "Học bổng MEXT Japan 2026",
                "Em muốn hướng dẫn sửa đề cương nghiên cứu và luyện phỏng vấn áp lực...",
                MenteeApplication.Status.PENDING
        ));
        allApplications.add(new MenteeApplication(
                "m2",
                "Trần Minh Hiếu",
                "TMH",
                "Sinh viên ĐH Bách Khoa Hà Nội",
                "Học bổng Chevening 2025",
                "Muốn học hỏi kinh nghiệm thực tế và kỹ năng nghiên cứu từ Mentor...",
                MenteeApplication.Status.APPROVED
        ));
        allApplications.add(new MenteeApplication(
                "m3",
                "Lê Thị Mai",
                "LTM",
                "Sinh viên ĐH Kinh tế - ĐHQG-HCM",
                "Học bổng DAAD 2025",
                "Cần hướng dẫn lập roadmap du học và chuẩn bị hồ sơ...",
                MenteeApplication.Status.REJECTED
        ));
        allApplications.add(new MenteeApplication(
                "m4",
                "Phạm Thị Hoa",
                "PTH",
                "Sinh viên ĐH Ngoại thương",
                "Học bổng Fulbright 2026",
                "Mong muốn nhận phản hồi chi tiết bài luận cá nhân...",
                MenteeApplication.Status.PENDING
        ));
    }

    private void selectFilter(String status) {
        currentFilterStatus = status;

        resetChipStyles();
        if ("ALL".equalsIgnoreCase(status) && chipFilterAll != null) {
            setChipSelected(chipFilterAll);
        } else if ("PENDING".equalsIgnoreCase(status) && chipFilterPending != null) {
            setChipSelected(chipFilterPending);
        } else if ("APPROVED".equalsIgnoreCase(status) && chipFilterApproved != null) {
            setChipSelected(chipFilterApproved);
        } else if ("REJECTED".equalsIgnoreCase(status) && chipFilterRejected != null) {
            setChipSelected(chipFilterRejected);
        }

        applySearchAndFilter();
    }

    private void resetChipStyles() {
        setChipUnselected(chipFilterAll);
        setChipUnselected(chipFilterPending);
        setChipUnselected(chipFilterApproved);
        setChipUnselected(chipFilterRejected);
    }

    private void setChipSelected(TextView chip) {
        if (chip == null) return;
        chip.setBackgroundResource(R.drawable.bg_welcome_button_primary);
        chip.setTextColor(ContextCompat.getColor(this, android.R.color.white));
    }

    private void setChipUnselected(TextView chip) {
        if (chip == null) return;
        chip.setBackgroundResource(R.drawable.bg_welcome_button_secondary);
        chip.setTextColor(ContextCompat.getColor(this, R.color.color_text_primary));
    }

    private void updateCountsAndFilter() {
        int pending = 0;
        int approved = 0;
        int rejected = 0;

        for (MenteeApplication app : allApplications) {
            if (app.getStatus() == MenteeApplication.Status.PENDING) pending++;
            else if (app.getStatus() == MenteeApplication.Status.APPROVED) approved++;
            else if (app.getStatus() == MenteeApplication.Status.REJECTED) rejected++;
        }

        if (tvCountPending != null) tvCountPending.setText(String.format("%02d", pending));
        if (tvCountApproved != null) tvCountApproved.setText(String.format("%02d", approved));

        if (chipFilterAll != null) chipFilterAll.setText("Tất cả (" + allApplications.size() + ")");
        if (chipFilterPending != null) chipFilterPending.setText("Chờ duyệt (" + pending + ")");
        if (chipFilterApproved != null) chipFilterApproved.setText("Đã nhận (" + approved + ")");
        if (chipFilterRejected != null) chipFilterRejected.setText("Từ chối (" + rejected + ")");

        applySearchAndFilter();
    }

    private void applySearchAndFilter() {
        filteredApplications.clear();

        for (MenteeApplication app : allApplications) {
            if (!"ALL".equalsIgnoreCase(currentFilterStatus)) {
                if ("PENDING".equalsIgnoreCase(currentFilterStatus) && app.getStatus() != MenteeApplication.Status.PENDING) continue;
                if ("APPROVED".equalsIgnoreCase(currentFilterStatus) && app.getStatus() != MenteeApplication.Status.APPROVED) continue;
                if ("REJECTED".equalsIgnoreCase(currentFilterStatus) && app.getStatus() != MenteeApplication.Status.REJECTED) continue;
            }

            if (!TextUtils.isEmpty(currentSearchQuery)) {
                boolean matchesName = app.getFullName().toLowerCase().contains(currentSearchQuery.toLowerCase());
                boolean matchesUni = app.getUniversity().toLowerCase().contains(currentSearchQuery.toLowerCase());
                if (!matchesName && !matchesUni) continue;
            }

            filteredApplications.add(app);
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
