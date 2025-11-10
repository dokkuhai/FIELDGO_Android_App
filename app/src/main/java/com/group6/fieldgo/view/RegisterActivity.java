package com.group6.fieldgo.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.group6.fieldgo.R;
import com.group6.fieldgo.api.AuthApi;
import com.group6.fieldgo.api.RetrofitClient;
import com.group6.fieldgo.model.ApiResponse;
import com.group6.fieldgo.model.RegisterRequest;
import com.group6.fieldgo.util.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private AuthApi api;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tokenManager = new TokenManager(this);
        api = RetrofitClient.create(tokenManager);

        EditText etName = findViewById(R.id.etName);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPhone = findViewById(R.id.etPhone);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnRegister = findViewById(R.id.btnRegister);
        Button btnBackToLogin = findViewById(R.id.btnBackToLogin);

        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            //  Kiểm tra các trường rỗng
            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            //  Kiểm tra định dạng email hợp lệ
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            //  Kiểm tra số điện thoại hợp lệ: 10 số, bắt đầu bằng 0
            if (!phone.matches("^0\\d{9}$")) {
                Toast.makeText(this, "Số điện thoại phải có 10 số và bắt đầu bằng 0", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gửi API đăng ký
            register(name, email, password, phone);
        });

        btnBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }



    private void register(String name, String email, String password, String phone) {
        RegisterRequest request = new RegisterRequest(name, email, password, phone);
        api.register(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this,
                            "Đăng ký thành công, vui lòng kiểm tra hộp thư để xác nhận tài khoản!",
                            Toast.LENGTH_SHORT).show();
                    // Delay 1 giây rồi mới chuyển sang LoginActivity
                    new android.os.Handler().postDelayed(() -> {
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    }, 1000);
                } else {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại. Kiểm tra lại thông tin.", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
