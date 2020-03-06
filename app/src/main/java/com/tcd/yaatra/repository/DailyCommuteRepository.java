package com.tcd.yaatra.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.tcd.yaatra.repository.models.AsyncData;
import com.tcd.yaatra.services.api.yaatra.api.CreateDailyCommuteApi;
import com.tcd.yaatra.services.api.yaatra.api.DailyCommuteApi;
import com.tcd.yaatra.services.api.yaatra.api.DailyCommuteDetailsApi;
import com.tcd.yaatra.services.api.yaatra.models.CreateDailyCommuteRequestBody;
import com.tcd.yaatra.services.api.yaatra.models.CreateDailyCommuteResponse;
import com.tcd.yaatra.services.api.yaatra.models.DailyCommuteDetailsResponse;
import com.tcd.yaatra.services.api.yaatra.models.DailyCommuteResponse;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DailyCommuteRepository {
    private DailyCommuteApi dailyCommuteApi;
    private CreateDailyCommuteApi createDailyCommuteApi;
    private DailyCommuteDetailsApi dailyCommuteDetailsApi;

    @Inject
    public DailyCommuteRepository(DailyCommuteApi dailyCommuteApi, CreateDailyCommuteApi createDailyCommuteApi, DailyCommuteDetailsApi dailyCommuteDetailsApi) {
        this.dailyCommuteApi = dailyCommuteApi;
        this.createDailyCommuteApi = createDailyCommuteApi;
        this.dailyCommuteDetailsApi = dailyCommuteDetailsApi;
    }

    public LiveData<AsyncData<DailyCommuteResponse>> getDailyCommute() {
        MutableLiveData<AsyncData<DailyCommuteResponse>> dailyCommuteResponseLiveData = new MutableLiveData<>();

        this.dailyCommuteApi.getDailyCommute().enqueue(new Callback<DailyCommuteResponse>() {
            @Override
            public void onResponse(Call<DailyCommuteResponse> call, Response<DailyCommuteResponse> response) {
                switch (response.code()) {
                    case 200: dailyCommuteResponseLiveData.postValue(AsyncData.getSuccessState(response.body()));break;
                    default: dailyCommuteResponseLiveData.postValue(AsyncData.getFailureState(null));
                }
            }

            @Override
            public void onFailure(Call<DailyCommuteResponse> call, Throwable t) {
                dailyCommuteResponseLiveData.postValue(AsyncData.getFailureState(null));
            }
        });

        dailyCommuteResponseLiveData.postValue(AsyncData.getLoadingState());
        return dailyCommuteResponseLiveData;
    }

    public LiveData<AsyncData<CreateDailyCommuteResponse>> createDailyCommute(CreateDailyCommuteRequestBody createDailyCommuteRequestBody){

        MutableLiveData<AsyncData<CreateDailyCommuteResponse>> createDailyCommuteResponseLiveData = new MutableLiveData<>();

        this.createDailyCommuteApi.createDailyCommute(createDailyCommuteRequestBody).enqueue(new Callback<CreateDailyCommuteResponse>() {
            @Override
            public void onResponse(Call<CreateDailyCommuteResponse> call, Response<CreateDailyCommuteResponse> response) {
                switch (response.code()) {
                    case 201: createDailyCommuteResponseLiveData.postValue(AsyncData.getSuccessState(response.body()));break;
                    default: createDailyCommuteResponseLiveData.postValue(AsyncData.getFailureState(null));
                }
            }

            @Override
            public void onFailure(Call<CreateDailyCommuteResponse> call, Throwable t) {
                createDailyCommuteResponseLiveData.postValue(AsyncData.getFailureState(null));
            }
        });

        createDailyCommuteResponseLiveData.postValue(AsyncData.getLoadingState());

        return createDailyCommuteResponseLiveData;
    }


    public LiveData<AsyncData<DailyCommuteDetailsResponse>> getDailyCommuteDetails(int journeyId) {
        MutableLiveData<AsyncData<DailyCommuteDetailsResponse>> dailyCommuteDetailsResponseLiveData = new MutableLiveData<>();

        this.dailyCommuteDetailsApi.getDailyCommuteDetails(journeyId).enqueue(new Callback<DailyCommuteDetailsResponse>() {
            @Override
            public void onResponse(Call<DailyCommuteDetailsResponse> call, Response<DailyCommuteDetailsResponse> response) {
                switch (response.code()) {
                    case 200: dailyCommuteDetailsResponseLiveData.postValue(AsyncData.getSuccessState(response.body()));break;
                    default: dailyCommuteDetailsResponseLiveData.postValue(AsyncData.getFailureState(null));
                }
            }

            @Override
            public void onFailure(Call<DailyCommuteDetailsResponse> call, Throwable t) {
                dailyCommuteDetailsResponseLiveData.postValue(AsyncData.getFailureState(null));
            }
        });

        dailyCommuteDetailsResponseLiveData.postValue(AsyncData.getLoadingState());
        return dailyCommuteDetailsResponseLiveData;
    }

}
