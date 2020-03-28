package com.tcd.yaatra.services.api.yaatra.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

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

    public DailyCommuteResponse(String message, String response) {
        this.message = message;
        this.response = response;
    }

    @Override
    public String toString() {
        return "DailyCommuteResponse{" +
                "message='" + message + '\'' +
                ", response='" + response + '\'' +
                ", journeyDetails=" + journeyDetails +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DailyCommuteResponse)) return false;
        DailyCommuteResponse that = (DailyCommuteResponse) o;
        return Objects.equals(message, that.message) &&
                Objects.equals(response, that.response) &&
                Objects.equals(journeyDetails, that.journeyDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, response, journeyDetails);
    }
}
