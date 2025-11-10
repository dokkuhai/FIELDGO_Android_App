package com.group6.fieldgo.model;

import com.google.gson.annotations.SerializedName;

// FeedbackItem.java
public class FeedbackItem {
    @SerializedName("userName")
    private String userName;
    @SerializedName("rating")
    private int rating;
    private String comment;
    private String createdAt;

    // Getters
    public String getUserName() { return userName; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public String getCreatedAt() { return createdAt; }
}