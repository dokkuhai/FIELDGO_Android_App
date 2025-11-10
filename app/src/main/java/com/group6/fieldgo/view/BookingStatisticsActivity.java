package com.group6.fieldgo.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.group6.fieldgo.R;
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

public class BookingStatisticsActivity extends AppCompatActivity {

    private TextView tvTotalBookings, tvPendingBookings, tvConfirmedBookings, tvCancelledBookings;
    private TextView tvTotalSpent, tvAverageSpent;
    private TextView tvTop1, tvTop2, tvTop3;
    private BarChart barChart;
    private Button btnViewMonthlyReport, btnBack;
    private ProgressBar progressBar;

    private BookingApi bookingApi;
    private List<Booking> allBookings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_statistics);

        // Khởi tạo API
        TokenManager tokenManager = new TokenManager(this);
        bookingApi = RetrofitClient.createBookingApi(tokenManager);

        setupViews();
        loadStatistics();
    }

    private void setupViews() {
        // TextViews
        tvTotalBookings = findViewById(R.id.tvTotalBookings);
        tvPendingBookings = findViewById(R.id.tvPendingBookings);
        tvConfirmedBookings = findViewById(R.id.tvConfirmedBookings);
        tvCancelledBookings = findViewById(R.id.tvCancelledBookings);
        tvTotalSpent = findViewById(R.id.tvTotalSpent);
        tvAverageSpent = findViewById(R.id.tvAverageSpent);
        tvTop1 = findViewById(R.id.tvTop1);
        tvTop2 = findViewById(R.id.tvTop2);
        tvTop3 = findViewById(R.id.tvTop3);

        // Chart
        barChart = findViewById(R.id.barChart);

        // Buttons
        btnViewMonthlyReport = findViewById(R.id.btnViewMonthlyReport);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);

        btnBack.setOnClickListener(v -> finish());

        btnViewMonthlyReport.setOnClickListener(v -> {
            Intent intent = new Intent(this, MonthlyBookingReportActivity.class);
            startActivity(intent);
        });
    }

    private void loadStatistics() {
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
                        calculateAndDisplayStatistics();
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

    private void calculateAndDisplayStatistics() {
        if (allBookings.isEmpty()) {
            Toast.makeText(this, "Chưa có booking nào", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Tổng quan
        int total = allBookings.size();
        int pending = 0, confirmed = 0, cancelled = 0;
        double totalSpent = 0;

        for (Booking b : allBookings) {
            String status = b.getStatus().toUpperCase();
            if (status.contains("PENDING")) pending++;
            else if (status.contains("CONFIRMED")) confirmed++;
            else if (status.contains("CANCELLED")) cancelled++;

            if (b.getPrice() != null) {
                totalSpent += b.getPrice();
            }
        }

        double averageSpent = total > 0 ? totalSpent / total : 0;

        // Hiển thị tổng quan
        tvTotalBookings.setText("Tổng booking: " + total);
        tvPendingBookings.setText("• Chờ xác nhận: " + pending);
        tvConfirmedBookings.setText("• Đã xác nhận: " + confirmed);
        tvCancelledBookings.setText("• Đã hủy: " + cancelled);
        tvTotalSpent.setText(String.format("Tổng: %.0f VNĐ", totalSpent));
        tvAverageSpent.setText(String.format("Trung bình: %.0f VNĐ/booking", averageSpent));

        // 2. Chart: Chi tiêu 6 tháng gần đây
        setupBarChart();

        // 3. Top 3 sân yêu thích
        displayTopCourts();
    }

    private void setupBarChart() {
        // Tính chi tiêu theo tháng (6 tháng gần nhất)
        Map<String, Double> monthlySpending = new HashMap<>();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM/yyyy", Locale.getDefault());

        for (Booking b : allBookings) {
            if (b.getPrice() != null && b.getBookingDate() != null) {
                try {
                    // Parse bookingDate (format: "2025-10-22")
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date date = inputFormat.parse(b.getBookingDate());
                    String month = monthFormat.format(date);

                    monthlySpending.put(month,
                            monthlySpending.getOrDefault(month, 0.0) + b.getPrice());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Lấy 6 tháng gần nhất
        List<String> last6Months = getLast6Months();
        List<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < last6Months.size(); i++) {
            String month = last6Months.get(i);
            double spending = monthlySpending.getOrDefault(month, 0.0);
            entries.add(new BarEntry(i, (float) spending));
        }

        // Setup BarChart
        BarDataSet dataSet = new BarDataSet(entries, "Chi tiêu (VNĐ)");
        dataSet.setColor(Color.parseColor("#4CAF50"));
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        // Custom X-axis (tháng)
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(last6Months));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        // Style
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(true);
        barChart.animateY(1000);
        barChart.invalidate();
    }

    private List<String> getLast6Months() {
        List<String> months = new ArrayList<>();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        Calendar cal = Calendar.getInstance();

        for (int i = 5; i >= 0; i--) {
            cal.add(Calendar.MONTH, -i);
            months.add(monthFormat.format(cal.getTime()));
            cal.add(Calendar.MONTH, i); // reset
        }

        return months;
    }

    private void displayTopCourts() {
        // Đếm số lần đặt từng sân
        Map<String, Integer> courtCount = new HashMap<>();

        for (Booking b : allBookings) {
            String courtName = b.getCourtName();
            courtCount.put(courtName, courtCount.getOrDefault(courtName, 0) + 1);
        }

        // Sort theo số lần đặt
        List<Map.Entry<String, Integer>> sortedCourts = new ArrayList<>(courtCount.entrySet());
        sortedCourts.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        // Hiển thị top 3
        if (sortedCourts.size() > 0) {
            tvTop1.setText("1. " + sortedCourts.get(0).getKey() + " - " + sortedCourts.get(0).getValue() + " lần");
        } else {
            tvTop1.setText("1. Chưa có dữ liệu");
        }

        if (sortedCourts.size() > 1) {
            tvTop2.setText("2. " + sortedCourts.get(1).getKey() + " - " + sortedCourts.get(1).getValue() + " lần");
        } else {
            tvTop2.setText("2. ---");
        }

        if (sortedCourts.size() > 2) {
            tvTop3.setText("3. " + sortedCourts.get(2).getKey() + " - " + sortedCourts.get(2).getValue() + " lần");
        } else {
            tvTop3.setText("3. ---");
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
