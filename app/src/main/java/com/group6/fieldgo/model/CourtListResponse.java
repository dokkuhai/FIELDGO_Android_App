package com.group6.fieldgo.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CourtListResponse {
    private boolean success;
    private String message;
    private Data data;

    public boolean isSuccess() { return success; }
    public Data getData() { return data; }

    public static class Data {
        private List<Court> content;
        private int page;
        private int size;
        private int totalPages;

        public List<Court> getContent() { return content; }
        public int getPage() { return page; }
        public int getTotalPages() { return totalPages; }
    }
}