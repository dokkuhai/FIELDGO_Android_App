// File: model/CourtDetail.java
package com.group6.fieldgo.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class CourtDetail implements Parcelable {
    private int id;
    private String name;
    private String address;
    private String phone;
    private String openTime;
    private String closeTime;

    @SerializedName("avatarUrl")
    private String avatarUrl;

    @SerializedName("coverImageUrl")
    private String coverImageUrl;

    private double averageRating;
    private List<String> images;
    private String mapUrl;
    private double distance;

    // === CONSTRUCTOR RỖNG CHO GSON ===
    public CourtDetail() {}

    // === GETTERS ===
    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getOpenTime() { return openTime; }
    public String getCloseTime() { return closeTime; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getCoverImageUrl() { return coverImageUrl; }
    public double getAverageRating() { return averageRating; }
    public List<String> getImages() { return images; }
    public String getMapUrl() { return mapUrl; }
    public double getDistance() { return distance; }

    // === PARCELABLE ===
    protected CourtDetail(Parcel in) {
        id = in.readInt();
        name = in.readString();
        address = in.readString();
        phone = in.readString();
        openTime = in.readString();
        closeTime = in.readString();
        avatarUrl = in.readString();
        coverImageUrl = in.readString();
        averageRating = in.readDouble();
        images = in.createStringArrayList();
        mapUrl = in.readString();
        distance = in.readDouble();
    }

    public static final Creator<CourtDetail> CREATOR = new Creator<CourtDetail>() {
        @Override
        public CourtDetail createFromParcel(Parcel in) {
            return new CourtDetail(in);
        }

        @Override
        public CourtDetail[] newArray(int size) {
            return new CourtDetail[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(phone);
        dest.writeString(openTime);
        dest.writeString(closeTime);
        dest.writeString(avatarUrl);
        dest.writeString(coverImageUrl);
        dest.writeDouble(averageRating);
        dest.writeStringList(images);
        dest.writeString(mapUrl);
        dest.writeDouble(distance);
    }
}