package com.tcd.yaatra.services.api.yaatra.models;

import com.google.gson.annotations.SerializedName;

public class RateRequestBody {
    @SerializedName("username")
    private String userName;
    @SerializedName("rating")
    private double rating;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "RateRequestBody{" +
                "userName='" + userName + '\'' +
                ", rating='" + rating + '\'' +
                '}';
    }
}
