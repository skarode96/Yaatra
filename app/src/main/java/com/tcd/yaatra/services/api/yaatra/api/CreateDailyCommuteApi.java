package com.tcd.yaatra.services.api.yaatra.api;

import com.tcd.yaatra.services.api.yaatra.models.CreateDailyCommuteRequestBody;
import com.tcd.yaatra.services.api.yaatra.models.CreateDailyCommuteResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CreateDailyCommuteApi {
    @POST("commute/daily/")
    Call<CreateDailyCommuteResponse> createDailyCommute(@Body CreateDailyCommuteRequestBody createDailyCommuteRequestBody);
}
