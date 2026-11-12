package com.example.myapplication;

import android.app.DatePickerDialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
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
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScholarshipDiscoveryActivity extends AppCompatActivity {

    public static final String EXTRA_FOCUS_SEARCH = "extra_focus_search";
    private boolean focusSearchOnWindow;

    private Call<ApiResponse<List<Scholarship>>> activeCall;
    private final android.os.Handler searchHandler =
            new android.os.Handler(android.os.Looper.getMainLooper());
    private final Runnable searchTask = () -> fetchScholarshipsFromApi();
    private int currentPage = 0, totalPages = 0, serverTotal = 0;
    private boolean loading;
    private MobileData.Filters filterOptions;
    private List<Scholarship> allScholarships;
    private List<Scholarship> filteredScholarships;
    private ScholarshipResultAdapter adapter;
    private View headerView, footerView;

    // Filter states
    private String selectedCountry = "Tất cả quốc gia";
    private String selectedLevel = "All levels";
    private String selectedField = "All fields";
    private String selectedFunding = "All funding";
    private Date startDate = null;
    private Date endDate = null;
    private String searchQuery = "";
    private String currentSort = "Mới nhất";

    private TextView tvSelectedCountry,
            tvResultCount,
            btnFilterLevel,
            btnFilterField,
            btnFilterFunding,
            btnFilterDeadline,
            tvSort;

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scholarship_discovery);
        RecyclerView recyclerView = findViewById(R.id.recycler_scholarship_results);
        // Inflation asks the parent to generate LayoutParams, which requires
        // the RecyclerView's LayoutManager to be installed already.
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        headerView = getLayoutInflater().inflate(
                R.layout.view_scholarship_discovery_header, recyclerView, false);
        footerView = getLayoutInflater().inflate(
                R.layout.view_scholarship_discovery_footer, recyclerView, false);
        focusSearchOnWindow =
                savedInstanceState == null
                        && getIntent().getBooleanExtra(EXTRA_FOCUS_SEARCH, false);

        View rootView = screenView(R.id.main);
        if (rootView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(
                    rootView,
                    (v, insets) -> {
                        Insets systemBars =
                                insets.getInsets(
                                        WindowInsetsCompat.Type.systemBars()
                                                | WindowInsetsCompat.Type.ime());

                        v.setPadding(
                                systemBars.left,
                                systemBars.top,
                                systemBars.right,
                                systemBars.bottom);

                        return insets;
                    });
        }

        // Back button
        ImageButton btnBack = screenView(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Initialize views
        tvSelectedCountry = screenView(R.id.tv_selected_country);
        tvResultCount = screenView(R.id.tv_result_count);
        btnFilterLevel = screenView(R.id.btn_filter_level);
        btnFilterField = screenView(R.id.btn_filter_field);
        btnFilterFunding = screenView(R.id.btn_filter_funding);
        btnFilterDeadline = screenView(R.id.btn_filter_deadline);
        tvSort = screenView(R.id.tv_sort);

        EditText etSearch = screenView(R.id.et_scholarship_search);

        // Results are loaded from the API.
        allScholarships = new ArrayList<>();

        filteredScholarships = new ArrayList<>(allScholarships);
        if (recyclerView != null) {
            adapter = new ScholarshipResultAdapter(filteredScholarships);
            // Controls and cards must have the same scroll owner. An outer
            // ScrollView can clip the RecyclerView while consuming its gestures.
            recyclerView.setItemAnimator(null);
            recyclerView.setAdapter(new ConcatAdapter(
                    new StaticSectionAdapter(headerView),
                    adapter,
                    new StaticSectionAdapter(footerView)));
            final int inset = Math.round(16 * getResources().getDisplayMetrics().density);
            recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view,
                        RecyclerView parent, RecyclerView.State state) {
                    if (parent.getChildViewHolder(view).getBindingAdapter() == adapter) {
                        outRect.left = inset;
                        outRect.right = inset;
                    }
                }
            });
        }

        recyclerView.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView view, int dx, int dy) {
                        if (dy > 0
                                && !view.canScrollVertically(1)
                                && !loading
                                && currentPage < totalPages) fetchPage(currentPage + 1);
                    }
                });
        screenView(R.id.tv_search_empty).setOnClickListener(v -> fetchScholarshipsFromApi());
        tvResultCount.setOnClickListener(
                v -> {
                    if (!loading && currentPage < totalPages) fetchPage(currentPage + 1);
                });
        loadFilterOptions();
        screenView(R.id.btn_more_scholarships)
                .setOnClickListener(
                        v -> {
                            if (!loading && currentPage < totalPages) fetchPage(currentPage + 1);
                        });
        restoreFilters(savedInstanceState, etSearch);
        applyFiltersAndSort();
        fetchScholarshipsFromApi();

        screenView(R.id.btn_clear_filters)
                .setOnClickListener(
                        v -> {
                            selectedCountry = "Tất cả quốc gia";
                            selectedLevel = "All levels";
                            selectedField = "All fields";
                            selectedFunding = "All funding";
                            startDate = null;
                            endDate = null;
                            currentSort = "Mới nhất";
                            searchQuery = "";
                            etSearch.setText("");
                            tvSelectedCountry.setText(selectedCountry);
                            updateLevelLabel();
                            btnFilterField.setText(R.string.filter_field);
                            btnFilterFunding.setText(R.string.filter_funding);
                            btnFilterDeadline.setText(R.string.filter_deadline);
                            tvSort.setText(
                                    "Mới nhất".equals(currentSort)
                                            ? getString(R.string.sort_label)
                                            : currentSort);
                            applyFiltersAndSort();
                            fetchScholarshipsFromApi();
                        });

        // Search listener
        if (etSearch != null) {
            etSearch.addTextChangedListener(
                    new TextWatcher() {
                        @Override
                        public void beforeTextChanged(
                                CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(
                                CharSequence s, int start, int before, int count) {
                            searchQuery = s.toString().trim();
                            searchHandler.removeCallbacks(searchTask);
                            searchHandler.postDelayed(searchTask, 350);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {}
                    });
        }

        // Setup Filter Click Listeners
        View layoutFullFilter = screenView(R.id.layout_full_filter);
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

    // The header/footer can be offscreen and detached from the RecyclerView.
    // Keep their original instances so filters and loading/retry controls stay bound.
    private <T extends View> T screenView(int id) {
        T view = findViewById(id);
        if (view == null && headerView != null) view = headerView.findViewById(id);
        if (view == null && footerView != null) view = footerView.findViewById(id);
        return view;
    }

    private void fetchScholarshipsFromApi() {
        searchHandler.removeCallbacks(searchTask);
        if (activeCall != null) activeCall.cancel();
        currentPage = totalPages = serverTotal = 0;
        allScholarships.clear();
        filteredScholarships.clear();
        if (adapter != null) adapter.notifyDataSetChanged();
        fetchPage(1);
    }

    private void fetchPage(int page) {
        loading = true;
        screenView(R.id.btn_more_scholarships).setVisibility(View.GONE);
        tvResultCount.setText("Đang tải học bổng…");
        screenView(R.id.tv_search_empty).setVisibility(View.GONE);
        String level = "All levels".equals(selectedLevel) ? null : selectedLevel;
        String country = "Tất cả quốc gia".equals(selectedCountry) ? null : selectedCountry;
        String sort = "created_at_desc";
        if ("Hạn nộp gần nhất".equals(currentSort)) sort = "deadline_asc";
        if ("Tên A–Z".equals(currentSort)) sort = "title_asc";
        SimpleDateFormat apiDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
        activeCall =
                ApiUi.request(
                        this,
                        ApiClient.getApiService()
                                .getScholarships(
                                        searchQuery.isEmpty() ? null : searchQuery,
                                        country,
                                        level,
                                        page,
                                        20,
                                        sort,
                                        "All funding".equals(selectedFunding)
                                                ? null
                                                : selectedFunding,
                                        "All fields".equals(selectedField) ? null : selectedField,
                                        startDate == null ? null : apiDate.format(startDate),
                                        endDate == null ? null : apiDate.format(endDate)),
                        result -> {
                            loading = false;
                            currentPage = page;
                            totalPages =
                                    result.getTotalPages() == null ? page : result.getTotalPages();
                            serverTotal = result.getTotal() == null ? 0 : result.getTotal();
                            if (result.getData() != null) allScholarships.addAll(result.getData());
                            applyFiltersAndSort();
                        },
                        message -> {
                            loading = false;
                            tvResultCount.setText("Không thể tải. Chạm để thử lại.");
                            tvResultCount.setOnClickListener(
                                    v -> {
                                        if (!loading) fetchPage(page);
                                    });
                            TextView empty = screenView(R.id.tv_search_empty);
                            empty.setText(message + "\nChạm để thử lại.");
                            empty.setVisibility(
                                    allScholarships.isEmpty() ? View.VISIBLE : View.GONE);
                            ApiUi.error(this, message);
                        });
    }

    @Override
    protected void onDestroy() {
        searchHandler.removeCallbacksAndMessages(null);
        if (activeCall != null) activeCall.cancel();
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && focusSearchOnWindow) {
            focusSearchOnWindow = false;
            EditText search = screenView(R.id.et_scholarship_search);
            search.post(
                    () -> {
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
        tvSort.setText(
                "Mới nhất".equals(currentSort) ? getString(R.string.sort_label) : currentSort);
        if (startDate != null && endDate != null)
            btnFilterDeadline.setText(
                    dateFormat.format(startDate) + " – " + dateFormat.format(endDate));
    }

    private void applyFiltersAndSort() {
        filteredScholarships.clear();
        filteredScholarships.addAll(allScholarships);
        if (adapter != null) adapter.notifyDataSetChanged();
        updateResultCount();
    }

    private void updateResultCount() {
        screenView(R.id.btn_more_scholarships)
                .setVisibility(!loading && currentPage < totalPages ? View.VISIBLE : View.GONE);
        tvResultCount.setText(
                serverTotal
                        + " học bổng · Đã tải "
                        + filteredScholarships.size()
                        + (currentPage < totalPages ? " · Chạm để tải thêm" : ""));
        tvResultCount.setOnClickListener(
                v -> {
                    if (!loading && currentPage < totalPages) fetchPage(currentPage + 1);
                });
        boolean active =
                !searchQuery.isEmpty()
                        || !"Tất cả quốc gia".equals(selectedCountry)
                        || !"All levels".equals(selectedLevel)
                        || !"All fields".equals(selectedField)
                        || !"All funding".equals(selectedFunding)
                        || startDate != null
                        || endDate != null
                        || !"Mới nhất".equals(currentSort);
        screenView(R.id.btn_clear_filters).setVisibility(active ? View.VISIBLE : View.GONE);
        TextView empty = screenView(R.id.tv_search_empty);
        empty.setText("Không tìm thấy học bổng phù hợp. Chạm để tải lại.");
        empty.setVisibility(!loading && filteredScholarships.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void loadFilterOptions() {
        ApiUi.request(
                this,
                ApiClient.getApiService().filters(),
                r -> {
                    filterOptions = r.getData();
                },
                message -> ApiUi.error(this, message));
    }

    private void selectOption(
            String title,
            java.util.List<String> options,
            String all,
            java.util.function.Consumer<String> select) {
        if (filterOptions == null) {
            new AlertDialog.Builder(this)
                    .setMessage("Chưa tải được bộ lọc.")
                    .setPositiveButton("Tải lại", (d, w) -> loadFilterOptions())
                    .setNegativeButton("Đóng", null)
                    .show();
            return;
        }
        java.util.List<String> values = new java.util.ArrayList<>();
        values.add(all);
        if (options != null)
            for (String option : options)
                if (!title.equals("Bậc học") || !"all".equals(option)) values.add(option);
        String[] labels = new String[values.size()];
        for (int i = 0; i < labels.length; i++)
            labels[i] =
                    i == 0
                            ? "Tất cả"
                            : title.equals("Bậc học")
                                    ? Scholarship.levelLabel(values.get(i))
                                    : values.get(i);
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setItems(
                        labels,
                        (d, w) -> {
                            select.accept(values.get(w));
                            fetchScholarshipsFromApi();
                        })
                .show();
    }

    private void showCountryDialog() {
        selectOption(
                "Quốc gia",
                filterOptions == null ? null : filterOptions.countries,
                "Tất cả quốc gia",
                value -> {
                    selectedCountry = value;
                    tvSelectedCountry.setText(value);
                });
    }

    private void updateLevelLabel() {
        if ("All levels".equals(selectedLevel)) btnFilterLevel.setText(R.string.filter_level);
        else btnFilterLevel.setText(Scholarship.levelLabel(selectedLevel));
        btnFilterLevel.setContentDescription(
                "All levels".equals(selectedLevel)
                        ? getString(R.string.filter_level)
                        : selectedLevel);
    }

    private void showLevelDialog() {
        selectOption(
                "Bậc học",
                filterOptions == null ? null : filterOptions.study_levels,
                "All levels",
                value -> {
                    selectedLevel = value;
                    updateLevelLabel();
                });
    }

    private void showFieldDialog() {
        selectOption(
                "Ngành học",
                filterOptions == null ? null : filterOptions.majors,
                "All fields",
                value -> {
                    selectedField = value;
                    btnFilterField.setText(
                            "All fields".equals(value) ? getString(R.string.filter_field) : value);
                });
    }

    private void showFundingDialog() {
        selectOption(
                "Tài trợ",
                filterOptions == null ? null : filterOptions.types,
                "All funding",
                value -> {
                    selectedFunding = value;
                    btnFilterFunding.setText(
                            "All funding".equals(value)
                                    ? getString(R.string.filter_funding)
                                    : value);
                });
    }

    private void showDeadlineDialog() {
        Calendar initial = Calendar.getInstance();
        if (startDate != null) initial.setTime(startDate);
        DatePickerDialog from =
                new DatePickerDialog(
                        this,
                        (view, year, month, day) -> {
                            Calendar first = Calendar.getInstance();
                            first.clear();
                            first.set(year, month, day);
                            Calendar last = (Calendar) first.clone();
                            if (endDate != null && !endDate.before(first.getTime()))
                                last.setTime(endDate);
                            DatePickerDialog to =
                                    new DatePickerDialog(
                                            this,
                                            (endView, endYear, endMonth, endDay) -> {
                                                Calendar chosenEnd = Calendar.getInstance();
                                                chosenEnd.clear();
                                                chosenEnd.set(endYear, endMonth, endDay);
                                                startDate = first.getTime();
                                                endDate = chosenEnd.getTime();
                                                btnFilterDeadline.setText(
                                                        dateFormat.format(startDate)
                                                                + " – "
                                                                + dateFormat.format(endDate));
                                                applyFiltersAndSort();
                                                fetchScholarshipsFromApi();
                                            },
                                            last.get(Calendar.YEAR),
                                            last.get(Calendar.MONTH),
                                            last.get(Calendar.DAY_OF_MONTH));
                            to.setTitle("Hạn nộp đến ngày (bao gồm ngày chọn)");
                            to.getDatePicker().setMinDate(first.getTimeInMillis());
                            to.show();
                        },
                        initial.get(Calendar.YEAR),
                        initial.get(Calendar.MONTH),
                        initial.get(Calendar.DAY_OF_MONTH));
        from.setTitle("Hạn nộp từ ngày");
        from.setButton(
                DatePickerDialog.BUTTON_NEUTRAL,
                "Xóa hạn nộp",
                (dialog, which) -> {
                    startDate = null;
                    endDate = null;
                    btnFilterDeadline.setText(R.string.filter_deadline);
                    applyFiltersAndSort();
                    fetchScholarshipsFromApi();
                });
        from.show();
    }

    private void showSortDialog() {
        String[] sorts = {"Mới nhất", "Hạn nộp gần nhất", "Tên A–Z"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sắp xếp");
        builder.setItems(
                sorts,
                (dialog, which) -> {
                    currentSort = sorts[which];
                    if (tvSort != null) {
                        tvSort.setText(
                                "Mới nhất".equals(currentSort)
                                        ? getString(R.string.sort_label)
                                        : currentSort);
                    }
                    applyFiltersAndSort();
                    fetchScholarshipsFromApi();
                });
        builder.show();
    }
}
