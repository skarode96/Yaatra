package com.tcd.yaatra.ui.viewmodels;

import androidx.lifecycle.MutableLiveData;

import com.tcd.yaatra.repository.DailyCommuteRepository;
import com.tcd.yaatra.repository.models.AsyncData;
import com.tcd.yaatra.services.api.yaatra.models.DailyCommuteDetailsResponse;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DailyCommuteDetailsViewModelTest {

    @InjectMocks
    DailyCommuteDetailsViewModel dailyCommuteDetailsViewModel;

    @Mock
    DailyCommuteRepository dailyCommuteRepository;

    @Test
    public void testGetDailyCommuteDetails() {
        MutableLiveData<AsyncData<DailyCommuteDetailsResponse>> dailyCommuteDetailsResponseLiveData = new MutableLiveData<>();
        Mockito.when(dailyCommuteRepository.getDailyCommuteDetails(Mockito.anyInt())).thenReturn(dailyCommuteDetailsResponseLiveData);
        final int testJourneyId = 2;
        Assert.assertEquals(dailyCommuteDetailsViewModel.getDailyCommuteDetails(testJourneyId), dailyCommuteDetailsResponseLiveData);
    }
}