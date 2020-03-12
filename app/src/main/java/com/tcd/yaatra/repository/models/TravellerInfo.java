package com.tcd.yaatra.repository.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TravellerInfo implements Serializable {

    private Integer userId;
    private String userName = "";
    private int age;
    private Gender gender = Gender.NOT_SPECIFIED;
    private Double sourceLatitude;
    private Double sourceLongitude;
    private Double destinationLatitude;
    private Double destinationLongitude;
    private String modeOfTravel;
    private TravellerStatus status = TravellerStatus.None;
    private String sourceName;

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    private String destinationName;
    private LocalDateTime requestStartTime = LocalDateTime.now();
    private Double userRating;
    private String ipAddress = "";
    private int portNumber;
    private LocalDateTime statusUpdateTime = LocalDateTime.now();
    private String infoProvider = "";

    @Inject
    public TravellerInfo(){}

    //region Getters & Setters

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Double getSourceLatitude() {
        return sourceLatitude;
    }

    public void setSourceLatitude(Double sourceLatitude) {
        this.sourceLatitude = sourceLatitude;
    }

    public Double getSourceLongitude() {
        return sourceLongitude;
    }

    public void setSourceLongitude(Double sourceLongitude) {
        this.sourceLongitude = sourceLongitude;
    }

    public Double getDestinationLatitude() {
        return destinationLatitude;
    }

    public void setDestinationLatitude(Double destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }

    public Double getDestinationLongitude() {
        return destinationLongitude;
    }

    public void setDestinationLongitude(Double destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }

    public TravellerStatus getStatus() {
        return status;
    }

    public void setStatus(TravellerStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestStartTime() {
        return requestStartTime;
    }

    public void setRequestStartTime(LocalDateTime requestStartTime) {
        this.requestStartTime = requestStartTime;
    }

    public Double getUserRating() {
        return userRating;
    }

    public void setUserRating(Double userRating) {
        this.userRating = userRating;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public LocalDateTime getStatusUpdateTime() {
        return statusUpdateTime;
    }

    public void setStatusUpdateTime(LocalDateTime statusUpdateTime) {
        this.statusUpdateTime = statusUpdateTime;
    }

    public String getInfoProvider() { return infoProvider; }

    public void setInfoProvider(String infoProvider){
        this.infoProvider = infoProvider;
    }


    public String getModeOfTravel() {
        return modeOfTravel;
    }

    public void setModeOfTravel(String modeOfTravel) {
        this.modeOfTravel = modeOfTravel;
    }
    //endregion
}