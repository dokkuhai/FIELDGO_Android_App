package com.group6.fieldgo.api;

import com.group6.fieldgo.model.ApiResponse;
import com.group6.fieldgo.model.Booking;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface BookingApi {

    /**
     * Lấy danh sách booking của user hiện tại
     * Endpoint: GET /api/bookings/my-bookings
     * Cần Authorization header
     */
    @GET("api/bookings/my-bookings")
    Call<ApiResponse<List<Booking>>> getMyBookings();

    /**
     * Hủy booking (cập nhật status)
     * Endpoint: PUT /api/bookings
     * Request body: { "bookingId": 0 }
     */
    @PUT("api/bookings")
    Call<ApiResponse<Void>> cancelBooking(@Body CancelBookingRequest request);

    /**
     * Request body cho việc cancel booking
     */
    class CancelBookingRequest {
        private Long bookingId;

        public CancelBookingRequest(Long bookingId) {
            this.bookingId = bookingId;
        }

        public Long getBookingId() {
            return bookingId;
        }
    }
}



