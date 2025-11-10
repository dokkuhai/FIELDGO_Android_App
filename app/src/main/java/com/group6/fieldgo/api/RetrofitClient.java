package com.group6.fieldgo.api;

import com.group6.fieldgo.util.TokenManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class RetrofitClient {

    private static final String BASE_URL = "https://fieldgo.site:8443/";
    private static final OkHttpClient publicHttpClient = new OkHttpClient.Builder().build();
    public static AuthApi create(TokenManager tokenManager) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request.Builder builder = chain.request().newBuilder();
                        String token = tokenManager.getToken();
                        if (token != null) {
                            builder.addHeader("Authorization", "Bearer " + token);
                        }
                        return chain.proceed(builder.build());
                    }
                }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(AuthApi.class);
    }

    /**
     * Tạo BookingApi instance với token authentication
     */
    public static BookingApi createBookingApi(TokenManager tokenManager) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request.Builder builder = chain.request().newBuilder();
                        String token = tokenManager.getToken();
                        if (token != null) {
                            builder.addHeader("Authorization", "Bearer " + token);
                        }
                        return chain.proceed(builder.build());
                    }
                }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(BookingApi.class);
    }

    public static CourtApiService createPublicCourtService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(publicHttpClient) // Dùng client KHÔNG có token
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(CourtApiService.class);
    }

    public static BookingApiService createBookingService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(publicHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(BookingApiService.class);
    }


}

