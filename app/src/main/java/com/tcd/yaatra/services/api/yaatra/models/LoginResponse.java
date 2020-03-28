package com.tcd.yaatra.services.api.yaatra.models;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class LoginResponse {

    @SerializedName("message")
    private String message;
    @SerializedName("response")
    private String response;
    @SerializedName("authToken")
    private String authToken;
    @SerializedName("userInfo")
    private UserInfo userInfo;


    public LoginResponse(String message, String response) {
        this.message = message;
        this.response = response;
    }

    public String getResponse() {
        return response;
    }


    public void setResponse(String response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "message='" + message + '\'' +
                ", response='" + response + '\'' +
                ", authToken='" + authToken + '\'' +
                ", userInfo=" + userInfo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoginResponse)) return false;
        LoginResponse that = (LoginResponse) o;
        return Objects.equals(message, that.message) &&
                Objects.equals(response, that.response) &&
                Objects.equals(authToken, that.authToken) &&
                Objects.equals(userInfo, that.userInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, response, authToken, userInfo);
    }
}
