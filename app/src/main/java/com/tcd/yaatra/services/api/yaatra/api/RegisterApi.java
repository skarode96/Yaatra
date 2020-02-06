package com.tcd.yaatra.services.api.yaatra.api;

import com.tcd.yaatra.services.api.yaatra.models.RegisterRequestBody;
import com.tcd.yaatra.services.api.yaatra.models.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegisterApi {
    @POST
    Call<RegisterResponse> register(@Body RegisterRequestBody registerRequestBody);
}
