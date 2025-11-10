package com.group6.fieldgo.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;  // CHỈ IMPORT PhotoView
import com.group6.fieldgo.R;

public class FullImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        // LẤY URL TỪ INTENT
        String url = getIntent().getStringExtra("IMAGE_URL");
        if (url == null) {
            finish(); // ĐÓNG NẾU KHÔNG CÓ URL
            return;
        }

        // FIND PhotoView TRỰC TIẾP (KHÔNG CẦN ImageView HOẶC ATTACHER)
        PhotoView ivFull = findViewById(R.id.ivFull);

        // LOAD ẢNH BẰNG GLIDE
        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.ic_placeholder)  // TÙY CHỌN: Ảnh chờ
                .error(R.drawable.anhsan)  // TÙY CHỌN: Ảnh lỗi
                .into(ivFull);

        // PhotoView TỰ ĐỘNG ZOOM (KHÔNG CẦN ATTACHER)
    }
}