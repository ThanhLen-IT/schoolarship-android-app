package com.example.myapplication;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScholarshipDiscoveryActivity extends AppCompatActivity {

    private List<Scholarship> allScholarships;
    private List<Scholarship> filteredScholarships;
    private ScholarshipResultAdapter adapter;

    // Filter states
    private String selectedCountry = "All countries";
    private String selectedLevel = "All levels";
    private String selectedField = "All fields";
    private String selectedFunding = "All funding";
    private Date startDate = null;
    private Date endDate = null;
    private String searchQuery = "";
    private String currentSort = "Phù hợp nhất";

    private TextView tvSelectedCountry, tvResultCount, btnFilterLevel, btnFilterField, btnFilterFunding, btnFilterDeadline, tvSort;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scholarship_discovery);

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
        ImageButton btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Initialize views
        tvSelectedCountry = findViewById(R.id.tv_selected_country);
        tvResultCount = findViewById(R.id.tv_result_count);
        btnFilterLevel = findViewById(R.id.btn_filter_level);
        btnFilterField = findViewById(R.id.btn_filter_field);
        btnFilterFunding = findViewById(R.id.btn_filter_funding);
        btnFilterDeadline = findViewById(R.id.btn_filter_deadline);
        tvSort = findViewById(R.id.tv_sort);

        EditText etSearch = findViewById(R.id.et_scholarship_search);
        RecyclerView recyclerView = findViewById(R.id.recycler_scholarship_results);

        // Load sample dataset
        loadSampleScholarships();

        filteredScholarships = new ArrayList<>(allScholarships);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new ScholarshipResultAdapter(filteredScholarships);
            recyclerView.setAdapter(adapter);
        }

        updateResultCount();

        // Search listener
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    searchQuery = s.toString().trim();
                    applyFiltersAndSort();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        // Setup Filter Click Listeners
        View layoutFullFilter = findViewById(R.id.layout_full_filter);
        if (layoutFullFilter != null) {
            layoutFullFilter.setOnClickListener(v -> showCountryDialog());
        }

        if (btnFilterLevel != null) {
            btnFilterLevel.setOnClickListener(v -> showLevelDialog());
        }

        if (btnFilterField != null) {
            btnFilterField.setOnClickListener(v -> showFieldDialog());
        }

        if (btnFilterFunding != null) {
            btnFilterFunding.setOnClickListener(v -> showFundingDialog());
        }

        if (btnFilterDeadline != null) {
            btnFilterDeadline.setOnClickListener(v -> showDeadlineDialog());
        }

        if (tvSort != null) {
            tvSort.setOnClickListener(v -> showSortDialog());
        }
    }

    private void loadSampleScholarships() {
        allScholarships = new ArrayList<>();
        try {
            allScholarships.add(new Scholarship(
                    "1", "Global Excellence Scholarship", "Harvard University", "United States",
                    "Thạc sĩ", "Công nghệ thông tin", "Toàn phần",
                    "17/10/2026", dateFormat.parse("17/10/2026"), dateFormat.parse("01/01/2026"), 100.0
            ));
            allScholarships.add(new Scholarship(
                    "2", "Fulbright Leadership Grant", "Fulbright Foundation", "United States",
                    "Thạc sĩ", "Khoa học xã hội", "Toàn phần",
                    "30/11/2026", dateFormat.parse("30/11/2026"), dateFormat.parse("05/01/2026"), 95.0
            ));
            allScholarships.add(new Scholarship(
                    "3", "Chevening UK Scholarship", "UK Government", "United Kingdom",
                    "Thạc sĩ", "Kinh doanh", "Toàn phần",
                    "05/11/2026", dateFormat.parse("05/11/2026"), dateFormat.parse("10/01/2026"), 98.0
            ));
            allScholarships.add(new Scholarship(
                    "4", "Erasmus Mundus Joint Master", "European Union", "France",
                    "Thạc sĩ", "Khoa học", "Bán phần",
                    "15/12/2026", dateFormat.parse("15/12/2026"), dateFormat.parse("12/01/2026"), 80.0
            ));
            allScholarships.add(new Scholarship(
                    "5", "Australia Awards Scholarship", "Australian Government", "Australia",
                    "Đại học", "Y tế", "Toàn phần",
                    "20/10/2026", dateFormat.parse("20/10/2026"), dateFormat.parse("02/01/2026"), 90.0
            ));
        } catch (ParseException e) {
            // Ignored for sample data
        }
    }

    private void applyFiltersAndSort() {
        filteredScholarships.clear();

        for (Scholarship s : allScholarships) {
            // Search query filter
            if (!TextUtils.isEmpty(searchQuery)) {
                boolean matchesTitle = s.getTitle().toLowerCase(Locale.ROOT).contains(searchQuery.toLowerCase(Locale.ROOT));
                boolean matchesOrg = s.getOrganization().toLowerCase(Locale.ROOT).contains(searchQuery.toLowerCase(Locale.ROOT));
                boolean matchesField = s.getFieldOfStudy().toLowerCase(Locale.ROOT).contains(searchQuery.toLowerCase(Locale.ROOT));
                if (!matchesTitle && !matchesOrg && !matchesField) {
                    continue;
                }
            }

            // Country filter
            if (!"All countries".equals(selectedCountry) && !s.getCountry().equalsIgnoreCase(selectedCountry)) {
                continue;
            }

            // Degree level filter
            if (!"All levels".equals(selectedLevel) && !s.getDegreeLevel().equalsIgnoreCase(selectedLevel)) {
                continue;
            }

            // Field of study filter
            if (!"All fields".equals(selectedField) && !s.getFieldOfStudy().equalsIgnoreCase(selectedField)) {
                continue;
            }

            // Funding filter
            if (!"All funding".equals(selectedFunding) && !s.getFundingType().equalsIgnoreCase(selectedFunding)) {
                continue;
            }

            // Deadline date range filter
            if (startDate != null && s.getDeadlineDate() != null && s.getDeadlineDate().before(startDate)) {
                continue;
            }
            if (endDate != null && s.getDeadlineDate() != null && s.getDeadlineDate().after(endDate)) {
                continue;
            }

            filteredScholarships.add(s);
        }

        // Apply Sorting
        switch (currentSort) {
            case "Hạn nộp gần nhất":
                filteredScholarships.sort((a, b) -> {
                    if (a.getDeadlineDate() == null) return 1;
                    if (b.getDeadlineDate() == null) return -1;
                    return a.getDeadlineDate().compareTo(b.getDeadlineDate());
                });
                break;
            case "Mới cập nhật":
                filteredScholarships.sort((a, b) -> {
                    if (a.getUpdatedAt() == null) return 1;
                    if (b.getUpdatedAt() == null) return -1;
                    return b.getUpdatedAt().compareTo(a.getUpdatedAt());
                });
                break;
            case "Tài trợ cao nhất":
                filteredScholarships.sort((a, b) -> Double.compare(b.getFundingScore(), a.getFundingScore()));
                break;
            case "Tên A–Z":
                filteredScholarships.sort((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
                break;
            case "Phù hợp nhất":
            default:
                // default order
                break;
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        updateResultCount();
    }

    private void updateResultCount() {
        if (tvResultCount != null) {
            tvResultCount.setText("Scholarships (" + filteredScholarships.size() + ")");
        }
    }

    private void showCountryDialog() {
        String[] countries = {"All countries", "United States", "United Kingdom", "France", "Australia"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Country");
        builder.setItems(countries, (dialog, which) -> {
            selectedCountry = countries[which];
            if (tvSelectedCountry != null) {
                tvSelectedCountry.setText(selectedCountry);
            }
            applyFiltersAndSort();
        });
        builder.show();
    }

    private void showLevelDialog() {
        String[] levels = {"All levels", "Đại học", "Thạc sĩ", "Tiến sĩ", "Trao đổi / ngắn hạn"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bậc học");
        builder.setItems(levels, (dialog, which) -> {
            selectedLevel = levels[which];
            if (btnFilterLevel != null) {
                btnFilterLevel.setText(selectedLevel);
            }
            applyFiltersAndSort();
        });
        builder.show();
    }

    private void showFieldDialog() {
        String[] fields = {"All fields", "Công nghệ thông tin", "Kinh doanh", "Khoa học xã hội", "Khoa học", "Y tế"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ngành học");
        builder.setItems(fields, (dialog, which) -> {
            selectedField = fields[which];
            if (btnFilterField != null) {
                btnFilterField.setText(selectedField);
            }
            applyFiltersAndSort();
        });
        builder.show();
    }

    private void showFundingDialog() {
        String[] fundings = {"All funding", "Toàn phần", "Bán phần", "Học phí", "Sinh hoạt phí"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tài trợ");
        builder.setItems(fundings, (dialog, which) -> {
            selectedFunding = fundings[which];
            if (btnFilterFunding != null) {
                btnFilterFunding.setText(selectedFunding);
            }
            applyFiltersAndSort();
        });
        builder.show();
    }

    private void showDeadlineDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedCal = Calendar.getInstance();
                    selectedCal.set(year, month, dayOfMonth, 0, 0, 0);
                    startDate = selectedCal.getTime();
                    Calendar endCal = (Calendar) selectedCal.clone();
                    endCal.add(Calendar.YEAR, 1);
                    endDate = endCal.getTime();

                    if (btnFilterDeadline != null) {
                        btnFilterDeadline.setText(dateFormat.format(startDate));
                    }
                    applyFiltersAndSort();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.setTitle("Chọn hạn nộp từ ngày");
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEUTRAL, "Clear", (dialog, which) -> {
            startDate = null;
            endDate = null;
            if (btnFilterDeadline != null) {
                btnFilterDeadline.setText(R.string.filter_deadline);
            }
            applyFiltersAndSort();
        });
        datePickerDialog.show();
    }

    private void showSortDialog() {
        String[] sorts = {"Phù hợp nhất", "Hạn nộp gần nhất", "Mới cập nhật", "Tài trợ cao nhất", "Tên A–Z"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sắp xếp");
        builder.setItems(sorts, (dialog, which) -> {
            currentSort = sorts[which];
            if (tvSort != null) {
                tvSort.setText(currentSort);
            }
            applyFiltersAndSort();
        });
        builder.show();
    }
}
