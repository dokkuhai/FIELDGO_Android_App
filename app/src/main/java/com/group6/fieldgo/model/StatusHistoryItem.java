package com.group6.fieldgo.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatusHistoryItem {
    private String title;       // "Đã xác nhận"
    private String description; // "Admin đã duyệt booking"
    private String timestamp;   // "10/11/2025 10:00"
    private String status;      // "CONFIRMED", "PENDING", "CANCELLED"
}



