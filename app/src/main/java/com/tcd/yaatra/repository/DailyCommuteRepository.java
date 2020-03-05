package com.tcd.yaatra.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.tcd.yaatra.repository.models.AsyncData;
import com.tcd.yaatra.services.api.yaatra.api.CreateDailyCommuteApi;
import com.tcd.yaatra.services.api.yaatra.api.DailyCommuteApi;
import com.tcd.yaatra.services.api.yaatra.models.CreateDailyCommuteRequestBody;
import com.tcd.yaatra.services.api.yaatra.models.CreateDailyCommuteResponse;
import com.tcd.yaatra.services.api.yaatra.models.Journey;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DailyCommuteRepository {
    private DailyCommuteApi dailyCommuteApi;
    private CreateDailyCommuteApi createDailyCommuteApi;

    @Inject
    public DailyCommuteRepository(DailyCommuteApi dailyCommuteApi, CreateDailyCommuteApi createDailyCommuteApi) {
        this.dailyCommuteApi = dailyCommuteApi;
        this.createDailyCommuteApi = createDailyCommuteApi;
    }

    public LiveData<AsyncData<List<Journey>>> getDailyCommute() {
        MutableLiveData<AsyncData<List<Journey>>> dailyCommuteResponseLiveData = new MutableLiveData<>();
        this.dailyCommuteApi.getDailyCommute().enqueue(new Callback<List<Journey>>() {
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

}
