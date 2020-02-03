package com.tcd.yaatra.services.api.yaatra.models;

import com.google.gson.annotations.SerializedName;

public class Journey {
    @SerializedName("journeyId")
    private String journeyId;
    @SerializedName("title")
    private String title;
    @SerializedName("source")
    private String source;
    @SerializedName("destination")
    private String destination;

    public Journey(String journeyId, String title, String source, String destination) {
        this.journeyId = journeyId;
        this.title = title;
        this.source = source;
        this.destination = destination;
    }

    public Journey() {

    }

    public String getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(String journeyId) {
        this.journeyId = journeyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public String toString() {
        return "Journey{" +
                "journeyId='" + journeyId + '\'' +
                ", title='" + title + '\'' +
                ", source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                '}';
    }
}
