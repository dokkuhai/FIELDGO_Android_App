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

public class ProfileActivity extends BaseActivity {
    private TokenManager tokenManager;
    private AuthApi api;
    private TextView tvName, tvEmail, tvPhone;
    private Button btnUpdateProfile, btnChangePassword, btnSettings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        tokenManager = new TokenManager(this);
        api = RetrofitClient.create(tokenManager);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        loadProfile();
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnSettings = findViewById(R.id.btnSettings);
        btnUpdateProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, UpdateProfileActivity.class);
            intent.putExtra("name", tvName.getText().toString());
            intent.putExtra("phone", tvPhone.getText().toString());
            startActivity(intent);
        });
        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
            startActivityForResult(intent, 100);;
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            boolean langChanged = data.getBooleanExtra("languageChanged", false);
            if (langChanged) {
                recreate();
            }
        }
    }

    private void loadProfile() {
        api.getProfile().enqueue(new Callback<ApiResponse<UserProfileResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserProfileResponse>> call, Response<ApiResponse<UserProfileResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponse profile = response.body().getData();
                    tvName.setText(profile.getName());
                    tvEmail.setText(profile.getEmail());
                    tvPhone.setText(profile.getPhone());
                } else {
                    Toast.makeText(ProfileActivity.this, getString(R.string.error_load_profile), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserProfileResponse>> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, getString(R.string.error_network, t.getMessage()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfile();
    }
}