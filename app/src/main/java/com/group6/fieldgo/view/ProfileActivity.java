package com.group6.fieldgo.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.group6.fieldgo.R;
import com.group6.fieldgo.api.AuthApi;
import com.group6.fieldgo.api.RetrofitClient;
import com.group6.fieldgo.model.ApiResponse;
import com.group6.fieldgo.model.UserProfileResponse;
import com.group6.fieldgo.util.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private TokenManager tokenManager;
    private AuthApi api;
    private TextView tvName, tvEmail, tvPhone;
    private Button btnMyBookings, btnLogout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tokenManager = new TokenManager(this);
        api = RetrofitClient.create(tokenManager);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        btnMyBookings = findViewById(R.id.btnMyBookings);
        btnLogout = findViewById(R.id.btnLogout);

        loadProfile();

        // Nút "Sân đã đặt"
        btnMyBookings.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, BookingsActivity.class);
            startActivity(intent);
        });

        // Nút "Đăng xuất"
        btnLogout.setOnClickListener(v -> {
            tokenManager.clearToken();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadProfile() {
        api.getProfile().enqueue(new Callback<ApiResponse<UserProfileResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserProfileResponse>> call, Response<ApiResponse<UserProfileResponse>> response) {
                if(response.isSuccessful() && response.body() != null){
                    UserProfileResponse profile = response.body().getData();
                    tvName.setText(profile.getName());
                    tvEmail.setText(profile.getEmail());
                    tvPhone.setText(profile.getPhone());
                } else {
                    Toast.makeText(ProfileActivity.this, "Không thể tải profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserProfileResponse>> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}


