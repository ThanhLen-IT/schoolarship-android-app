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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScholarshipDiscoveryActivity extends AppCompatActivity {

    public static final String EXTRA_FOCUS_SEARCH = "extra_focus_search";
    private boolean focusSearchOnWindow;

    private List<Scholarship> allScholarships;
    private List<Scholarship> filteredScholarships;
    private ScholarshipResultAdapter adapter;

    // Filter states
    private String selectedCountry = "Tất cả quốc gia";
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
        focusSearchOnWindow = savedInstanceState == null
                && getIntent().getBooleanExtra(EXTRA_FOCUS_SEARCH, false);

        View rootView = findViewById(R.id.main);
        if (rootView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
                Insets systemBars = insets.getInsets(
                        WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime()
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
        allScholarships = LocalDemoCatalog.scholarships();

        filteredScholarships = new ArrayList<>(allScholarships);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new ScholarshipResultAdapter(filteredScholarships);
            recyclerView.setAdapter(adapter);
        }

        restoreFilters(savedInstanceState, etSearch);
        applyFiltersAndSort();
        fetchScholarshipsFromApi();

        findViewById(R.id.btn_clear_filters).setOnClickListener(v -> {
            selectedCountry = "Tất cả quốc gia";
            selectedLevel = "All levels";
            selectedField = "All fields";
            selectedFunding = "All funding";
            startDate = null;
            endDate = null;
            currentSort = "Phù hợp nhất";
            searchQuery = "";
            etSearch.setText("");
            tvSelectedCountry.setText(selectedCountry);
            updateLevelLabel();
            btnFilterField.setText(R.string.filter_field);
            btnFilterFunding.setText(R.string.filter_funding);
            btnFilterDeadline.setText(R.string.filter_deadline);
            tvSort.setText("Phù hợp nhất".equals(currentSort) ? getString(R.string.sort_label) : currentSort);
            applyFiltersAndSort();
            fetchScholarshipsFromApi();
        });

        // Search listener
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    searchQuery = s.toString().trim();
                    applyFiltersAndSort();
                    fetchScholarshipsFromApi();
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

    private void fetchScholarshipsFromApi() {
        String levelParam = "All levels".equalsIgnoreCase(selectedLevel) ? null : selectedLevel;
        String countryParam = "Tất cả quốc gia".equalsIgnoreCase(selectedCountry) ? null : selectedCountry;
        String searchParam = TextUtils.isEmpty(searchQuery) ? null : searchQuery;

        ApiClient.getApiService().getScholarships(searchParam, countryParam, levelParam, 1, 20, "deadline_asc")
                .enqueue(new Callback<ApiResponse<List<Scholarship>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Scholarship>>> call, Response<ApiResponse<List<Scholarship>>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            List<Scholarship> apiData = response.body().getData();
                            if (apiData != null && !apiData.isEmpty()) {
                                filteredScholarships.clear();
                                filteredScholarships.addAll(apiData);
                                if (adapter != null) {
                                    adapter.notifyDataSetChanged();
                                }
                                updateResultCount();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Scholarship>>> call, Throwable t) {
                        // Fallback to local catalog
                    }
                });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && focusSearchOnWindow) {
            focusSearchOnWindow = false;
            EditText search = findViewById(R.id.et_scholarship_search);
            search.post(() -> {
                search.requestFocus();
                androidx.core.view.WindowCompat.getInsetsController(getWindow(), search)
                        .show(WindowInsetsCompat.Type.ime());
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        state.putString("country", selectedCountry);
        state.putString("level", selectedLevel);
        state.putString("field", selectedField);
        state.putString("funding", selectedFunding);
        state.putString("query", searchQuery);
        state.putString("sort", currentSort);
        if (startDate != null) state.putLong("from", startDate.getTime());
        if (endDate != null) state.putLong("to", endDate.getTime());
        super.onSaveInstanceState(state);
    }

    private void restoreFilters(Bundle state, EditText search) {
        if (state == null) return;
        selectedCountry = state.getString("country", selectedCountry);
        selectedLevel = state.getString("level", selectedLevel);
        selectedField = state.getString("field", selectedField);
        selectedFunding = state.getString("funding", selectedFunding);
        searchQuery = state.getString("query", "");
        currentSort = state.getString("sort", currentSort);
        if (state.containsKey("from")) startDate = new Date(state.getLong("from"));
        if (state.containsKey("to")) endDate = new Date(state.getLong("to"));
        search.setText(searchQuery);
        tvSelectedCountry.setText(selectedCountry);
        updateLevelLabel();
        if (!"All fields".equals(selectedField)) btnFilterField.setText(selectedField);
        if (!"All funding".equals(selectedFunding)) btnFilterFunding.setText(selectedFunding);
        tvSort.setText("Phù hợp nhất".equals(currentSort) ? getString(R.string.sort_label) : currentSort);
        if (startDate != null && endDate != null)
            btnFilterDeadline.setText(dateFormat.format(startDate) + " – " + dateFormat.format(endDate));
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
            if (!"Tất cả quốc gia".equalsIgnoreCase(selectedCountry) && !"All countries".equalsIgnoreCase(selectedCountry) && !s.getCountry().equalsIgnoreCase(selectedCountry)) {
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

            // Unknown deadlines cannot satisfy a requested date interval.
            if ((startDate != null || endDate != null) && s.getDeadlineDate() == null) continue;
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
                    if (a.getDeadlineDate() == null) return b.getDeadlineDate() == null ? 0 : 1;
                    if (b.getDeadlineDate() == null) return -1;
                    return a.getDeadlineDate().compareTo(b.getDeadlineDate());
                });
                break;
            case "Mới cập nhật":
                filteredScholarships.sort((a, b) -> {
                    if (a.getUpdatedAt() == null) return b.getUpdatedAt() == null ? 0 : 1;
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
        tvResultCount.setText(getString(R.string.local_result_count, filteredScholarships.size()));
        boolean active = !searchQuery.isEmpty() || !"Tất cả quốc gia".equals(selectedCountry)
                || !"All levels".equals(selectedLevel) || !"All fields".equals(selectedField)
                || !"All funding".equals(selectedFunding) || startDate != null || endDate != null
                || !"Phù hợp nhất".equals(currentSort);
        findViewById(R.id.btn_clear_filters).setVisibility(active ? View.VISIBLE : View.GONE);
        findViewById(R.id.tv_search_empty).setVisibility(filteredScholarships.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showCountryDialog() {
        String[] countries = {"Tất cả quốc gia", "Đông Nam Á", "Trung Quốc", "Hàn Quốc", "Nhật Bản", "Đài Loan"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn quốc gia");
        builder.setItems(countries, (dialog, which) -> {
            selectedCountry = countries[which];
            if (tvSelectedCountry != null) {
                tvSelectedCountry.setText(selectedCountry);
            }
            applyFiltersAndSort();
            fetchScholarshipsFromApi();
        });
        builder.show();
    }

    private void updateLevelLabel() {
        if ("All levels".equals(selectedLevel)) btnFilterLevel.setText(R.string.filter_level);
        else if (LocalDemoCatalog.LEVEL_EXCHANGE.equals(selectedLevel))
            btnFilterLevel.setText(R.string.study_level_exchange_short);
        else btnFilterLevel.setText(selectedLevel);
        btnFilterLevel.setContentDescription("All levels".equals(selectedLevel)
                ? getString(R.string.filter_level) : selectedLevel);
    }

    private void showLevelDialog() {
        String[] levels = {"All levels", "Đại học", "Thạc sĩ", "Tiến sĩ"};
        String[] labels = {getString(R.string.all_study_levels), "Đại học", "Thạc sĩ", "Tiến sĩ"};
        int checked = java.util.Arrays.asList(levels).indexOf(selectedLevel);
        new AlertDialog.Builder(this)
                .setTitle("Bậc học")
                .setSingleChoiceItems(labels, checked, (dialog, which) -> {
                    selectedLevel = levels[which];
                    updateLevelLabel();
                    applyFiltersAndSort();
                    fetchScholarshipsFromApi();
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.cancel_button, null)
                .show();
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
            fetchScholarshipsFromApi();
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
            fetchScholarshipsFromApi();
        });
        builder.show();
    }

    private void showDeadlineDialog() {
        Calendar initial = Calendar.getInstance();
        if (startDate != null) initial.setTime(startDate);
        DatePickerDialog from = new DatePickerDialog(this, (view, year, month, day) -> {
            Calendar first = Calendar.getInstance();
            first.clear();
            first.set(year, month, day);
            Calendar last = (Calendar) first.clone();
            if (endDate != null && !endDate.before(first.getTime())) last.setTime(endDate);
            DatePickerDialog to = new DatePickerDialog(this, (endView, endYear, endMonth, endDay) -> {
                Calendar chosenEnd = Calendar.getInstance();
                chosenEnd.clear();
                chosenEnd.set(endYear, endMonth, endDay);
                startDate = first.getTime();
                endDate = chosenEnd.getTime();
                btnFilterDeadline.setText(dateFormat.format(startDate) + " – " + dateFormat.format(endDate));
                applyFiltersAndSort();
                fetchScholarshipsFromApi();
            }, last.get(Calendar.YEAR), last.get(Calendar.MONTH), last.get(Calendar.DAY_OF_MONTH));
            to.setTitle("Hạn nộp đến ngày (bao gồm ngày chọn)");
            to.getDatePicker().setMinDate(first.getTimeInMillis());
            to.show();
        }, initial.get(Calendar.YEAR), initial.get(Calendar.MONTH), initial.get(Calendar.DAY_OF_MONTH));
        from.setTitle("Hạn nộp từ ngày");
        from.setButton(DatePickerDialog.BUTTON_NEUTRAL, "Xóa hạn nộp", (dialog, which) -> {
            startDate = null;
            endDate = null;
            btnFilterDeadline.setText(R.string.filter_deadline);
            applyFiltersAndSort();
            fetchScholarshipsFromApi();
        });
        from.show();
    }

    private void showSortDialog() {
        String[] sorts = {"Phù hợp nhất", "Hạn nộp gần nhất", "Mới cập nhật", "Tài trợ cao nhất", "Tên A–Z"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sắp xếp");
        builder.setItems(sorts, (dialog, which) -> {
            currentSort = sorts[which];
            if (tvSort != null) {
                tvSort.setText("Phù hợp nhất".equals(currentSort) ? getString(R.string.sort_label) : currentSort);
            }
            applyFiltersAndSort();
            fetchScholarshipsFromApi();
        });
        builder.show();
    }
}
