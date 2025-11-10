package com.group6.fieldgo.api;

import com.group6.fieldgo.model.CourtListResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CourtApiService {
    // Endpoint để lấy danh sách sân có phân trang
    // {BASE_URL}/courts?page=0&size=10
    @GET("api/search/courts")
    Call<CourtListResponse> getCourts(
    );
}