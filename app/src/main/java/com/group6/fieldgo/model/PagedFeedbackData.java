package com.group6.fieldgo.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

// PagedFeedbackData.java
public class PagedFeedbackData {
    @SerializedName("content")
    private List<FeedbackItem> content;
    private int page;
    private int size;
    private int totalPages;

    // Getters
    public List<FeedbackItem> getContent() { return content; }
    public int getPage() { return page; }
    public int getTotalPages() { return totalPages; }
}
