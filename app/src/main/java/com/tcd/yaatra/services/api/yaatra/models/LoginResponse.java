package com.tcd.yaatra.services.api.yaatra.models;

public class LoginResponse {

    private String message;
    private String response;
    private String auth_token;

    public LoginResponse(String message, String response, String auth_token) {
        this.message = message;
        this.response = response;
        this.auth_token = auth_token;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getAuthToken() {
        return auth_token;
    }

    public void setAuthToken(String auth_token) {
        this.auth_token = auth_token;
    }


    @Override
    public String toString() {
        return "LoginResponse{" +
                "token='" + message + '\'' +
                ", response='" + response + '\'' +
                ", auth_token='" + auth_token + '\'' +
                '}';
    }
}
