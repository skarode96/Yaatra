package com.tcd.yaatra.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.tcd.yaatra.repository.DailyCommuteRepository;
import com.tcd.yaatra.repository.models.AsyncData;
import com.tcd.yaatra.services.api.yaatra.models.DailyCommuteResponse;

import java.util.List;

import javax.inject.Inject;

public class DailyCommuteActivityViewModel extends ViewModel {
    private DailyCommuteRepository dailyCommuteRepository;

    @Inject
    DailyCommuteActivityViewModel(DailyCommuteRepository dailyCommuteRepository) {
        this.dailyCommuteRepository = dailyCommuteRepository;
    }

    public LiveData<AsyncData<DailyCommuteResponse>> getDailyCommute(){
        return this.dailyCommuteRepository.getDailyCommute();
    }
}
