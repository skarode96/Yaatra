package com.tcd.yaatra.services.api.yaatra.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.Objects;

public class ScheduleDailyCommuteResponse {
    @SerializedName("journey_title")
    private String journeyTitle;
    @SerializedName("source_long")
    private double sourceLong;
    @SerializedName("source_lat")
    private double sourceLat;
    @SerializedName("destination_lat")
    private double destinationLat;
    @SerializedName("destination_long")
    private double destinationLong;
    @SerializedName("start_time")
    private String startTime;
    @SerializedName("journey_frequency")
    private int journeyFrequency;
    @SerializedName("journey_id")
    private int journeyID;
    @SerializedName("message")
    private String message;
    @SerializedName("response")
    private String response;

    public ScheduleDailyCommuteResponse() {
    }

    public ScheduleDailyCommuteResponse(String message, String response) {
        this.message = message;
        this.response = response;
    }

    public String getJourneyTitle() {
        return journeyTitle;
    }

    public void setJourneyTitle(String journeyTitle) {
        this.journeyTitle = journeyTitle;
    }

    public double getSourceLong() {
        return sourceLong;
    }

    public void setSourceLong(double sourceLong) {
        this.sourceLong = sourceLong;
    }

    public double getSourceLat() {
        return sourceLat;
    }

    public void setSourceLat(double sourceLat) {
        this.sourceLat = sourceLat;
    }

    public double getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(double destinationLat) {
        this.destinationLat = destinationLat;
    }

    public double getDestinationLong() {
        return destinationLong;
    }

    public void setDestinationLong(double destinationLong) {
        this.destinationLong = destinationLong;
    }

    public int getJourneyID() {
        return journeyID;
    }

    public void setJourneyID(int journeyID) {
        this.journeyID = journeyID;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getJourneyFrequency() {
        return journeyFrequency;
    }

    public void setJourneyFrequency(int journeyFrequency) {
        this.journeyFrequency = journeyFrequency;
    }

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


    @Override
    public String toString() {
        return "ScheduleDailyCommuteResponse{" +
                "journeyTitle='" + journeyTitle + '\'' +
                ", sourceLong=" + sourceLong +
                ", sourceLat=" + sourceLat +
                ", destinationLat=" + destinationLat +
                ", destinationLong=" + destinationLong +
                ", startTime='" + startTime + '\'' +
                ", journeyFrequency=" + journeyFrequency +
                ", journeyID=" + journeyID +
                ", message='" + message + '\'' +
                ", response='" + response + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScheduleDailyCommuteResponse)) return false;
        ScheduleDailyCommuteResponse that = (ScheduleDailyCommuteResponse) o;
        return Double.compare(that.sourceLong, sourceLong) == 0 &&
                Double.compare(that.sourceLat, sourceLat) == 0 &&
                Double.compare(that.destinationLat, destinationLat) == 0 &&
                Double.compare(that.destinationLong, destinationLong) == 0 &&
                journeyFrequency == that.journeyFrequency &&
                journeyID == that.journeyID &&
                Objects.equals(journeyTitle, that.journeyTitle) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(message, that.message) &&
                Objects.equals(response, that.response);
    }

    @Override
    public int hashCode() {
        return Objects.hash(journeyTitle, sourceLong, sourceLat, destinationLat, destinationLong, startTime, journeyFrequency, journeyID, message, response);
    }
}
