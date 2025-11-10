package com.group6.fieldgo.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.group6.fieldgo.R;
import com.group6.fieldgo.api.AuthApi;
import com.group6.fieldgo.api.RetrofitClient;
import com.group6.fieldgo.model.ApiResponse;
import com.group6.fieldgo.model.UpdateProfileRequest;
import com.group6.fieldgo.model.UserProfileResponse;
import com.group6.fieldgo.util.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileActivity extends BaseActivity {

    private EditText etName, etPhone;
    private Button btnSave, btnCancel;

    private TokenManager tokenManager;
    private AuthApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        tokenManager = new TokenManager(this);
        api = RetrofitClient.create(tokenManager);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnBack);


        // có thể preload từ intent nếu ProfileActivity gửi sang
        etName.setText(getIntent().getStringExtra("name"));
        etPhone.setText(getIntent().getStringExtra("phone"));


        btnSave.setOnClickListener(v -> {
            if (validateInput()) {
                updateProfile();
            }
        });        btnCancel.setOnClickListener(v -> finish());
    }
    private boolean validateInput() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError(getString(R.string.error_name_empty));
            etName.requestFocus();
            return false;
        }

        if (phone.isEmpty()) {
            etPhone.setError(getString(R.string.error_phone_empty));
            etPhone.requestFocus();
            return false;
        }

        if (!phone.matches("\\d{9,11}")) {
            etPhone.setError(getString(R.string.error_phone_invalid));
            etPhone.requestFocus();
            return false;
        }

        return true;
    }
    private void updateProfile() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        UpdateProfileRequest request = new UpdateProfileRequest(name, phone);

        api.updateProfile(request).enqueue(new Callback<ApiResponse<UserProfileResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserProfileResponse>> call, Response<ApiResponse<UserProfileResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(UpdateProfileActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(UpdateProfileActivity.this, "Không thể cập nhật thông tin", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserProfileResponse>> call, Throwable t) {
                Toast.makeText(UpdateProfileActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
