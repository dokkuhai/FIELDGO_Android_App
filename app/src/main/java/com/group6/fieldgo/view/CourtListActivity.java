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
import com.group6.fieldgo.adapter.CourtAdapter;
import com.group6.fieldgo.api.RetrofitClient;
import com.group6.fieldgo.model.Court;
import com.group6.fieldgo.model.CourtListResponse;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourtListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private CourtAdapter adapter;
    private List<Court> courtList = new ArrayList<>();
    private LinearLayoutManager layoutManager;

    // Logic Phân trang
    private int currentPage = 0;
    private int totalPages = 1;
    private final int pageSize = 10;
    private boolean isLoading = false;
    private final int VISIBLE_THRESHOLD = 5; // Tải khi còn 5 item nữa là đến cuối

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_court_list);

        // Ánh xạ View
        recyclerView = findViewById(R.id.recyclerViewCourts);
        progressBar = findViewById(R.id.progressBar);

        setupRecyclerView();
        loadCourts();
    }

    private void setupRecyclerView() {
        adapter = new CourtAdapter(this, courtList);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // ⭐ Tối ưu hóa logic phân trang
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                // Điều kiện tải trang:
                // 1. Không đang tải (isLoading == false)
                // 2. Vẫn còn trang để tải (currentPage < totalPages)
                // 3. Vị trí item nhìn thấy cuối cùng + ngưỡng tải (VISIBLE_THRESHOLD) >= Tổng số item
                if (!isLoading && currentPage < totalPages) {
                    if (lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount) {
                        loadCourts(); // Tải trang tiếp theo
                    }
                }
            }
        });
    }

    private void loadCourts() {
        if (isLoading) return;
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);

        // Gọi API công khai (không cần token)
        RetrofitClient.createPublicCourtService()
                .getCourts()
                .enqueue(new Callback<CourtListResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<CourtListResponse> call, @NonNull Response<CourtListResponse> response) {
                        isLoading = false;
                        progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            CourtListResponse.Data data = response.body().getData();
                            if (data != null && data.getContent() != null) {

                                // Cập nhật dữ liệu vào Adapter
                                adapter.addCourts(data.getContent());

                                // Cập nhật trạng thái phân trang
                                currentPage = data.getPage() + 1; // API thường trả về page index 0, nên page tiếp theo là page + 1
                                totalPages = data.getTotalPages();
                            }
                        } else {
                            Toast.makeText(CourtListActivity.this, "Lỗi: Không thể tải dữ liệu sân.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<CourtListResponse> call, @NonNull Throwable t) {
                        isLoading = false;
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(CourtListActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}