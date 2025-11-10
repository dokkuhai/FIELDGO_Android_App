
package com.group6.fieldgo.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.group6.fieldgo.R;
import com.group6.fieldgo.adapter.BookingAdapter;
import com.group6.fieldgo.api.BookingApi;
import com.group6.fieldgo.api.RetrofitClient;
import com.group6.fieldgo.model.ApiResponse;
import com.group6.fieldgo.model.Booking;
import com.group6.fieldgo.util.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookingsActivity extends AppCompatActivity implements BookingAdapter.OnBookingActionListener {

    private BookingApi bookingApi;
    private BookingAdapter adapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvEmpty;
    private FloatingActionButton fabStatistics;

    private List<Booking> allBookings = new ArrayList<>();
    private String currentFilter = "ALL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings);

        TokenManager tokenManager = new TokenManager(this);
        if (tokenManager.getToken() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        bookingApi = RetrofitClient.createBookingApi(tokenManager);
        setupViews();
        loadBookings();
    }

    private void setupViews() {
        progressBar = findViewById(R.id.progressBar);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        tvEmpty = findViewById(R.id.tvEmpty);
        RecyclerView recyclerView = findViewById(R.id.rvBookings);
        fabStatistics = findViewById(R.id.fabStatistics);

        // FAB mở Statistics
        fabStatistics.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingStatisticsActivity.class);
            startActivity(intent);
        });

        TabLayout tabLayout = findViewById(R.id.tabStatus);
        tabLayout.addTab(tabLayout.newTab().setText("Tất cả"));
        tabLayout.addTab(tabLayout.newTab().setText("Đã xác nhận"));
        tabLayout.addTab(tabLayout.newTab().setText("Chờ xác nhận"));
        tabLayout.addTab(tabLayout.newTab().setText("Đã hủy"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: currentFilter = "ALL"; break;
                    case 1: currentFilter = "CONFIRMED"; break;
                    case 2: currentFilter = "PENDING"; break;
                    case 3: currentFilter = "CANCELLED"; break;
                }
                filterBookings();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                loadBookings();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new BookingAdapter(this);
        recyclerView.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::loadBookings);
    }

    private void loadBookings() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        bookingApi.getMyBookings().enqueue(new Callback<ApiResponse<List<Booking>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Booking>>> call,
                                   Response<ApiResponse<List<Booking>>> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Booking>> apiResponse = response.body();

                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        allBookings = apiResponse.getData();
                        filterBookings();
                    } else {
                        showError(apiResponse.getMessage());
                    }
                } else {
                    showError("Không thể tải danh sách booking");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Booking>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void filterBookings() {
        List<Booking> filtered;

        if ("ALL".equals(currentFilter)) {
            filtered = allBookings;
        } else {
            filtered = allBookings.stream()
                    .filter(b -> currentFilter.equalsIgnoreCase(b.getStatus()))
                    .collect(Collectors.toList());
        }

        adapter.setBookings(filtered);

        if (filtered.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText("Không có booking nào");
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBookingClick(Booking booking) {
        // Đã handle trong adapter
    }

    @Override
    public void onCancelClick(Booking booking) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận hủy")
                .setMessage("Bạn có chắc muốn hủy đặt sân này?\n\n" +
                        "Sân: " + booking.getCourtName() + "\n" +
                        "Ngày: " + booking.getBookingDate())
                .setPositiveButton("Hủy đặt sân", (dialog, which) -> {
                    cancelBooking(booking.getBookingId());
                })
                .setNegativeButton("Không", null)
                .show();
    }

    private void cancelBooking(Long bookingId) {
        progressBar.setVisibility(View.VISIBLE);

        BookingApi.CancelBookingRequest request =
                new BookingApi.CancelBookingRequest(bookingId);

        bookingApi.cancelBooking(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call,
                                   Response<ApiResponse<Void>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        Toast.makeText(BookingsActivity.this,
                                "Đã hủy đặt sân thành công", Toast.LENGTH_SHORT).show();
                        loadBookings();
                    } else {
                        showError(apiResponse.getMessage());
                    }
                } else {
                    showError("Không thể hủy booking");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showError("Lỗi: " + t.getMessage());
            }
        });
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}