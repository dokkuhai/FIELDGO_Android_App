package com.group6.fieldgo.model;
import com.google.gson.annotations.SerializedName;

/**
 * Dữ liệu gửi lên API /api/bookings để đặt sân.
 */
public class BookingRequest {
    @SerializedName("courtId")
    private int courtId;

    @SerializedName("slotId")
    private int slotId; // ID của khung giờ

    @SerializedName("bookingDate")
    private String bookingDate; // Ngày đặt sân (Ví dụ: "2025-11-10")

    public BookingRequest(int courtId, int slotId, String bookingDate) {
        this.courtId = courtId;
        this.slotId = slotId;
        this.bookingDate = bookingDate;
    }
}
