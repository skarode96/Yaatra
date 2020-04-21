package com.tcd.yaatra.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.tcd.yaatra.repository.UserRatingRepository;
import com.tcd.yaatra.repository.models.AsyncData;
import com.tcd.yaatra.services.api.yaatra.models.RateRequestBody;
import com.tcd.yaatra.services.api.yaatra.models.RateResponse;

import javax.inject.Inject;

public class UserRatingFragmentViewModel extends ViewModel {
    private UserRatingRepository userRateRepository;

    @Inject
    public UserRatingFragmentViewModel(UserRatingRepository userRatingRepository) {
        this.userRateRepository = userRatingRepository;
    }

    public LiveData<AsyncData<RateResponse>> rate(RateRequestBody rateRequestBody) {
        return this.userRateRepository.rateUsers(rateRequestBody);
    }
}
