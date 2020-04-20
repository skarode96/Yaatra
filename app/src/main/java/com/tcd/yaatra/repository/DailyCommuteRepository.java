package com.tcd.yaatra.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.tcd.yaatra.repository.models.AsyncData;
import com.tcd.yaatra.services.api.yaatra.api.ScheduleDailyCommuteApi;
import com.tcd.yaatra.services.api.yaatra.api.DailyCommuteApi;
import com.tcd.yaatra.services.api.yaatra.api.DailyCommuteDetailsApi;
import com.tcd.yaatra.services.api.yaatra.models.ScheduleDailyCommuteRequestBody;
import com.tcd.yaatra.services.api.yaatra.models.ScheduleDailyCommuteResponse;
import com.tcd.yaatra.services.api.yaatra.models.DailyCommuteDetailsResponse;
import com.tcd.yaatra.services.api.yaatra.models.DailyCommuteResponse;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DailyCommuteRepository {
    private DailyCommuteApi dailyCommuteApi;
    private ScheduleDailyCommuteApi scheduleDailyCommuteApi;
    private DailyCommuteDetailsApi dailyCommuteDetailsApi;

    protected MutableLiveData<AsyncData<DailyCommuteDetailsResponse>> dailyCommuteDetailsResponseLiveData;
    protected MutableLiveData<AsyncData<DailyCommuteResponse>> dailyCommuteResponseLiveData;
    protected MutableLiveData<AsyncData<ScheduleDailyCommuteResponse>> scheduleDailyCommuteResponseLiveData;


    @Inject
    public DailyCommuteRepository(DailyCommuteApi dailyCommuteApi, ScheduleDailyCommuteApi scheduleDailyCommuteApi, DailyCommuteDetailsApi dailyCommuteDetailsApi) {
        this.dailyCommuteApi = dailyCommuteApi;
        this.scheduleDailyCommuteApi = scheduleDailyCommuteApi;
        this.dailyCommuteDetailsApi = dailyCommuteDetailsApi;
    }

    public LiveData<AsyncData<DailyCommuteResponse>> getDailyCommute() {

        this.dailyCommuteResponseLiveData = new MutableLiveData<>();
        dailyCommuteResponseLiveData.postValue(AsyncData.getLoadingState());

        this.dailyCommuteApi.getDailyCommute().enqueue(new Callback<DailyCommuteResponse>() {
            @Override
            public void onResponse(Call<DailyCommuteResponse> call, Response<DailyCommuteResponse> response) {
                switch (response.code()) {
                    case 200: dailyCommuteResponseLiveData.postValue(AsyncData.getSuccessState(response.body()));break;
                    default: dailyCommuteResponseLiveData.postValue(AsyncData.getFailureState(new DailyCommuteResponse("Fatal Error", "Error")));
                }
            }

            @Override
            public void onFailure(Call<DailyCommuteResponse> call, Throwable t) {
                dailyCommuteResponseLiveData.postValue(AsyncData.getFailureState(new DailyCommuteResponse("Fatal Error", "Error")));
            }
        });

        return dailyCommuteResponseLiveData;
    }

    public LiveData<AsyncData<ScheduleDailyCommuteResponse>> scheduleDailyCommute(ScheduleDailyCommuteRequestBody scheduleDailyCommuteRequestBody){

        this.scheduleDailyCommuteResponseLiveData = new MutableLiveData<>();
        scheduleDailyCommuteResponseLiveData.postValue(AsyncData.getLoadingState());

        this.scheduleDailyCommuteApi.createDailyCommute(scheduleDailyCommuteRequestBody).enqueue(new Callback<ScheduleDailyCommuteResponse>() {
            @Override
            public void onResponse(Call<ScheduleDailyCommuteResponse> call, Response<ScheduleDailyCommuteResponse> response) {
                switch (response.code()) {
                    case 201: scheduleDailyCommuteResponseLiveData.postValue(AsyncData.getSuccessState(response.body()));break;
                    default: scheduleDailyCommuteResponseLiveData.postValue(AsyncData.getFailureState(new ScheduleDailyCommuteResponse("Fatal Error", "Error")));
                }
            }

            @Override
            public void onFailure(Call<ScheduleDailyCommuteResponse> call, Throwable t) {
                scheduleDailyCommuteResponseLiveData.postValue(AsyncData.getFailureState(new ScheduleDailyCommuteResponse("Fatal Error", "Error")));;
            }
        });


        return scheduleDailyCommuteResponseLiveData;
    }


    public LiveData<AsyncData<DailyCommuteDetailsResponse>> getDailyCommuteDetails(int journeyId) {

        this.dailyCommuteDetailsResponseLiveData = new MutableLiveData<>();
        dailyCommuteDetailsResponseLiveData.postValue(AsyncData.getLoadingState());

        this.dailyCommuteDetailsApi.getDailyCommuteDetails(journeyId).enqueue(new Callback<DailyCommuteDetailsResponse>() {
            @Override
            public void onResponse(Call<DailyCommuteDetailsResponse> call, Response<DailyCommuteDetailsResponse> response) {
                switch (response.code()) {
                    case 200: dailyCommuteDetailsResponseLiveData.postValue(AsyncData.getSuccessState(response.body()));break;
                    default: dailyCommuteDetailsResponseLiveData.postValue(AsyncData.getFailureState(new DailyCommuteDetailsResponse("Fatal Error", "Error")));
                }
            }

            @Override
            public void onFailure(Call<DailyCommuteDetailsResponse> call, Throwable t) {
                dailyCommuteDetailsResponseLiveData.postValue(AsyncData.getFailureState(new DailyCommuteDetailsResponse("Fatal Error", "Error")));
            }
        });

        return dailyCommuteDetailsResponseLiveData;
    }

}
