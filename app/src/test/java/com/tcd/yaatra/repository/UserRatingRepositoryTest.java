package com.tcd.yaatra.repository;


import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.jraska.livedata.TestObserver;
import com.tcd.yaatra.repository.models.AsyncData;
import com.tcd.yaatra.services.api.yaatra.api.RatingApi;
import com.tcd.yaatra.services.api.yaatra.models.RateRequestBody;
import com.tcd.yaatra.services.api.yaatra.models.RateResponse;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.mock.Calls;

@RunWith(MockitoJUnitRunner.class)
public class UserRatingRepositoryTest {

    @InjectMocks
    UserRatingRepository userRatingRepository;

    @Mock
    RatingApi ratingApi;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();


    @Test
    public void rateUserFor200OkResponse() throws InterruptedException {

        final RateRequestBody rateRequestBody = new RateRequestBody();
        final RateResponse expectedSuccessResponse = new RateResponse("Success Msg", "Success Response");
        Call<RateResponse> response = Calls.response(expectedSuccessResponse);
        Mockito.when(ratingApi.rate(Mockito.any(RateRequestBody.class))).thenReturn(response);
        userRatingRepository.rateUsers(rateRequestBody);
        TestObserver.test(userRatingRepository.rateUsersResponseLiveData)
                .awaitValue()
                .assertHistorySize(1)
                .assertValue(AsyncData.getSuccessState(expectedSuccessResponse));
    }

    @Test
    public void rateUsersFor400ErrorResponse() throws InterruptedException {
        final RateRequestBody rateRequestBody = new RateRequestBody();
        Response<RateResponse> mockErrorResponse = Response.error(400, ResponseBody.create(MediaType.parse("application/json"), "Error Msg"));
        Call<RateResponse> response = Calls.response(mockErrorResponse);
        Mockito.when(ratingApi.rate(Mockito.any(RateRequestBody.class))).thenReturn(response);
        userRatingRepository.rateUsers(rateRequestBody);
        RateResponse expectedErrorResponse = new RateResponse("Username not found", "Error");
        TestObserver.test(userRatingRepository.rateUsersResponseLiveData)
                .awaitValue()
                .assertHistorySize(1)
                .assertValue(AsyncData.getFailureState(expectedErrorResponse));
    }


    @Test
    public void rateUsersForAnyErrorResponseOtherThan400And200() throws InterruptedException {
        final RateRequestBody rateRequestBody = new RateRequestBody();
        Response<RateResponse> mockErrorResponse = Response.error(403, ResponseBody.create(MediaType.parse("application/json"), "Error Msg"));
        Call<RateResponse> response = Calls.response(mockErrorResponse);
        Mockito.when(ratingApi.rate(Mockito.any(RateRequestBody.class))).thenReturn(response);
        userRatingRepository.rateUsers(rateRequestBody);
        RateResponse expectedErrorResponse = new RateResponse("Error", "Error");
        TestObserver.test(userRatingRepository.rateUsersResponseLiveData)
                .awaitValue()
                .assertHistorySize(1)
                .assertValue(AsyncData.getFailureState(expectedErrorResponse));
    }

}