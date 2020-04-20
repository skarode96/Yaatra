package com.tcd.yaatra.ui.viewmodels;

import androidx.lifecycle.MutableLiveData;

import com.tcd.yaatra.repository.UserRatingRepository;
import com.tcd.yaatra.repository.models.AsyncData;
import com.tcd.yaatra.services.api.yaatra.models.RateRequestBody;
import com.tcd.yaatra.services.api.yaatra.models.RateResponse;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserRatingActivityViewModelTest {

    @InjectMocks UserRatingActivityViewModel userRatingActivityViewModel;

    @Mock
    UserRatingRepository userRatingRepository;

    @Test
    public void shouldReturnRegisterResponse() {
        final RateRequestBody rateRequestBody = new RateRequestBody();
        MutableLiveData<AsyncData<RateResponse>> rateResponseLiveData = new MutableLiveData<>();
        Mockito.when(userRatingRepository.rateUsers(Mockito.any(RateRequestBody.class))).thenReturn(rateResponseLiveData);
        Assert.assertEquals(userRatingActivityViewModel.rate(rateRequestBody), rateResponseLiveData);
    }
}