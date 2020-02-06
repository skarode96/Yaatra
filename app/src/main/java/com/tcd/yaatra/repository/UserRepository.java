package com.tcd.yaatra.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.tcd.yaatra.repository.models.AsyncData;
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


    public LiveData<AsyncData<LoginResponse>> authenticateUser(String username, String password){

        MutableLiveData<AsyncData<LoginResponse>> loginResponseLiveData = new MutableLiveData<>();

        this.loginApi.getToken(username, password).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                switch (response.code()) {
                    case 200: loginResponseLiveData.postValue(AsyncData.getSuccessState(response.body()));break;
                    case 404: loginResponseLiveData.postValue(AsyncData.getFailureState(new LoginResponse("User Not Found, Invalid Credentials!", "Error")));break;
                    default: loginResponseLiveData.postValue(AsyncData.getFailureState(new LoginResponse("Fatal Error", "Error")));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                loginResponseLiveData.postValue(AsyncData.getFailureState(null));
            }
        });

        loginResponseLiveData.postValue(AsyncData.getLoadingState());

        return loginResponseLiveData;
    }
}
