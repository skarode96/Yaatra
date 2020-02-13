package com.tcd.yaatra.services.api.yaatra.models;

import com.google.gson.annotations.SerializedName;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UserInfo {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    private int id;
    @SerializedName("first_name")
    @ColumnInfo(name="firstName")
    private String firstName;
    @SerializedName("last_name")
    @ColumnInfo(name="lastName")
    private String lastName;
    @SerializedName("email")
    @ColumnInfo(name="email")
    private String email;
    @SerializedName("gender")
    @ColumnInfo(name="gender")
    private String gender;
    @SerializedName("age")
    @ColumnInfo(name="age")
    private int age;
    @SerializedName("username")
    @ColumnInfo(name="username")
    private String username;
    @SerializedName("created_on")
    @ColumnInfo(name="createdOn")
    private String created_on;
    @SerializedName("last_login")
    @ColumnInfo(name="lastLogin")
    private String last_login;
    @SerializedName("pref_mode_travel")
    @ColumnInfo(name="prefModeTravel")
    private int pref_mode_travel;
    @SerializedName("pref_gender")
    @ColumnInfo(name="prefGender")
    private int pref_gender;
    @SerializedName("rating")
    @ColumnInfo(name="rating")
    private double rating;

    public UserInfo(String firstName, String lastName, String email, String gender, int age, String username, String created_on, String last_login, int pref_mode_travel, int pref_gender, double rating) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
        this.age = age;
        this.username = username;
        this.created_on = created_on;
        this.last_login = last_login;
        this.pref_mode_travel = pref_mode_travel;
        this.pref_gender = pref_gender;
        this.rating = rating;
    }

    public UserInfo() {
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

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getLast_login() {
        return last_login;
    }

    public void setLast_login(String last_login) {
        this.last_login = last_login;
    }

    public int getPref_mode_travel() {
        return pref_mode_travel;
    }

    public void setPref_mode_travel(int pref_mode_travel) {
        this.pref_mode_travel = pref_mode_travel;
    }

    public int getPref_gender() {
        return pref_gender;
    }

    public void setPref_gender(int pref_gender) {
        this.pref_gender = pref_gender;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
