package com.tcd.yaatra.services.api.yaatra.api;

import com.tcd.yaatra.services.api.yaatra.models.DailyCommuteResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.POST;

public interface DailyCommuteApi {
    @POST("commute/daily/schedules/")
    Call<DailyCommuteResponse> getDailyCommute();
}