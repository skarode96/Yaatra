package com.tcd.yaatra.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.tcd.yaatra.repository.models.AsyncData;
import com.tcd.yaatra.services.api.yaatra.api.DailyCommuteApi;
import com.tcd.yaatra.services.api.yaatra.models.Journey;

import java.util.List;

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

    public LiveData<AsyncData<List<Journey>>> getDailyCommute(String token) {
        MutableLiveData<AsyncData<List<Journey>>> dailyCommuteResponseLiveData = new MutableLiveData<>();
        this.dailyCommuteApi.
                getDailyCommute("Token " + token).enqueue(new Callback<List<Journey>>() {
            @Override
            public void onResponse(Call<List<Journey>> call, Response<List<Journey>> response) {
                if(response.code() == 200) {
                    dailyCommuteResponseLiveData.postValue(AsyncData.getSuccessState(response.body()));
                } else {
                    dailyCommuteResponseLiveData.postValue(AsyncData.getFailureState(null));
                }
            }

            @Override
            public void onFailure(Call<List<Journey>> call, Throwable t) {
                dailyCommuteResponseLiveData.postValue(AsyncData.getFailureState(null));
            }
        });

        return dailyCommuteResponseLiveData;
    }
}
