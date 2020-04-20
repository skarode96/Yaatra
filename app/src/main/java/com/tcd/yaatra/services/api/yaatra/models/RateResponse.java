package com.tcd.yaatra.services.api.yaatra.models;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RateResponse)) return false;
        RateResponse that = (RateResponse) o;
        return Objects.equals(message, that.message) &&
                Objects.equals(response, that.response);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, response);
    }

    @Override
    public String toString() {
        return "RatingResponse{" +
                "message='" + message + '\'' +
                ", response='" + response + '\'' +
                '}';
    }
}
