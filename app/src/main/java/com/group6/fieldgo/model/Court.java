package com.group6.fieldgo.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class Court implements Parcelable {
    private int id;
    private String name;
    @SerializedName("pricePerHour")
    private int price;
    private String sportType;
    private String venueName;
    private String provinceName;
    private String wardName;
    @SerializedName("firstImageUrl")
    private String imageUrl;

    // Constructor rỗng (cần cho Gson)
    public Court() {}

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public int getPrice() { return price; }
    public String getSportType() { return sportType; }
    public String getVenueName() { return venueName; }
    public String getProvinceName() { return provinceName; }
    public String getWardName() { return wardName; }
    public String getImageUrl() { return imageUrl; }

    // === PARCELABLE ===
    protected Court(Parcel in) {
        id = in.readInt();
        name = in.readString();
        price = in.readInt();
        sportType = in.readString();
        venueName = in.readString();
        provinceName = in.readString();
        wardName = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<Court> CREATOR = new Creator<Court>() {
        @Override
        public Court createFromParcel(Parcel in) {
            return new Court(in);
        }

        @Override
        public Court[] newArray(int size) {
            return new Court[size];
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
        dest.writeInt(price);
        dest.writeString(sportType);
        dest.writeString(venueName);
        dest.writeString(provinceName);
        dest.writeString(wardName);
        dest.writeString(imageUrl);
    }
}