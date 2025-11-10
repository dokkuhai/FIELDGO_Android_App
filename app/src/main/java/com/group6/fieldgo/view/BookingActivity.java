package com.group6.fieldgo.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.group6.fieldgo.R;
import com.group6.fieldgo.adapter.SlotAdapter;
import com.group6.fieldgo.adapter.WeekDayAdapter;
import com.group6.fieldgo.api.BookingApiService;
import com.group6.fieldgo.api.RetrofitClient;
import com.group6.fieldgo.model.BookingRequest;
import com.group6.fieldgo.model.BookingResponse;
import com.group6.fieldgo.model.SlotResponse;
import com.group6.fieldgo.util.TokenManager; // Cần import TokenManager
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity extends BaseActivity {

    private static final int REQUEST_CODE_PAYMENT = 101;

    private int courtId;
    private String courtName;
    private MaterialTextView tvCourtName;
    private RecyclerView rvWeekDays, rvSlots;
    private MaterialButton btnConfirm;
    private SlotAdapter slotAdapter;
    private String selectedDate;
    private SlotResponse.Slot selectedSlot;

    private BookingApiService bookingApiService;
    private TokenManager tokenManager; // Khai báo TokenManager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Khởi tạo TokenManager VỚI Context và API Service sử dụng client có token
        tokenManager = new TokenManager(this); // <-- Đã sửa: Truyền Context
        bookingApiService = RetrofitClient.createBookingService(tokenManager);

        // Lấy dữ liệu từ Intent
        courtId = getIntent().getIntExtra("COURT_ID", -1);
        courtName = getIntent().getStringExtra("COURT_NAME");
        if (courtId == -1 || courtName == null) {
            Toast.makeText(this, "Dữ liệu sân không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        // Lấy ngày hiện tại hoặc ngày đầu tuần thực tế
        loadSlots("2025-11-10");
    }

    private void initViews() {
        tvCourtName = findViewById(R.id.tvCourtName);
        rvWeekDays = findViewById(R.id.rvWeekDays);
        rvSlots = findViewById(R.id.rvSlots);
        btnConfirm = findViewById(R.id.btnConfirm);

        tvCourtName.setText(courtName);

        // Cấu hình RecyclerView
        rvWeekDays.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvSlots.setLayoutManager(new LinearLayoutManager(this));
        rvSlots.setNestedScrollingEnabled(false);

        // Nút xác nhận
        btnConfirm.setEnabled(false);
        btnConfirm.setOnClickListener(v -> confirmBooking());
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Đặt sân");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadSlots(String startDate) {
        // Dùng bookingApiService vì nó đã được khởi tạo
        bookingApiService
                .getSlots(courtId, startDate)
                .enqueue(new Callback<SlotResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<SlotResponse> call, @NonNull Response<SlotResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            SlotResponse.SlotData data = response.body().getData();
                            renderWeekDays(data.getWeekDays());
                            renderSlots(data);
                        } else {
                            Toast.makeText(BookingActivity.this, "Không lấy được dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<SlotResponse> call, @NonNull Throwable t) {
                        Toast.makeText(BookingActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void renderWeekDays(java.util.List<SlotResponse.WeekDay> weekDays) {
        WeekDayAdapter adapter = new WeekDayAdapter(weekDays, position -> {
            // Khi chọn ngày → cập nhật bảng khung giờ
            slotAdapter.setSelectedDate(weekDays.get(position).getDate());
            rvSlots.scrollToPosition(0);
        });
        rvWeekDays.setAdapter(adapter);
    }

    private void renderSlots(SlotResponse.SlotData data) {
        slotAdapter = new SlotAdapter(
                data.getSlots(),
                data.getWeekDays(),
                data.getBookedSlots(),
                (date, slot) -> {
                    selectedDate = date;
                    selectedSlot = slot;
                    btnConfirm.setEnabled(true);
                    btnConfirm.setText("Xác nhận: " + date.substring(8) + " | " + slot.getTimeRange() + " - " + formatPrice(slot.getPrice()));
                }
        );
        rvSlots.setAdapter(slotAdapter);
    }

    /**
     * Bắt đầu quá trình đặt sân: Gọi API đặt sân để lấy Booking ID trước khi chuyển sang thanh toán.
     */
    private void confirmBooking() {
        if (selectedSlot == null || selectedDate == null) {
            Toast.makeText(this, "Vui lòng chọn ngày và giờ.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Vô hiệu hóa nút và hiển thị trạng thái đang chờ
        btnConfirm.setEnabled(false);
        btnConfirm.setText("Đang đặt chỗ...");

        // Chuẩn bị request body. Dùng 'selectedDate' cho trường 'bookingDate'
        BookingRequest request = new BookingRequest(courtId, selectedSlot.getId(), selectedDate);

        // GỌI API ĐẶT SÂN (Sử dụng client có token)
        bookingApiService.bookSlot(request).enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(@NonNull Call<BookingResponse> call, @NonNull Response<BookingResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {

                    String bookingId = response.body().getData().getBookingId();

                    if (bookingId != null && !bookingId.isEmpty()) {
                        // Đặt chỗ thành công, chuyển sang màn hình thanh toán
                        startPaymentActivity(bookingId);
                    } else {
                        // Lỗi logic: API thành công nhưng không trả về bookingId
                        handleBookingFailure("API không trả về mã đặt chỗ.");
                    }
                } else {
                    // Lỗi từ server (4xx, 5xx, hoặc success=false)
                    String message = response.body() != null && response.body().getMessage() != null
                            ? response.body().getMessage()
                            : "Đặt sân thất bại (Mã: " + response.code() + ").";
                    handleBookingFailure(message);
                }
            }

            @Override
            public void onFailure(@NonNull Call<BookingResponse> call, @NonNull Throwable t) {
                // Lỗi mạng/kết nối
                handleBookingFailure("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    /**
     * Bắt đầu Activity thanh toán với Booking ID thực tế.
     */
    private void startPaymentActivity(String bookingId) {
        // Khôi phục nút về trạng thái có thể bấm
        btnConfirm.setEnabled(true);
        btnConfirm.setText("Đã đặt chỗ, chuyển sang thanh toán");

        // ⭐ KHẮC PHỤC LỖI: Bổ sung tham số thứ 6 (courtName)
        PaymentsActivity.start(
                this,
                bookingId, // 1. bookingId
                courtId,   // 2. courtId
                selectedSlot.getId(), // 3. slotId
                selectedSlot.getPrice(), // 4. price
                courtName // ⭐ 5. courtName (Tổng 6 tham số cùng Context)
        );

        // Lưu ý: Không dùng finish() ở đây. Việc finish() sẽ được xử lý trong onActivityResult
        // sau khi thanh toán thành công.
    }

    /**
     * Xử lý khi quá trình đặt sân thất bại.
     */
    private void handleBookingFailure(String message) {
        btnConfirm.setEnabled(true);
        btnConfirm.setText("Đặt sân thất bại, thử lại");
        Toast.makeText(this, "Đặt sân thất bại: " + message, Toast.LENGTH_LONG).show();
    }


    /**
     * Xử lý kết quả trả về từ PaymentActivity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == RESULT_OK) {
                // Thanh toán thành công!
                Toast.makeText(this, "Đặt sân và Thanh toán thành công!", Toast.LENGTH_LONG).show();

                // Trả kết quả về CourtDetailActivity
                Intent result = new Intent();
                result.putExtra("BOOKED_DATE", selectedDate);
                result.putExtra("BOOKED_TIME", selectedSlot.getTimeRange());
                setResult(RESULT_OK, result);
                finish(); // Quay về chi tiết sân

            } else if (resultCode == RESULT_CANCELED) {
                // Thanh toán bị hủy hoặc thất bại
                Toast.makeText(this, "Thanh toán bị hủy hoặc thất bại. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
            }

            // Đặt lại trạng thái nút sau khi quay lại từ thanh toán
            btnConfirm.setEnabled(selectedSlot != null);
            if (selectedSlot != null) {
                btnConfirm.setText("Xác nhận: " + selectedDate.substring(8) + " | " + selectedSlot.getTimeRange() + " - " + formatPrice(selectedSlot.getPrice()));
            }
        }
    }

    private String formatPrice(double price) {
        return java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN")).format(price) + "đ";
    }
}