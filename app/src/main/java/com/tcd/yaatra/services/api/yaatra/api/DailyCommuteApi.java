package com.tcd.yaatra.services.api.yaatra.api;

import com.tcd.yaatra.services.api.yaatra.models.Journey;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface DailyCommuteApi {
    @GET("backend/daily-commutes/v1/")
    Call<List<Journey>> getDailyCommute(@Header("Authorization") String token);
}