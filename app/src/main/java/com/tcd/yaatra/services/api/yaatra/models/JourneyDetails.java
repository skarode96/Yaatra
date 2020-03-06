package com.tcd.yaatra.services.api.yaatra.models;

import com.google.gson.annotations.SerializedName;

public class JourneyDetails {

    @SerializedName("id")
    private int id;
    @SerializedName("user")
    private int user;
    @SerializedName("journey_title")
    private String journey_title;
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
    @SerializedName("pref_mode_travel")
    private int prefModeTravel;
    @SerializedName("pref_gender")
    private int prefGender;
    @SerializedName("journey_frequency")
    private int journeyFrequency;
    @SerializedName("journey_id")
    private int journeyId;
    @SerializedName("time_of_commute")
    private String timeOfCommute;
    @SerializedName("number_of_travellers")
    private int numberOfTravellers;

    public JourneyDetails() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public String getJourney_title() {
        return journey_title;
    }

    public void setJourney_title(String journey_title) {
        this.journey_title = journey_title;
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

    public int getJourneyFrequency() {
        return journeyFrequency;
    }

    public void setJourneyFrequency(int journeyFrequency) {
        this.journeyFrequency = journeyFrequency;
    }

    public int getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(int journeyId) {
        this.journeyId = journeyId;
    }

    public String getTimeOfCommute() {
        return timeOfCommute;
    }

    public void setTimeOfCommute(String timeOfCommute) {
        this.timeOfCommute = timeOfCommute;
    }

    public int getNumberOfTravellers() {
        return numberOfTravellers;
    }

    public void setNumberOfTravellers(int numberOfTravellers) {
        this.numberOfTravellers = numberOfTravellers;
    }

    @Override
    public String toString() {
        return "DailyCommuteResponse{" +
                "id=" + id +
                ", user=" + user +
                ", journey_title='" + journey_title + '\'' +
                ", sourceLong=" + sourceLong +
                ", sourceLat=" + sourceLat +
                ", destinationLat=" + destinationLat +
                ", destinationLong=" + destinationLong +
                ", startTime='" + startTime + '\'' +
                ", prefModeTravel=" + prefModeTravel +
                ", prefGender=" + prefGender +
                ", journeyFrequency=" + journeyFrequency +
                ", journeyId=" + journeyId +
                ", timeOfCommute='" + timeOfCommute + '\'' +
                ", numberOfTravellers=" + numberOfTravellers +
                '}';
    }
}
