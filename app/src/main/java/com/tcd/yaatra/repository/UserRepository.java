package com.tcd.yaatra.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.tcd.yaatra.repository.models.AsyncData;
import com.tcd.yaatra.services.api.yaatra.api.LoginApi;
import com.tcd.yaatra.services.api.yaatra.api.RegisterApi;
import com.tcd.yaatra.services.api.yaatra.models.LoginResponse;
import com.tcd.yaatra.services.api.yaatra.models.RegisterRequestBody;
import com.tcd.yaatra.services.api.yaatra.models.RegisterResponse;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private LoginApi loginApi;
    private RegisterApi registerApi;
    protected MutableLiveData<AsyncData<LoginResponse>> loginResponseLiveData;
    protected MutableLiveData<AsyncData<RegisterResponse>> registerResponseLiveData;

    @Inject
    public UserRepository(LoginApi loginApi, RegisterApi registerApi){
        this.loginApi = loginApi;
        this.registerApi = registerApi;
    }


    public LiveData<AsyncData<LoginResponse>> authenticateUser(String username, String password){

        this.loginResponseLiveData = new MutableLiveData<>();

        loginResponseLiveData.postValue(AsyncData.getLoadingState());

        this.loginApi.getToken(username, password).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                switch (response.code()) {
                    case 200: loginResponseLiveData.postValue(AsyncData.getSuccessState(response.body()));break;
                    case 401: loginResponseLiveData.postValue(AsyncData.getFailureState(new LoginResponse("User not Found! Please provide correct username and password", "Error")));break;
                    case 400: loginResponseLiveData.postValue(AsyncData.getFailureState(new LoginResponse("Use post request", "Error")));break;
                    default: loginResponseLiveData.postValue(AsyncData.getFailureState(new LoginResponse("Fatal Error", "Error")));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                loginResponseLiveData.postValue(AsyncData.getFailureState(new LoginResponse("Fatal Error", "Error")));
            }
        });

        return loginResponseLiveData;
    }


    public LiveData<AsyncData<RegisterResponse>> registerUser(RegisterRequestBody registerRequestBody){

        this.registerResponseLiveData =  new MutableLiveData<>();
        registerResponseLiveData.postValue(AsyncData.getLoadingState());

        this.registerApi.register(registerRequestBody).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                switch (response.code()) {
                    case 201: registerResponseLiveData.postValue(AsyncData.getSuccessState(response.body()));break;
                    case 400: registerResponseLiveData.postValue(AsyncData.getFailureState(new RegisterResponse("Incorrect data, Either email exists or username exists or password did not match", "Error")));break;
                    default: registerResponseLiveData.postValue(AsyncData.getFailureState(new RegisterResponse("Fatal Error", "Error")));
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                registerResponseLiveData.postValue(AsyncData.getFailureState(new RegisterResponse("Fatal Error", "Error")));
            }
        });

        return registerResponseLiveData;
    }



}
