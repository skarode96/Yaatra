package com.tcd.yaatra.services.api.yaatra.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class CreateDailyCommuteResponse {
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
    private Date startTime;
    @SerializedName("journey_frequency")
    private int journeyFrequency;
    @SerializedName("journey_id")
    private int journeyID;
    @SerializedName("message")
    private String message;
    @SerializedName("response")
    private String response;

    public CreateDailyCommuteResponse() {
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
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
        return "CreateDailyCommuteResponse{" +
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
}
