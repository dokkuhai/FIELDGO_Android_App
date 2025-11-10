package com.group6.fieldgo.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri; // Import Uri
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.group6.fieldgo.R;

public class PaymentsActivity extends BaseActivity {

    private TextView tvAmount;
    private TextView tvBankName;
    private TextView tvAccountName;
    private TextView tvAccountNumber;
    private TextView tvTransferAmount;
    private TextView tvTransferContent;

    private ImageView ivQrCode;

    private ImageButton btnCopyBankName;
    private ImageButton btnCopyAccountName;
    private ImageButton btnCopyAccountNumber;
    private ImageButton btnCopyAmount;
    private ImageButton btnCopyContent;

    private MaterialButton btnConfirmPayment;

    private String bookingId;
    private String amount; // Ví dụ: "300,000"
    private String rawAmount; // Ví dụ: "300000" - dùng cho QR code
    private String bankName;
    private String accountName;
    private String accountNumber;
    private String transferContent; // Nội dung chuyển khoản

    // Endpoint tạo QR code
    private static final String QR_ENDPOINT_BASE = "https://qr.sepay.vn/img";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        // Nhận dữ liệu từ Intent
        getDataFromIntent();

        // Khởi tạo views
        initViews();

        // Hiển thị dữ liệu
        displayPaymentInfo();

        // Setup các nút copy
        setupCopyButtons();

        // Setup nút xác nhận
        setupConfirmButton();

        // Load QR code từ endpoint
        loadQRCode();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            bookingId = intent.getStringExtra("BOOKING_ID");
            amount = intent.getStringExtra("AMOUNT");
            bankName = intent.getStringExtra("BANK_NAME");
            accountName = intent.getStringExtra("ACCOUNT_NAME");
            accountNumber = intent.getStringExtra("ACCOUNT_NUMBER");
        }

        // Giá trị mặc định nếu không có dữ liệu
        if (bookingId == null) bookingId = "1";
        if (amount == null) amount = "300,000";
        if (bankName == null) bankName = "MBBank"; // Đổi thành MBBank cho phù hợp với Sepay mẫu
        if (accountName == null) accountName = "VU DUC HAI";
        if (accountNumber == null) accountNumber = "VQRQAELEU3708";

        // Xử lý số tiền để dùng cho QR code (loại bỏ dấu phẩy)
        rawAmount = amount.replace(",", "").replace(".", "");

        // 2. Tạo Nội dung chuyển khoản
        transferContent = "FGBKO" + bookingId;
    }

    // Các phương thức initViews(), displayPaymentInfo(), setupCopyButtons(), copyToClipboard(),
    // setupConfirmButton(), confirmPayment() giữ nguyên.

    private void initViews() {
        tvAmount = findViewById(R.id.tvAmount);
        tvBankName = findViewById(R.id.tvBankName);
        tvAccountName = findViewById(R.id.tvAccountName);
        tvAccountNumber = findViewById(R.id.tvAccountNumber);
        tvTransferAmount = findViewById(R.id.tvTransferAmount);
        tvTransferContent = findViewById(R.id.tvTransferContent);

        ivQrCode = findViewById(R.id.ivQrCode);

        btnCopyBankName = findViewById(R.id.btnCopyBankName);
        btnCopyAccountName = findViewById(R.id.btnCopyAccountName);
        btnCopyAccountNumber = findViewById(R.id.btnCopyAccountNumber);
        btnCopyAmount = findViewById(R.id.btnCopyAmount);
        btnCopyContent = findViewById(R.id.btnCopyContent);

        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);
    }

    private void displayPaymentInfo() {
        tvAmount.setText(amount + " đ");
        tvBankName.setText(bankName);
        tvAccountName.setText(accountName);
        tvAccountNumber.setText(accountNumber);
        tvTransferAmount.setText(amount + " đ");
        tvTransferContent.setText(transferContent);
    }

    private void setupCopyButtons() {
        btnCopyBankName.setOnClickListener(v -> {
            copyToClipboard(tvBankName.getText().toString());
        });

        btnCopyAccountName.setOnClickListener(v -> {
            copyToClipboard(tvAccountName.getText().toString());
        });

        btnCopyAccountNumber.setOnClickListener(v -> {
            copyToClipboard(tvAccountNumber.getText().toString());
        });

        btnCopyAmount.setOnClickListener(v -> {
            copyToClipboard(rawAmount); // Copy số tiền không dấu phẩy
        });

        btnCopyContent.setOnClickListener(v -> {
            copyToClipboard(tvTransferContent.getText().toString());
        });
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Đã sao chép: " + text, Toast.LENGTH_SHORT).show();
    }

    private void setupConfirmButton() {
        btnConfirmPayment.setOnClickListener(v -> {
            // TODO: Gọi API xác nhận thanh toán
            confirmPayment();
        });
    }

    // Trong PaymentsActivity.java

    private void confirmPayment() {
        // Hiển thị loading nếu cần
        btnConfirmPayment.setEnabled(false);
        btnConfirmPayment.setText("Đang xử lý...");

        // TODO: Gọi API xác nhận thanh toán ở đây (sau khi API thành công)

        // Tạm thời giả lập thành công (Thay thế Handler cũ)
        new android.os.Handler().postDelayed(() -> {
            // Tắt loading
            btnConfirmPayment.setEnabled(true);
            btnConfirmPayment.setText("Tôi đã thanh toán");

            // 1. Khởi chạy màn hình thông báo thành công
            PaymentSuccessActivity.start(this);

            // 2. Kết thúc màn hình thanh toán hiện tại (để người dùng không quay lại được)
            finish();

        }, 1500);
    }

    // ⭐ PHƯƠNG THỨC CẬP NHẬT: Load QR code từ Endpoint trả về ảnh trực tiếp
    private void loadQRCode() {
        // Endpoint tạo QR code
        final String QR_ENDPOINT_BASE = "https://qr.sepay.vn/img";

        // 1. Mã hóa nội dung chuyển khoản để an toàn trong URL
        String encodedDescription = android.net.Uri.encode(transferContent);

        // 2. Xây dựng URL QR code với các tham số
        String qrUrl = android.net.Uri.parse(QR_ENDPOINT_BASE).buildUpon()
                .appendQueryParameter("acc", accountNumber)
                .appendQueryParameter("bank", bankName)
                .appendQueryParameter("amount", rawAmount)
                .appendQueryParameter("des", encodedDescription)
                .build()
                .toString();

        // 3. Load QR code bằng Glide
        Glide.with(this)
                .load(qrUrl)
                .placeholder(R.drawable.ic_qr_placeholder) // Ảnh hiển thị khi đang tải
                .error(R.drawable.ic_qr_placeholder)      // Ảnh hiển thị khi lỗi
                .into(ivQrCode); // ivQrCode đã được ánh xạ trong initViews()
    }
    // Phương thức để start activity từ nơi khác
    public static void start(Context context, String bookingId, String amount,
                             String bankName, String accountName, String accountNumber) {
        Intent intent = new Intent(context, PaymentsActivity.class);
        intent.putExtra("BOOKING_ID", bookingId);
        intent.putExtra("AMOUNT", amount);
        intent.putExtra("BANK_NAME", bankName);
        intent.putExtra("ACCOUNT_NAME", accountName);
        intent.putExtra("ACCOUNT_NUMBER", accountNumber);
        context.startActivity(intent);
    }
}