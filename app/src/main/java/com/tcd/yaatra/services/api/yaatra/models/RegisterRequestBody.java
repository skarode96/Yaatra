package com.tcd.yaatra.services.api.yaatra.models;

import com.google.gson.annotations.SerializedName;

public class RegisterRequestBody {
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("username")
    private String userName;
    @SerializedName("password")
    private String password;
    @SerializedName("confirm_password")
    private String confirmPassword;
    @SerializedName("email")
    private String email;
    @SerializedName("age")
    private int age;
    @SerializedName("gender")
    private String gender;
    @SerializedName("pref_gender")
    private int prefGender;
    @SerializedName("pref_mode_travel")
    private int prefMode;

    @SerializedName("country")
    private String country;
    @SerializedName("phone_number")
    private long phoneNumber;

    public RegisterRequestBody() {
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getPrefGender() {
        return prefGender;
    }

    public void setPrefGender(int prefGender) {
        this.prefGender = prefGender;
    }

    public int getPrefMode() {
        return prefMode;
    }

    public void setPrefMode(int prefMode) {
        this.prefMode = prefMode;
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

    @Override
    public String toString() {
        return "RegisterRequestBody{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", prefGender='" + prefGender + '\'' +
                ", prefMode='" + prefMode + '\'' +
                ", country='" + country + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
