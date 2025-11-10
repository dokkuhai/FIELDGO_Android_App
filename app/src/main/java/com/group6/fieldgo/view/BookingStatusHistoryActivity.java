package com.group6.fieldgo.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.group6.fieldgo.R;
import com.group6.fieldgo.adapter.StatusHistoryAdapter;
import com.group6.fieldgo.model.Booking;
import com.group6.fieldgo.model.StatusHistoryItem;
import java.util.ArrayList;
import java.util.List;

public class BookingStatusHistoryActivity extends AppCompatActivity {

    public static final String EXTRA_BOOKING = "extra_booking";

    private TextView tvBookingInfo;
    private RecyclerView rvHistory;
    private Button btnBack;

    private Booking booking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_status_history);

        // Get booking từ intent
        booking = (Booking) getIntent().getSerializableExtra(EXTRA_BOOKING);
        if (booking == null) {
            finish();
            return;
        }

        setupViews();
        loadHistory();
    }

    private void setupViews() {
        tvBookingInfo = findViewById(R.id.tvBookingInfo);
        rvHistory = findViewById(R.id.rvHistory);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        // Setup RecyclerView
        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        // Hiển thị thông tin booking
        tvBookingInfo.setText(String.format(
                "Sân: %s\nNgày: %s",
                booking.getCourtName(),
                booking.getBookingDate()
        ));
    }

    private void loadHistory() {
        // LƯU Ý: Backend chưa có API lấy history
        // Nên mình sẽ fake data dựa trên status hiện tại

        List<StatusHistoryItem> historyItems = generateFakeHistory();

        StatusHistoryAdapter adapter = new StatusHistoryAdapter(historyItems);
        rvHistory.setAdapter(adapter);
    }

    private List<StatusHistoryItem> generateFakeHistory() {
        List<StatusHistoryItem> items = new ArrayList<>();

        String status = booking.getStatus();

        // Timeline theo thứ tự ngược (mới nhất trước)
        if ("CANCELLED".equalsIgnoreCase(status)) {
            items.add(new StatusHistoryItem(
                    "Đã hủy",
                    "Booking đã bị hủy",
                    getCurrentTime(),
                    "CANCELLED"
            ));
        }

        if (!"PENDING".equalsIgnoreCase(status)) {
            items.add(new StatusHistoryItem(
                    "Đã xác nhận",
                    "Admin đã xác nhận booking",
                    getTimeAgo(1),
                    "CONFIRMED"
            ));
        }

        // Luôn có trạng thái tạo mới
        items.add(new StatusHistoryItem(
                "Chờ xác nhận",
                "Booking được tạo thành công",
                getTimeAgo(2),
                "PENDING"
        ));

        return items;
    }

    private String getCurrentTime() {
        // Lấy thời gian hiện tại
        return new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                .format(new java.util.Date());
    }

    private String getTimeAgo(int hoursAgo) {
        // Tính thời gian cách đây X giờ
        long timeInMillis = System.currentTimeMillis() - (hoursAgo * 60 * 60 * 1000);
        return new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                .format(new java.util.Date(timeInMillis));
    }
}
