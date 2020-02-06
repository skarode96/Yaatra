package com.tcd.yaatra.services.api.yaatra.models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("message")
    private String message;
    @SerializedName("response")
    private String response;
    @SerializedName("authToken")
    private String authToken;


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


    @Override
    public String toString() {
        return "LoginResponse{" +
                "token='" + message + '\'' +
                ", response='" + response + '\'' +
                ", authToken='" + authToken + '\'' +
                '}';
    }
}
