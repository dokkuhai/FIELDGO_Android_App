// nghĩa 5: Báo cáo chi tiết theo tháng
package com.group6.fieldgo.view;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.group6.fieldgo.R;
import com.group6.fieldgo.adapter.MonthlyReportAdapter;
import com.group6.fieldgo.api.BookingApi;
import com.group6.fieldgo.api.RetrofitClient;
import com.group6.fieldgo.model.ApiResponse;
import com.group6.fieldgo.model.Booking;
import com.group6.fieldgo.util.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.text.SimpleDateFormat;
import java.util.*;

public class MonthlyBookingReportActivity extends AppCompatActivity {

    private Spinner spinnerMonth;
    private RecyclerView rvBookings;
    private TextView tvMonthSummary, tvEmpty;
    private Button btnBack;
    private ProgressBar progressBar;

    private BookingApi bookingApi;
    private List<Booking> allBookings = new ArrayList<>();
    private MonthlyReportAdapter adapter;

    private List<String> availableMonths = new ArrayList<>();
    private String selectedMonth = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_booking_report);

        // Khởi tạo API
        TokenManager tokenManager = new TokenManager(this);
        bookingApi = RetrofitClient.createBookingApi(tokenManager);

        setupViews();
        loadBookings();
    }

    private void setupViews() {
        spinnerMonth = findViewById(R.id.spinnerMonth);
        rvBookings = findViewById(R.id.rvBookings);
        tvMonthSummary = findViewById(R.id.tvMonthSummary);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);

        btnBack.setOnClickListener(v -> finish());

        // Setup RecyclerView
        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MonthlyReportAdapter();
        rvBookings.setAdapter(adapter);

        // Spinner listener
        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMonth = availableMonths.get(position);
                filterBookingsByMonth(selectedMonth);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadBookings() {
        progressBar.setVisibility(View.VISIBLE);

        bookingApi.getMyBookings().enqueue(new Callback<ApiResponse<List<Booking>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Booking>>> call,
                                   Response<ApiResponse<List<Booking>>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Booking>> apiResponse = response.body();

                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        allBookings = apiResponse.getData();
                        setupMonthSpinner();
                    } else {
                        showError(apiResponse.getMessage());
                    }
                } else {
                    showError("Không thể tải dữ liệu");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Booking>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showError("Lỗi: " + t.getMessage());
            }
        });
    }

    private void setupMonthSpinner() {
        // Lấy danh sách tháng có booking
        Set<String> monthSet = new HashSet<>();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM/yyyy", Locale.getDefault());

        for (Booking b : allBookings) {
            if (b.getBookingDate() != null) {
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date date = inputFormat.parse(b.getBookingDate());
                    String month = monthFormat.format(date);
                    monthSet.add(month);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Sort tháng (mới nhất trước)
        availableMonths = new ArrayList<>(monthSet);
        Collections.sort(availableMonths, (a, b) -> {
            try {
                SimpleDateFormat format = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
                Date dateA = format.parse(a);
                Date dateB = format.parse(b);
                return dateB.compareTo(dateA); // Reverse order
            } catch (Exception e) {
                return 0;
            }
        });

        if (availableMonths.isEmpty()) {
            availableMonths.add("Chưa có dữ liệu");
        }

        // Setup spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                availableMonths
        );
        spinnerMonth.setAdapter(spinnerAdapter);
    }

    private void filterBookingsByMonth(String month) {
        if (month.equals("Chưa có dữ liệu")) {
            adapter.setBookings(new ArrayList<>());
            tvEmpty.setVisibility(View.VISIBLE);
            tvMonthSummary.setText("Tổng: 0 VNĐ | Số booking: 0");
            return;
        }

        SimpleDateFormat monthFormat = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        List<Booking> filteredBookings = new ArrayList<>();
        double totalSpent = 0;

        for (Booking b : allBookings) {
            if (b.getBookingDate() != null) {
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date date = inputFormat.parse(b.getBookingDate());
                    String bookingMonth = monthFormat.format(date);

                    if (bookingMonth.equals(month)) {
                        filteredBookings.add(b);
                        if (b.getPrice() != null) {
                            totalSpent += b.getPrice();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Update UI
        if (filteredBookings.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvBookings.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvBookings.setVisibility(View.VISIBLE);
        }

        adapter.setBookings(filteredBookings);
        tvMonthSummary.setText(String.format(
                "Tổng: %.0f VNĐ | Số booking: %d",
                totalSpent,
                filteredBookings.size()
        ));
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}