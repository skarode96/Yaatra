package com.tcd.yaatra.services.api.yaatra.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class Rating {

    @SerializedName("id")
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    private int id;

    @SerializedName("username")
    @ColumnInfo(name="username")
    private String username;

    @SerializedName("value")
    @ColumnInfo(name="value")
    private double value;

    public Rating(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
