package com.tcd.yaatra.services.api.yaatra.models;

import com.google.gson.annotations.SerializedName;

public class RateResponse {
    @SerializedName("message")
    private String message;
    @SerializedName("response")
    private String response;

    public RateResponse (String message, String response) {
        this.message = message;
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "RatingResponse{" +
                "message='" + message + '\'' +
                ", response='" + response + '\'' +
                '}';
    }
}
