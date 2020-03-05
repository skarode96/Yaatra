package com.tcd.yaatra.services.api.yaatra.models;

import com.google.gson.annotations.SerializedName;

public class CreateDailyCommuteRequestBody {
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
    @SerializedName("time_of_commute")
    private String timeOfCommute;
    @SerializedName("journey_frequency")
    private int journeyFrequency;
    @SerializedName("pref_mode_travel")
    private int prefModeTravel;
    @SerializedName("pref_gender")
    private int prefGender;
    @SerializedName("user_id")
    private int userId;

    public CreateDailyCommuteRequestBody() {
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getTimeOfCommute() {
        return timeOfCommute;
    }

    public void setTimeOfCommute(String timeOfCommute) {
        this.timeOfCommute = timeOfCommute;
    }

    public int getJourneyFrequency() {
        return journeyFrequency;
    }

    public void setJourneyFrequency(int journeyFrequency) {
        this.journeyFrequency = journeyFrequency;
    }

    public int getPrefModeTravel() {
        return prefModeTravel;
    }

    public void setPrefModeTravel(int prefModeTravel) {
        this.prefModeTravel = prefModeTravel;
    }

    public int getPrefGender() {
        return prefGender;
    }

    public void setPrefGender(int prefGender) {
        this.prefGender = prefGender;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "CreateDailyCommuteRequestBody{" +
                "journeyTitle='" + journeyTitle + '\'' +
                ", sourceLong='" + sourceLong + '\'' +
                ", sourceLat='" + sourceLat + '\'' +
                ", destinationLat='" + destinationLat + '\'' +
                ", destinationLong='" + destinationLong + '\'' +
                ", startTime='" + startTime + '\'' +
                ", journeyFrequency='" + journeyFrequency + '\'' +
                ", prefModeTravel='" + prefModeTravel + '\'' +
                ", prefGender='" + prefGender + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
