package com.example.loginjourneysharing.activities;

public class User {
    private int id;
    private String token_id;
    private String userName;


    public User() {
    }

    public User(int id, String token_id) {
        this.id = id;
        this.token_id = token_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTokenId() {
        return token_id;
    }

    public void setTokenId(String token_id) {
        this.token_id = token_id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
