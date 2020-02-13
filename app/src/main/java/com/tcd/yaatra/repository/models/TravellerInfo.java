package com.tcd.yaatra.repository.models;

import java.time.LocalDateTime;

public class TravellerInfo {

    private Integer userId;
    private String userName = "";
    private int age;
    private Gender gender = Gender.NotSpecified;
    private Double sourceLatitude;
    private Double sourceLongitude;
    private Double destinationLatitude;
    private Double destinationLongitude;
    private TravellerStatus status = TravellerStatus.None;
    private LocalDateTime requestStartTime = LocalDateTime.now();
    private Double userRating;
    private String ipAddress = "";
    private int portNumber;
    private LocalDateTime statusUpdateTime = LocalDateTime.now();
    private String infoProvider = "";

    public TravellerInfo(Integer userId, String userName, int age, Gender gender
            , Double sourceLatitude, Double sourceLongitude
            , Double destinationLatitude, Double destinationLongitude
            , TravellerStatus status, LocalDateTime requestStartTime
            , Double userRating, String ipAddress, int port
            , LocalDateTime statusUpdateTime
            , String infoProvider){

        setUserId(userId);
        setUserName(userName);
        setAge(age);
        setGender(gender);
        setSourceLatitude(sourceLatitude);
        setSourceLongitude(sourceLongitude);
        setDestinationLatitude(destinationLatitude);
        setDestinationLongitude(destinationLongitude);
        setStatus(status);
        setRequestStartTime(requestStartTime);
        setUserRating(userRating);
        setIpAddress(ipAddress);
        setPortNumber(port);
        setStatusUpdateTime(statusUpdateTime);
        setInfoProvider(infoProvider);
    }

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

    //endregion
}