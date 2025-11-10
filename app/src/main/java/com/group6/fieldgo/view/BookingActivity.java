// view/BookingActivity.java
package com.group6.fieldgo.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.group6.fieldgo.R;
import com.group6.fieldgo.adapter.SlotAdapter;
import com.group6.fieldgo.adapter.WeekDayAdapter;
import com.group6.fieldgo.api.RetrofitClient;
import com.group6.fieldgo.model.SlotResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity extends BaseActivity {

    private int courtId;
    private String courtName;
    private MaterialTextView tvCourtName;
    private RecyclerView rvWeekDays, rvSlots;
    private MaterialButton btnConfirm;
    private SlotAdapter slotAdapter;
    private String selectedDate;
    private SlotResponse.Slot selectedSlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

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
        loadSlots("2025-11-10"); // Gọi API với ngày đầu tuần
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
        RetrofitClient.createBookingService()
                .getSlots(courtId, startDate)
                .enqueue(new Callback<SlotResponse>() {
                    @Override
                    public void onResponse(Call<SlotResponse> call, Response<SlotResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            SlotResponse.SlotData data = response.body().getData();
                            renderWeekDays(data.getWeekDays());
                            renderSlots(data);
                        } else {
                            Toast.makeText(BookingActivity.this, "Không lấy được dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<SlotResponse> call, Throwable t) {
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
                    btnConfirm.setText("Xác nhận: " + date.substring(8) + " | " + slot.getTimeRange());
                }
        );
        rvSlots.setAdapter(slotAdapter);
    }

    private void confirmBooking() {
        if (selectedSlot == null || selectedDate == null) return;

        // GỌI API ĐẶT SÂN Ở ĐÂY (sau khi có API)
        // Ví dụ: RetrofitClient.createBookingService().bookSlot(courtId, selectedDate, selectedSlot.getId())

        Toast.makeText(this,
                "Đặt sân thành công!\n" +
                        selectedDate.substring(8) + " | " + selectedSlot.getTimeRange() + "\n" +
                        "Giá: " + formatPrice(selectedSlot.getPrice()),
                Toast.LENGTH_LONG).show();

        // Trả kết quả về CourtDetailActivity
        Intent result = new Intent();
        result.putExtra("BOOKED_DATE", selectedDate);
        result.putExtra("BOOKED_TIME", selectedSlot.getTimeRange());
        setResult(RESULT_OK, result);

        finish(); // Quay về chi tiết sân
    }

    private String formatPrice(double price) {
        return java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN")).format(price) + "đ";
    }
}