package com.group6.fieldgo.model;

import com.google.gson.annotations.SerializedName;

// FeedbackResponse.java
public class FeedbackResponse {
    @SerializedName("success")
    private boolean success;
    private String message;
    @SerializedName("data")
    private PagedFeedbackData data;

    // Getters
    public PagedFeedbackData getFeedbackData() { return data; } // <-- Phương thức getter cho 'data'
    public boolean isFeedbackSuccess() { return success; }     // <-- Phương thức isSuccess()
}
