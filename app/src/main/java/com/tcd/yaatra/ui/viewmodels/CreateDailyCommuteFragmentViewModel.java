package com.tcd.yaatra.ui.viewmodels;

import com.tcd.yaatra.repository.DailyCommuteRepository;
import com.tcd.yaatra.repository.models.AsyncData;
import com.tcd.yaatra.services.api.yaatra.models.ScheduleDailyCommuteRequestBody;
import com.tcd.yaatra.services.api.yaatra.models.ScheduleDailyCommuteResponse;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class CreateDailyCommuteFragmentViewModel extends ViewModel {

    private DailyCommuteRepository dailyCommuteRepository;

    @Inject
    public CreateDailyCommuteFragmentViewModel(DailyCommuteRepository dailyCommuteRepository){
        this.dailyCommuteRepository = dailyCommuteRepository;
    }

    public LiveData<AsyncData<ScheduleDailyCommuteResponse>> createDaily(ScheduleDailyCommuteRequestBody scheduleDailyCommuteRequestBody){
        return this.dailyCommuteRepository.scheduleDailyCommute(scheduleDailyCommuteRequestBody);
    }
}
