package com.group6.fieldgo.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.group6.fieldgo.MainActivity;
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
import com.group6.fieldgo.model.CourtDetailResponse;
import com.group6.fieldgo.model.CourtDetail;
import android.util.Log;

public class CourtListActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private CourtAdapter adapter;
    private List<Court> courtList = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private MaterialToolbar toolbar;

    // Logic Phân trang
    private int currentPage = 0;
    private int totalPages = 1;
    private final int pageSize = 10;
    private boolean isLoading = false;
    private final int VISIBLE_THRESHOLD = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_court_list);

        // Ánh xạ View
        recyclerView = findViewById(R.id.recyclerViewCourts);
        progressBar = findViewById(R.id.progressBar);
        toolbar = findViewById(R.id.toolbar);

        setupToolbar();
        setupRecyclerView();
        loadCourts();

        adapter.setOnCourtClickListener(court -> {
            double userLat = 1.0;
            double userLng = 1.0;

            Log.d("COURT_CLICK", "Bấm vào sân ID: " + court.getId());

            RetrofitClient.createPublicCourtService()
                    .getCourtDetail(court.getId(), userLat, userLng)
                    .enqueue(new Callback<CourtDetailResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<CourtDetailResponse> call, @NonNull Response<CourtDetailResponse> response) {
                            Log.d("API_RESPONSE", "URL: " + call.request().url());
                            Log.d("API_RESPONSE", "HTTP Code: " + response.code());

                            if (response.isSuccessful()) {
                                Log.d("API_SUCCESS", "Body: " + response.body());
                                if (response.body() != null && response.body().isSuccess()) {
                                    CourtDetail detail = response.body().getData();
                                    Log.d("API_DATA", "Tên sân: " + detail.getName());

                                    Intent intent = new Intent(CourtListActivity.this, CourtDetailActivity.class);
                                    intent.putExtra("COURT_DETAIL", detail);
                                    startActivity(intent);
                                } else {
                                    String msg = response.body() != null ? response.body().getMessage() : "Unknown error";
                                    Log.e("API_ERROR", "Success = false: " + msg);
                                    Toast.makeText(CourtListActivity.this, "Lỗi: " + msg, Toast.LENGTH_LONG).show();
                                }
                            } else {
                                String errorBody = "No error body";
                                try {
                                    if (response.errorBody() != null) {
                                        errorBody = response.errorBody().string();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Log.e("API_ERROR", "HTTP " + response.code() + ": " + errorBody);
                                Toast.makeText(CourtListActivity.this, "Lỗi server: " + response.code(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<CourtDetailResponse> call, @NonNull Throwable t) {
                            Log.e("API_FAILURE", "Lỗi kết nối: " + t.getMessage());
                            t.printStackTrace();
                            Toast.makeText(CourtListActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    /**
     * Khôi phục phương pháp chuẩn: Thiết lập Toolbar làm Action Bar chính
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.title_court_list));
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        // ⭐ ĐÃ XÓA inflateMenu VÀ setOnMenuItemClickListener khỏi đây
    }

    /**
     * ⭐ PHƯƠNG THỨC CHUẨN ĐỂ HIỂN THỊ MENU
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Tham chiếu đến R.menu.court_list_menu (CẦN PHẢI NẰM TRONG res/menu/)
        getMenuInflater().inflate(R.menu.court_list_menu, menu);
        return true;
    }

    /**
     * ⭐ PHƯƠNG THỨC CHUẨN ĐỂ XỬ LÝ CLICK MENU
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        } else if (itemId == R.id.menu_my_bookings) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        } else if (itemId == R.id.menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (itemId == R.id.menu_about_us) {
            startActivity(new Intent(this, AboutUsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView() {
        adapter = new CourtAdapter(this, courtList);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                if (!isLoading && currentPage < totalPages) {
                    if (lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount) {
                        loadCourts();
                    }
                }
            }
        });
    }

    private void loadCourts() {
        if (isLoading) return;
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);

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

                                adapter.addCourts(data.getContent());

                                currentPage = data.getPage() + 1;
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