package com.group6.fieldgo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long bookingId;
    private Long courtId;
    private String courtName;
    private String address;
    private String bookingDate;  // "2025-11-10"
    private String timeslot;     // "10:00 - 12:00" - Lowercase để khớp backend
    private String status;       // "PENDING", "CONFIRMED", "CANCELLED"
    private Double price;
}