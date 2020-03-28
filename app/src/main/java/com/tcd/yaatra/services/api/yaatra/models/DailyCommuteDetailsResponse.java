package com.tcd.yaatra.services.api.yaatra.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class DailyCommuteDetailsResponse {
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
    @SerializedName("traveller_info")
    private List<TravellerDetails> travellerDetails;
    @SerializedName("message")
    private String message;
    @SerializedName("response")
    private String response;

    public DailyCommuteDetailsResponse(String message, String response) {
        this.message = message;
        this.response = response;
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

    public List<TravellerDetails> getTravellerDetails() {
        return travellerDetails;
    }

    public void setTravellerDetails(List<TravellerDetails> travellerDetails) {
        this.travellerDetails = travellerDetails;
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
        return "DailyCommuteDetailsResponse{" +
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
                ", travellerDetails=" + travellerDetails +
                ", message='" + message + '\'' +
                ", response='" + response + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DailyCommuteDetailsResponse)) return false;
        DailyCommuteDetailsResponse that = (DailyCommuteDetailsResponse) o;
        return id == that.id &&
                user == that.user &&
                Double.compare(that.sourceLong, sourceLong) == 0 &&
                Double.compare(that.sourceLat, sourceLat) == 0 &&
                Double.compare(that.destinationLat, destinationLat) == 0 &&
                Double.compare(that.destinationLong, destinationLong) == 0 &&
                prefModeTravel == that.prefModeTravel &&
                prefGender == that.prefGender &&
                journeyFrequency == that.journeyFrequency &&
                journeyId == that.journeyId &&
                Objects.equals(journey_title, that.journey_title) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(timeOfCommute, that.timeOfCommute) &&
                Objects.equals(travellerDetails, that.travellerDetails) &&
                Objects.equals(message, that.message) &&
                Objects.equals(response, that.response);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, journey_title, sourceLong, sourceLat, destinationLat, destinationLong, startTime, prefModeTravel, prefGender, journeyFrequency, journeyId, timeOfCommute, travellerDetails, message, response);
    }
}
