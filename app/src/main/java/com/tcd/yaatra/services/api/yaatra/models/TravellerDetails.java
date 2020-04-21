package com.tcd.yaatra.services.api.yaatra.models;

import com.google.gson.annotations.SerializedName;

public class TravellerDetails {
    @SerializedName("id")
    private int id;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("email")
    private String email;
    @SerializedName("gender")
    private String gender;
    @SerializedName("age")
    private int age;
    @SerializedName("username")
    private String username;
    @SerializedName("created_on")
    private String createdOn;
    @SerializedName("last_login")
    private String lastLogin;
    @SerializedName("pref_mode_travel")
    private int prefModeTravel;
    @SerializedName("pref_gender")
    private int prefGender;
    @SerializedName("rating")
    private double rating;
    @SerializedName("country")
    private String country;
    @SerializedName("phone_number")
    private long phoneNumber;
    @SerializedName("source_long")
    private double sourceLong;
    @SerializedName("source_lat")
    private double sourceLat;
    @SerializedName("destination_lat")
    private double destinationLat;
    @SerializedName("destination_long")
    private double destinationLong;
    @SerializedName("time_of_commute")
    private String timeOfCommute;
    @SerializedName("commute_start_date")
    private String commuteStartDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
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

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getTimeOfCommute() {
        return timeOfCommute;
    }

    public void setTimeOfCommute(String timeOfCommute) {
        this.timeOfCommute = timeOfCommute;
    }

    public String getCommuteStartDate() {
        return commuteStartDate;
    }

    public void setCommuteStartDate(String commuteStartDate) {
        this.commuteStartDate = commuteStartDate;
    }

    @Override
    public String toString() {
        return "TravellerDetails{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                ", username='" + username + '\'' +
                ", createdOn='" + createdOn + '\'' +
                ", lastLogin='" + lastLogin + '\'' +
                ", prefModeTravel=" + prefModeTravel +
                ", prefGender=" + prefGender +
                ", rating=" + rating +
                ", country='" + country + '\'' +
                ", phoneNumber=" + phoneNumber +
                ", sourceLong=" + sourceLong +
                ", sourceLat=" + sourceLat +
                ", destinationLat=" + destinationLat +
                ", destinationLong=" + destinationLong +
                ", timeOfCommute='" + timeOfCommute + '\'' +
                ", commuteStartDate='" + commuteStartDate + '\'' +
                '}';
    }
}
