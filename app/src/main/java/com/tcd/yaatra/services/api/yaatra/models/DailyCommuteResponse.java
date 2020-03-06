package com.tcd.yaatra.services.api.yaatra.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DailyCommuteResponse {
    @SerializedName("message")
    private String message;
    @SerializedName("response")
    private String response;
    @SerializedName("journey_details")
    private List<JourneyDetails> journeyDetails;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public List<JourneyDetails> getJourneyDetails() {
        return journeyDetails;
    }

    public void setJourneyDetails(List<JourneyDetails> journeyDetails) {
        this.journeyDetails = journeyDetails;
    }

    @Override
    public String toString() {
        return "DailyCommuteResponse{" +
                "message='" + message + '\'' +
                ", response='" + response + '\'' +
                ", journeyDetails=" + journeyDetails +
                '}';
    }
}
