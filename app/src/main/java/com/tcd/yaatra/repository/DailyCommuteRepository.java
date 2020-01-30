package com.tcd.yaatra.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.tcd.yaatra.repository.models.AsyncData;
import com.tcd.yaatra.services.api.yaatra.api.DailyCommuteApi;
import com.tcd.yaatra.services.api.yaatra.models.DailyCommuteResponse;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DailyCommuteRepository {
    private DailyCommuteApi dailyCommuteApi;

    @Inject
    public DailyCommuteRepository(DailyCommuteApi dailyCommuteApi){
        this.dailyCommuteApi = dailyCommuteApi;
    }

    public LiveData<AsyncData<DailyCommuteResponse>> getDailyCommute(String token) {
        MutableLiveData<AsyncData<DailyCommuteResponse>> dailyCommuteResponseLiveData = new MutableLiveData<>();
        this.dailyCommuteApi.
                getDailyCommute("Token " + token).enqueue(new Callback<DailyCommuteResponse>() {
            @Override
            public void onResponse(Call<DailyCommuteResponse> call, Response<DailyCommuteResponse> response) {
                if(response.code() == 200) {
                    dailyCommuteResponseLiveData.postValue(AsyncData.getSuccessState(response.body()));
                } else {
                    dailyCommuteResponseLiveData.postValue(AsyncData.getFailureState(null));
                }
            }

            @Override
            public void onFailure(Call<DailyCommuteResponse> call, Throwable t) {
                dailyCommuteResponseLiveData.postValue(AsyncData.getFailureState(null));
            }
        });
        return dailyCommuteResponseLiveData;
    }
}
