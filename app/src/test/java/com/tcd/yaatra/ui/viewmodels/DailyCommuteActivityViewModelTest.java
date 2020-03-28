package com.tcd.yaatra.ui.viewmodels;

import androidx.lifecycle.MutableLiveData;

import com.tcd.yaatra.repository.DailyCommuteRepository;
import com.tcd.yaatra.repository.models.AsyncData;
import com.tcd.yaatra.services.api.yaatra.models.DailyCommuteResponse;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DailyCommuteActivityViewModelTest {

    @InjectMocks DailyCommuteActivityViewModel dailyCommuteActivityViewModel;

    @Mock
    DailyCommuteRepository dailyCommuteRepository;

    @Test
    public void testGetDailyCommute() {
        MutableLiveData<AsyncData<DailyCommuteResponse>> dailyCommuteResponseLiveData = new MutableLiveData<>();
        Mockito.when(dailyCommuteRepository.getDailyCommute()).thenReturn(dailyCommuteResponseLiveData);
        Assert.assertEquals(dailyCommuteActivityViewModel.getDailyCommute(), dailyCommuteResponseLiveData);
    }
}