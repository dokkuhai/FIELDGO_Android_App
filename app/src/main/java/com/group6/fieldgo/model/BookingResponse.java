package com.group6.fieldgo.model;

import com.google.gson.annotations.SerializedName;

/**
 * Phản hồi từ API /api/bookings sau khi đặt sân thành công.
 */
public class BookingResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private BookingData data;

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public BookingData getData() { return data; }

    public static class BookingData {
        @SerializedName("bookingId")
        private String bookingId; // Mã đặt chỗ thực tế từ server

        // Getter
        public String getBookingId() { return bookingId; }
    }
}