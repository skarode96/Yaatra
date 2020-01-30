package com.tcd.yaatra.services.api.yaatra.models;

public class LoginResponse {

    private String message;
    private String response;
    private String authToken;

    public LoginResponse(String message, String response, String authToken) {
        this.message = message;
        this.response = response;
        this.authToken = authToken;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
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
