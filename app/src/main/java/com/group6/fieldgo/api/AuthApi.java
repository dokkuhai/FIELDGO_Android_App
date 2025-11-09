package com.group6.fieldgo.api;

import com.group6.fieldgo.model.ApiResponse;
import com.group6.fieldgo.model.LoginRequest;
import com.group6.fieldgo.model.UserProfileResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("api/auth/login")
    Call<ApiResponse<String>> login(@Body LoginRequest request);
    @GET("api/users/profile")
    Call<ApiResponse<UserProfileResponse>> getProfile();
}

