package com.group6.fieldgo.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.group6.fieldgo.MainActivity;
import com.group6.fieldgo.R;

import java.text.NumberFormat;
import java.util.Locale;

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

    // ⭐ Dữ liệu nhận từ Intent
    private int courtId;
    private int slotId;
    private String bookingId;
    private double price; // Số tiền gốc (dạng double)
    private String courtName;

    // Data đã xử lý
    private String formattedAmount; // Ví dụ: "300,000 đ"
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
            // Lấy dữ liệu theo khóa mới từ BookingActivity
            bookingId = intent.getStringExtra("BOOKING_ID");
            courtId = intent.getIntExtra("COURT_ID", -1);
            slotId = intent.getIntExtra("SLOT_ID", -1);
            price = intent.getDoubleExtra("PRICE", 0.0);
            courtName = intent.getStringExtra("COURT_NAME");
        }

        // Giá trị mặc định nếu không có dữ liệu
        if (bookingId == null || bookingId.isEmpty()) {
            bookingId = "ERR_ID";
        }

        // 1. Xử lý số tiền và định dạng từ 'price' (double)
        formattedAmount = formatPrice(price);
        rawAmount = String.valueOf((int) price); // Số nguyên không dấu phẩy

        // 2. Tạo Nội dung chuyển khoản (Sử dụng bookingId đã nhận)
        transferContent = "FGBKO" + bookingId;

        // 3. Thông tin ngân hàng (Giả lập vì chưa có API trả về)
        bankName = "MBBank";
        accountName = "VU DUC HAI";
        accountNumber = "VQRQAELEU3708";
    }

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
        tvAmount.setText(formattedAmount);
        tvBankName.setText(bankName);
        tvAccountName.setText(accountName);
        tvAccountNumber.setText(accountNumber);
        tvTransferAmount.setText(formattedAmount);
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

    private void confirmPayment() {
        // Hiển thị loading nếu cần
        btnConfirmPayment.setEnabled(false);
        btnConfirmPayment.setText("Đang xử lý...");

        // Tạm thời giả lập thành công
        new android.os.Handler().postDelayed(() -> {
            // Tắt loading
            btnConfirmPayment.setEnabled(true);
            btnConfirmPayment.setText("Tôi đã thanh toán");

            Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_LONG).show();

            // ⭐ 1. Khởi chạy màn hình thông báo thành công (ví dụ: PaymentSuccessActivity)
            // PaymentSuccessActivity.start(this); // Giả định có phương thức này

            // ⭐ 2. CHUYỂN VỀ MÀN HÌNH CHÍNH VÀ DỌN DẸP BACK STACK
            Intent mainIntent = new Intent(this, MainActivity.class); // <-- THAY MainActivity BẰNG TÊN ACTIVITY CHÍNH CỦA BẠN
            // Thêm cờ để xóa tất cả các Activity trên back stack và tạo Activity chính mới
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainIntent);

            // 3. Kết thúc màn hình hiện tại
            finish();

        }, 1500);
    }

    private void loadQRCode() {
        String encodedDescription = Uri.encode(transferContent);

        // Xây dựng URL QR code với các tham số
        String qrUrl = Uri.parse(QR_ENDPOINT_BASE).buildUpon()
                .appendQueryParameter("acc", accountNumber)
                .appendQueryParameter("bank", bankName)
                .appendQueryParameter("amount", rawAmount)
                .appendQueryParameter("des", encodedDescription)
                .build()
                .toString();

        // Load QR code bằng Glide
        Glide.with(this)
                .load(qrUrl)
                .placeholder(R.drawable.ic_qr_placeholder)
                .error(R.drawable.ic_qr_placeholder)
                .into(ivQrCode);
    }

    // Định dạng số tiền
    private String formatPrice(double price) {
        NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
        return format.format(price) + " đ";
    }

    // Phương thức tĩnh để khởi chạy Activity
    public static void start(Context context, String bookingId, int courtId, int slotId, double price, String courtName) {
        Intent intent = new Intent(context, PaymentsActivity.class);
        intent.putExtra("BOOKING_ID", bookingId);
        intent.putExtra("COURT_ID", courtId);
        intent.putExtra("SLOT_ID", slotId);
        intent.putExtra("PRICE", price);
        intent.putExtra("COURT_NAME", courtName);
        context.startActivity(intent);
    }
}