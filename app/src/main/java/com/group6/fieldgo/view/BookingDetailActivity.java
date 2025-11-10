package com.group6.fieldgo.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.group6.fieldgo.R;
import com.group6.fieldgo.api.BookingApi;
import com.group6.fieldgo.api.RetrofitClient;
import com.group6.fieldgo.model.ApiResponse;
import com.group6.fieldgo.model.Booking;
import com.group6.fieldgo.util.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingDetailActivity extends AppCompatActivity {

    public static final String EXTRA_BOOKING = "extra_booking";

    private TextView tvCourtName, tvAddress, tvDate, tvTimeSlot, tvPrice, tvStatus;
    private Button btnViewHistory, btnCancel, btnBack;
    private ProgressBar progressBar;

    private Booking booking;
    private BookingApi bookingApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        // Khởi tạo API
        TokenManager tokenManager = new TokenManager(this);
        bookingApi = RetrofitClient.createBookingApi(tokenManager);

        // Get booking từ intent
        booking = (Booking) getIntent().getSerializableExtra(EXTRA_BOOKING);
        if (booking == null) {
            Toast.makeText(this, "Không tìm thấy thông tin booking", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupViews();
        displayBookingInfo();
    }

    private void setupViews() {
        tvCourtName = findViewById(R.id.tvCourtName);
        tvAddress = findViewById(R.id.tvAddress);
        tvDate = findViewById(R.id.tvDate);
        tvTimeSlot = findViewById(R.id.tvTimeSlot);
        tvPrice = findViewById(R.id.tvPrice);
        tvStatus = findViewById(R.id.tvStatus);
        btnViewHistory = findViewById(R.id.btnViewHistory);
        btnCancel = findViewById(R.id.btnCancel);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);

        // Nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // Nút xem lịch sử
        btnViewHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingStatusHistoryActivity.class);
            intent.putExtra(BookingStatusHistoryActivity.EXTRA_BOOKING, booking);
            startActivity(intent);
        });

        // Nút hủy booking
        btnCancel.setOnClickListener(v -> showCancelDialog());

        // Ẩn nút hủy nếu đã hủy hoặc completed
        if ("CANCELLED".equalsIgnoreCase(booking.getStatus()) ||
                "COMPLETED".equalsIgnoreCase(booking.getStatus())) {
            btnCancel.setVisibility(android.view.View.GONE);
        }
    }

    private void displayBookingInfo() {
        tvCourtName.setText(booking.getCourtName());
        tvAddress.setText(booking.getAddress());
        tvDate.setText("📅 Ngày: " + booking.getBookingDate());

        String timeSlot = booking.getTimeslot() != null ? booking.getTimeslot() : "Chưa xác định";
        tvTimeSlot.setText("⏰ Giờ: " + timeSlot);

        tvPrice.setText(String.format("💰 Giá: %.0f VNĐ", booking.getPrice()));
        tvStatus.setText("🏷️ Trạng thái: " + booking.getStatus());

        // Đổi màu status
        if ("CONFIRMED".equalsIgnoreCase(booking.getStatus())) {
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else if ("PENDING".equalsIgnoreCase(booking.getStatus())) {
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
        } else if ("CANCELLED".equalsIgnoreCase(booking.getStatus())) {
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    private void showCancelDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận hủy")
                .setMessage("Bạn có chắc muốn hủy đặt sân này?\n\n" +
                        "Sân: " + booking.getCourtName() + "\n" +
                        "Ngày: " + booking.getBookingDate())
                .setPositiveButton("Hủy đặt sân", (dialog, which) -> cancelBooking())
                .setNegativeButton("Không", null)
                .show();
    }

    private void cancelBooking() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        btnCancel.setEnabled(false);

        BookingApi.CancelBookingRequest request =
                new BookingApi.CancelBookingRequest(booking.getBookingId());

        bookingApi.cancelBooking(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                progressBar.setVisibility(android.view.View.GONE);
                btnCancel.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(BookingDetailActivity.this,
                            "Đã hủy đặt sân thành công", Toast.LENGTH_SHORT).show();

                    // Cập nhật status và UI
                    booking.setStatus("CANCELLED");
                    displayBookingInfo();
                    btnCancel.setVisibility(android.view.View.GONE);

                    // Set result để BookingsActivity refresh
                    setResult(RESULT_OK);
                } else {
                    Toast.makeText(BookingDetailActivity.this,
                            "Không thể hủy booking", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                progressBar.setVisibility(android.view.View.GONE);
                btnCancel.setEnabled(true);
                Toast.makeText(BookingDetailActivity.this,
                        "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}



