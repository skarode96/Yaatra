package com.tcd.yaatra.services.api.yaatra.api;

import com.tcd.yaatra.services.api.yaatra.models.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginApi {

    @POST("backend/login/v1/")
    @FormUrlEncoded
    Call<LoginResponse> getToken(@Field("username") String username, @Field("password") String password);
}
