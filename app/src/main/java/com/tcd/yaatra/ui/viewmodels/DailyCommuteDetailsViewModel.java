package com.tcd.yaatra.ui.viewmodels;

import com.tcd.yaatra.repository.DailyCommuteRepository;
import com.tcd.yaatra.repository.models.AsyncData;
import com.tcd.yaatra.services.api.yaatra.models.DailyCommuteDetailsResponse;
import com.tcd.yaatra.services.api.yaatra.models.DailyCommuteResponse;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class DailyCommuteDetailsViewModel extends ViewModel {

    private DailyCommuteRepository dailyCommuteRepository;

    @Inject
    DailyCommuteDetailsViewModel(DailyCommuteRepository dailyCommuteRepository) {
        this.dailyCommuteRepository = dailyCommuteRepository;
    }

    public LiveData<AsyncData<DailyCommuteDetailsResponse>> getDailyCommuteDetails(int journeyId){
        return this.dailyCommuteRepository.getDailyCommuteDetails(journeyId);
    }

}
