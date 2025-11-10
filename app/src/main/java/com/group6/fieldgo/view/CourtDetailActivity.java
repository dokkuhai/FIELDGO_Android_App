// File: view/CourtDetailActivity.java
package com.group6.fieldgo.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.group6.fieldgo.R;
import com.group6.fieldgo.model.CourtDetail;
import java.text.NumberFormat;
import java.util.Locale;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.group6.fieldgo.adapter.ImageAdapter;

public class CourtDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_court_detail);

        CourtDetail court = getIntent().getParcelableExtra("COURT_DETAIL");
        if (court == null) {
            finish();
            return;
        }

        // Views
        ImageView ivAvatar = findViewById(R.id.ivAvatar);
        ImageView ivCover = findViewById(R.id.ivCover);
        TextView tvName = findViewById(R.id.tvName);
        TextView tvAddress = findViewById(R.id.tvAddress);
        TextView tvPhone = findViewById(R.id.tvPhone);
        TextView tvTime = findViewById(R.id.tvTime);
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        TextView tvDistance = findViewById(R.id.tvDistance);
        Button btnMap = findViewById(R.id.btnMap);
        RecyclerView recyclerImages = findViewById(R.id.recyclerImages);

        // Hiển thị thông tin
        tvName.setText(court.getName());
        tvAddress.setText(court.getAddress());
        tvPhone.setText("Phone: " + court.getPhone());
        tvTime.setText("Giờ mở: " + court.getOpenTime() + " - " + court.getCloseTime());
        ratingBar.setRating((float) court.getAverageRating());

        NumberFormat fmt = NumberFormat.getInstance(new Locale("vi", "VN"));
        tvDistance.setText("Khoảng cách: " + fmt.format(court.getDistance()) + " m");

        Glide.with(this).load(court.getAvatarUrl()).into(ivAvatar);
        Glide.with(this).load(court.getCoverImageUrl()).into(ivCover);

        // Google Maps
        btnMap.setOnClickListener(v -> {
            Uri uri = Uri.parse(court.getMapUrl());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        });

        Button btnBook = findViewById(R.id.btnBook);
        btnBook.setOnClickListener(v -> {
            Intent intent = new Intent(CourtDetailActivity.this, BookingActivity.class);
            intent.putExtra("COURT_ID", court.getId());
            intent.putExtra("COURT_NAME", court.getName());
            startActivity(intent); // HOẶC startActivityForResult(intent, 100) nếu cần nhận kết quả
        });

        // Danh sách ảnh
        ImageAdapter adapter = new ImageAdapter(this, court.getImages(), url -> {
            // MỞ ẢNH TO
            Intent intent = new Intent(this, FullImageActivity.class);
            intent.putExtra("IMAGE_URL", url);
            startActivity(intent);
        });

        recyclerImages.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerImages.setAdapter(adapter);
    }
}