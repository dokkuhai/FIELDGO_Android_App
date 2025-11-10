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
import com.group6.fieldgo.model.ChangePasswordRequest;
import com.group6.fieldgo.util.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends BaseActivity {

    private EditText etOldPass, etNewPass, etConfirmPass;
    private Button btnChange, btnCancel;
    private AuthApi api;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        tokenManager = new TokenManager(this);
        api = RetrofitClient.create(tokenManager);

        etOldPass = findViewById(R.id.etOldPass);
        etNewPass = findViewById(R.id.etNewPass);
        etConfirmPass = findViewById(R.id.etConfirmPass);
        btnChange = findViewById(R.id.btnChangePass);
        btnCancel = findViewById(R.id.btnCancel);
        btnChange.setOnClickListener(v -> changePassword());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void changePassword() {
        String oldPass = etOldPass.getText().toString().trim();
        String newPass = etNewPass.getText().toString().trim();
        String confirmPass = etConfirmPass.getText().toString().trim();

        if (oldPass.isEmpty()) {
            etOldPass.setError(getString(R.string.error_old_pass_empty));
            etOldPass.requestFocus();
            return;
        }

        if (newPass.isEmpty()) {
            etNewPass.setError(getString(R.string.error_new_pass_empty));
            etNewPass.requestFocus();
            return;
        }

        if (confirmPass.isEmpty()) {
            etConfirmPass.setError(getString(R.string.error_confirm_pass_empty));
            etConfirmPass.requestFocus();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            etConfirmPass.setError(getString(R.string.error_password_mismatch));
            etConfirmPass.requestFocus();
            return;
        }

        ChangePasswordRequest request = new ChangePasswordRequest(oldPass, newPass, confirmPass);

        api.changePassword(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(ChangePasswordActivity.this, getString(R.string.msg_change_success), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ChangePasswordActivity.this, getString(R.string.msg_change_fail), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(ChangePasswordActivity.this, getString(R.string.msg_network_error, t.getMessage()), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
