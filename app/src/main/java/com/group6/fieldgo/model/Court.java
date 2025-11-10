package com.group6.fieldgo.model;

import com.google.gson.annotations.SerializedName;

public class Court {
    private int id;
    private String name;
    @SerializedName("pricePerHour")
    private int price;
    // ... các trường khác (averageRating, ratingCount)
    private String sportType;
    private String venueName;
    private String provinceName;
    private String wardName;
    @SerializedName("firstImageUrl")
    private String imageUrl;

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public int getPrice() { return price; }
    public String getSportType() { return sportType; }
    public String getVenueName() { return venueName; }
    public String getProvinceName() { return provinceName; }
    public String getWardName() { return wardName; }
    public String getImageUrl() { return imageUrl; }
}