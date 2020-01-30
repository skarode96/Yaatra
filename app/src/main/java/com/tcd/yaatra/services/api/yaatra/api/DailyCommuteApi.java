package com.tcd.yaatra.services.api.yaatra.api;

import com.tcd.yaatra.services.api.yaatra.models.DailyCommuteResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface DailyCommuteApi {
    @GET("backend/daily-commutes/v1/")
    Call<DailyCommuteResponse> getDailyCommute(@Header("Authorization") String token);
}