package com.tcd.yaatra.services.api.yaatra.api;

import com.tcd.yaatra.services.api.yaatra.models.ScheduleDailyCommuteRequestBody;
import com.tcd.yaatra.services.api.yaatra.models.ScheduleDailyCommuteResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ScheduleDailyCommuteApi {
    @POST("commute/daily/")
    Call<ScheduleDailyCommuteResponse> createDailyCommute(@Body ScheduleDailyCommuteRequestBody scheduleDailyCommuteRequestBody);
}
