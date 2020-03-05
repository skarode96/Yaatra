package com.tcd.yaatra.ui.viewmodels;

import com.tcd.yaatra.repository.DailyCommuteRepository;
import com.tcd.yaatra.repository.UserRepository;
import com.tcd.yaatra.repository.models.AsyncData;
import com.tcd.yaatra.services.api.yaatra.models.CreateDailyCommuteRequestBody;
import com.tcd.yaatra.services.api.yaatra.models.CreateDailyCommuteResponse;
import com.tcd.yaatra.services.api.yaatra.models.RegisterRequestBody;
import com.tcd.yaatra.services.api.yaatra.models.RegisterResponse;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class CreateDailyCommuteFragmentViewModel extends ViewModel {

    private DailyCommuteRepository dailyCommuteRepository;

    @Inject
    public CreateDailyCommuteFragmentViewModel(DailyCommuteRepository dailyCommuteRepository){
        this.dailyCommuteRepository = dailyCommuteRepository;
    }

    public LiveData<AsyncData<CreateDailyCommuteResponse>> createDaily(CreateDailyCommuteRequestBody createDailyCommuteRequestBody){
        return this.dailyCommuteRepository.createDailyCommute(createDailyCommuteRequestBody);
    }
}
