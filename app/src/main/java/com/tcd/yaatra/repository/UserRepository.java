package com.tcd.yaatra.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.tcd.yaatra.services.api.yaatra.api.LoginApi;
import com.tcd.yaatra.services.api.yaatra.models.LoginResponse;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private LoginApi loginApi;

    @Inject
    public UserRepository(LoginApi loginApi){
        this.loginApi = loginApi;
    }


    public LiveData<LoginResponse> authenticateUser(String username, String password){
        MutableLiveData<LoginResponse> loginResponseLiveData = new MutableLiveData<>();

        this.loginApi.getToken(username, password).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                loginResponseLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

            }
        });


        return loginResponseLiveData;
    }
}
