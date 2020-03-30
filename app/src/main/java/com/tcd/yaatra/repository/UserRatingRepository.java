package com.tcd.yaatra.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.tcd.yaatra.repository.models.AsyncData;
import com.tcd.yaatra.services.api.yaatra.api.RatingApi;
import com.tcd.yaatra.services.api.yaatra.models.RateRequestBody;
import com.tcd.yaatra.services.api.yaatra.models.RateResponse;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRatingRepository {
    private RatingApi ratingApi;

    @Inject
    public UserRatingRepository(RatingApi ratingApi) {
        this.ratingApi = ratingApi;
    }

    public LiveData<AsyncData<RateResponse>> rateUsers(RateRequestBody rateRequestBody) {
        MutableLiveData<AsyncData<RateResponse>> rateUsersResponseLiveData = new MutableLiveData<>();
        this.ratingApi.rate(rateRequestBody).enqueue(new Callback<RateResponse>() {
            @Override
            public void onResponse(Call<RateResponse> call, Response<RateResponse> response) {
                switch (response.code()) {
                    case 200: rateUsersResponseLiveData.postValue(AsyncData.getSuccessState(response.body())); break;
                    case 400: rateUsersResponseLiveData.postValue(AsyncData.getFailureState(new RateResponse("Username not found", "Error"))); break;
                    default: rateUsersResponseLiveData.postValue(AsyncData.getFailureState(new RateResponse("Error", "Error")));
                }
            }

            @Override
            public void onFailure(Call<RateResponse> call, Throwable t) {
                rateUsersResponseLiveData.postValue(AsyncData.getFailureState(new RateResponse("Error", "Error")));
            }
        });
        rateUsersResponseLiveData.postValue(AsyncData.getLoadingState());
        return rateUsersResponseLiveData;
    }
}