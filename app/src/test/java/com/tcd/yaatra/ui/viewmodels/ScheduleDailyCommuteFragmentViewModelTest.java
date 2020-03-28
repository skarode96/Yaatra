package com.tcd.yaatra.ui.viewmodels;

import androidx.lifecycle.MutableLiveData;

import com.tcd.yaatra.repository.DailyCommuteRepository;
import com.tcd.yaatra.repository.models.AsyncData;
import com.tcd.yaatra.services.api.yaatra.models.ScheduleDailyCommuteRequestBody;
import com.tcd.yaatra.services.api.yaatra.models.ScheduleDailyCommuteResponse;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ScheduleDailyCommuteFragmentViewModelTest {
    @InjectMocks
    ScheduleDailyCommuteFragmentViewModel scheduleDailyCommuteFragmentViewModel;

    @Mock
    DailyCommuteRepository dailyCommuteRepository;

    @Test
    public void scheduleDailyCommute() {
        MutableLiveData<AsyncData<ScheduleDailyCommuteResponse>> scheduleDailyCommuteResponseLiveData = new MutableLiveData<>();
        Mockito.when(dailyCommuteRepository.scheduleDailyCommute(Mockito.any(ScheduleDailyCommuteRequestBody.class))).thenReturn(scheduleDailyCommuteResponseLiveData);
        final ScheduleDailyCommuteRequestBody testScheduleDailyCommuteRequestBody = new ScheduleDailyCommuteRequestBody();
        Assert.assertEquals(scheduleDailyCommuteFragmentViewModel.scheduleDailyCommute(testScheduleDailyCommuteRequestBody), scheduleDailyCommuteResponseLiveData);
    }
}