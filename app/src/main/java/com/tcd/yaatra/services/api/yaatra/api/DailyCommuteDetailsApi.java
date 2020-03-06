package com.tcd.yaatra.services.api.yaatra.api;

import com.tcd.yaatra.services.api.yaatra.models.DailyCommuteDetailsResponse;
import com.tcd.yaatra.services.api.yaatra.models.DailyCommuteResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface DailyCommuteDetailsApi {

    @POST("commute/daily/details/")
    @FormUrlEncoded
    Call<DailyCommuteDetailsResponse> getDailyCommuteDetails(@Field("journey_id") int journeyId);
}
