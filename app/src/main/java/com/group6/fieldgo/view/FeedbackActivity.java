package com.group6.fieldgo.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.group6.fieldgo.R;
import com.group6.fieldgo.adapter.FeedbackAdapter;
import com.group6.fieldgo.api.CourtApiService;
import com.group6.fieldgo.api.RetrofitClient;
import com.group6.fieldgo.model.FeedbackResponse;
import com.group6.fieldgo.model.PagedFeedbackData;
import com.group6.fieldgo.util.PaginationScrollListener; // Đổi từ ui.common sang util
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FeedbackAdapter adapter;
    private CourtApiService courtApiService;

    // Biến trạng thái phân trang
    public static final String EXTRA_COURT_ID = "extra_court_id"; // Key để nhận ID
    private static final int PAGE_SIZE = 10;

    private int courtId;
    private int currentPage = 1; // Bắt đầu từ trang 1 theo logic API
    private boolean isLoading = false;
    private boolean isLastPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // ⭐ 1. Nhận COURT_ID từ Intent
        courtId = getIntent().getIntExtra(EXTRA_COURT_ID, -1);

        if (courtId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID sân bóng.", Toast.LENGTH_LONG).show();
            // Nếu ID không hợp lệ, không cần tiếp tục
            finish();
            return;
        }

        // Khởi tạo Retrofit Service (sử dụng Client công cộng)
        courtApiService = RetrofitClient.createPublicCourtService();

        recyclerView = findViewById(R.id.recycler_view_feedback);
        progressBar = findViewById(R.id.progress_bar);

        setupRecyclerView();
        loadFirstPage();
    }

    private void setupRecyclerView() {
        adapter = new FeedbackAdapter(new ArrayList<>());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // --- Logic Phân Trang Thủ Công ---
        recyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1; // Chuyển sang trang tiếp theo
                loadNextPage();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    private void loadFirstPage() {
        // Kiểm tra courtId đã được xử lý trong onCreate, không cần kiểm tra lại ở đây
        progressBar.setVisibility(View.VISIBLE);
        callFeedbackApi(currentPage).enqueue(new Callback<FeedbackResponse>() {
            @Override
            public void onResponse(@NonNull Call<FeedbackResponse> call, @NonNull Response<FeedbackResponse> response) {
                progressBar.setVisibility(View.GONE);
                handleResponse(response);
            }

            @Override
            public void onFailure(@NonNull Call<FeedbackResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(FeedbackActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadNextPage() {
        // courtId đã được đảm bảo hợp lệ
        adapter.addLoadingFooter();

        callFeedbackApi(currentPage).enqueue(new Callback<FeedbackResponse>() {
            @Override
            public void onResponse(@NonNull Call<FeedbackResponse> call, @NonNull Response<FeedbackResponse> response) {
                adapter.removeLoadingFooter();
                isLoading = false;
                handleResponse(response);
            }

            @Override
            public void onFailure(@NonNull Call<FeedbackResponse> call, @NonNull Throwable t) {
                adapter.removeLoadingFooter();
                isLoading = false;
                // KHẮC PHỤC: Lùi lại currentPage nếu tải trang tiếp theo thất bại.
                currentPage -= 1;
                Toast.makeText(FeedbackActivity.this, "Lỗi tải thêm feedback: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleResponse(@NonNull Response<FeedbackResponse> response) {
        // Đã sửa lỗi: Dùng response.body().isFeedbackSuccess() và response.body().getFeedbackData()
        if (response.isSuccessful() && response.body() != null && response.body().isFeedbackSuccess()) {
            PagedFeedbackData data = response.body().getFeedbackData();

            // Thêm dữ liệu mới vào adapter
            adapter.addAll(data.getContent());

            // Cập nhật trạng thái trang cuối (API page bắt đầu từ 1, totalPages là tổng số trang)
            isLastPage = data.getPage() >= data.getTotalPages();

            // Nếu chưa phải trang cuối, thêm loading footer để chuẩn bị tải tiếp
            if (!isLastPage) {
                adapter.addLoadingFooter();
            }

        } else {
            // Xử lý lỗi API (ví dụ: HTTP 404, hoặc success=false)
            String errorMessage = "Không tải được dữ liệu feedback. Mã lỗi: " + response.code();
            // Nếu đây là lần tải đầu tiên và thất bại, hiển thị thông báo rõ ràng
            if (currentPage == 1) {
                recyclerView.setVisibility(View.GONE);
                errorMessage = "Không thể tải feedback hoặc chưa có feedback.";
            }
            Toast.makeText(FeedbackActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private Call<FeedbackResponse> callFeedbackApi(int page) {
        // ⭐ Sử dụng courtId thực tế nhận từ Intent
        return courtApiService.getCourtFeedback(courtId, page, PAGE_SIZE);
    }
}