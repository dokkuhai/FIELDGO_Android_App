package com.group6.fieldgo.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.group6.fieldgo.MainActivity;
import com.group6.fieldgo.R;

public class PaymentSuccessActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success); // Layout mới

        MaterialButton btnGoHome = findViewById(R.id.btnGoHome);
        MaterialButton btnViewBookings = findViewById(R.id.btnViewBookings);

        // Nút về Trang Home
        btnGoHome.setOnClickListener(v -> {
            // TODO: Thay thế YourHomeActivity.class bằng Activity Trang chủ thực tế của bạn
            Intent homeIntent = new Intent(PaymentSuccessActivity.this, MainActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            finish();
        });

        // Nút xem Sân đã đặt
        btnViewBookings.setOnClickListener(v -> {
            // TODO: Thay thế BookingsActivity.class bằng Activity xem sân đã đặt thực tế
            Intent bookingsIntent = new Intent(PaymentSuccessActivity.this, ProfileActivity.class);
            startActivity(bookingsIntent);
            finish();
        });
    }

    // Phương thức tĩnh để khởi chạy Activity này
    public static void start(Context context) {
        Intent intent = new Intent(context, PaymentSuccessActivity.class);
        context.startActivity(intent);
    }
}