package com.tcd.yaatra.repository;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.jraska.livedata.TestObserver;
import com.tcd.yaatra.repository.models.AsyncData;
import com.tcd.yaatra.services.api.yaatra.api.DailyCommuteApi;
import com.tcd.yaatra.services.api.yaatra.api.DailyCommuteDetailsApi;
import com.tcd.yaatra.services.api.yaatra.api.ScheduleDailyCommuteApi;
import com.tcd.yaatra.services.api.yaatra.models.DailyCommuteDetailsResponse;
import com.tcd.yaatra.services.api.yaatra.models.DailyCommuteResponse;
import com.tcd.yaatra.services.api.yaatra.models.ScheduleDailyCommuteRequestBody;
import com.tcd.yaatra.services.api.yaatra.models.ScheduleDailyCommuteResponse;

import org.jetbrains.annotations.NotNull;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.mock.Calls;


@RunWith(MockitoJUnitRunner.class)
public class DailyCommuteRepositoryTest {

    @InjectMocks
    private DailyCommuteRepository dailyCommuteRepository;

    @Mock
    private DailyCommuteApi dailyCommuteApi;

    @Mock
    private ScheduleDailyCommuteApi scheduleDailyCommuteApi;

    @Mock
    private DailyCommuteDetailsApi dailyCommuteDetailsApi;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();



    @Test
    public void getDailyCommuteFor200OkResponse() throws InterruptedException {
        final DailyCommuteResponse expectedDailyCommuteResponse = new DailyCommuteResponse("Success Msg", "Success Response");
        Call<DailyCommuteResponse> response = Calls.response(expectedDailyCommuteResponse);
        Mockito.when(dailyCommuteApi.getDailyCommute()).thenReturn(response);
        dailyCommuteRepository.getDailyCommute();
        TestObserver.test(dailyCommuteRepository.dailyCommuteResponseLiveData)
                .awaitValue()
                .assertHistorySize(1)
                .assertValue(AsyncData.getSuccessState(expectedDailyCommuteResponse));

    }

    @Test
    public void getDailyCommuteForErrorResponse() throws InterruptedException {
        final DailyCommuteResponse expectedDailyCommuteErrorResponse = new DailyCommuteResponse("Fatal Error", "Error");
        Response<DailyCommuteResponse> mockErrorResponse = Response.error(401, ResponseBody.create(MediaType.parse("application/json"), "Error Msg"));
        Call<DailyCommuteResponse> response = Calls.response(mockErrorResponse);
        Mockito.when(dailyCommuteApi.getDailyCommute()).thenReturn(response);
        dailyCommuteRepository.getDailyCommute();
        TestObserver.test(dailyCommuteRepository.dailyCommuteResponseLiveData)
                .awaitValue()
                .assertHistorySize(1)
                .assertValue(AsyncData.getFailureState(expectedDailyCommuteErrorResponse));
    }

    @Test
    public void getDailyCommuteForLoadingState() throws InterruptedException {
        final AsyncData<DailyCommuteResponse> expectedLoadingState = AsyncData.getLoadingState();
        Call<DailyCommuteResponse> response = getMockCallObject();
        Mockito.when(dailyCommuteApi.getDailyCommute()).thenReturn(response);
        dailyCommuteRepository.getDailyCommute();
        TestObserver.test(dailyCommuteRepository.dailyCommuteResponseLiveData)
                .awaitValue()
                .assertHistorySize(1)
                .assertValue(expectedLoadingState);
    }

    @Test
    public void scheduleDailyCommuteFor201CreatedResponse() throws InterruptedException {
        final ScheduleDailyCommuteResponse expectedScheduleDailyCommuteResponse = new ScheduleDailyCommuteResponse("Success Created", "Success");

        ResponseBody body = ResponseBody.create(
                MediaType.parse("application/json"),
                "Test"
        );
        Response mockResponse = Response.success(expectedScheduleDailyCommuteResponse.toString(), new okhttp3.Response.Builder() //
                .code(201)
                .message("OK")
                .body(body)
                .protocol(Protocol.HTTP_1_1)
                .request(new Request.Builder().url("http://localhost/").build())
                .build());
        Call<ScheduleDailyCommuteResponse> response = Calls.response(mockResponse);

        ScheduleDailyCommuteRequestBody mockScheduleDailyCommuteRequestBody = new ScheduleDailyCommuteRequestBody();
        Mockito.when(scheduleDailyCommuteApi.createDailyCommute(Mockito.any(ScheduleDailyCommuteRequestBody.class
        ))).thenReturn(response);

        dailyCommuteRepository.scheduleDailyCommute(mockScheduleDailyCommuteRequestBody);
        TestObserver.test(dailyCommuteRepository.scheduleDailyCommuteResponseLiveData)
                .awaitValue()
                .assertHistorySize(1);
    }

    @Test
    public void scheduleDailyCommuteForAnyErrorCode() throws InterruptedException {
        final ScheduleDailyCommuteResponse expectedScheduleDailyCommuteResponse = new ScheduleDailyCommuteResponse("Fatal Error", "Error");
        Response<ScheduleDailyCommuteResponse> mockErrorResponse = Response.error(405, ResponseBody.create(MediaType.parse("application/json"), "Error Msg"));
        Call<ScheduleDailyCommuteResponse> response = Calls.response(mockErrorResponse);
        Mockito.when(scheduleDailyCommuteApi.createDailyCommute(Mockito.any(ScheduleDailyCommuteRequestBody.class
        ))).thenReturn(response);
        ScheduleDailyCommuteRequestBody mockScheduleDailyCommuteRequestBody = new ScheduleDailyCommuteRequestBody();
        dailyCommuteRepository.scheduleDailyCommute(mockScheduleDailyCommuteRequestBody);
        TestObserver.test(dailyCommuteRepository.scheduleDailyCommuteResponseLiveData)
                .awaitValue()
                .assertHistorySize(1)
                .assertValue(AsyncData.getFailureState(expectedScheduleDailyCommuteResponse));
    }

    @Test
    public void getDailyCommuteDetailsFor200OkResponse() throws InterruptedException {
        final DailyCommuteDetailsResponse expectedDailyCommuteDetailsResponse = new DailyCommuteDetailsResponse("Success Msg", "Success Response");
        Call<DailyCommuteDetailsResponse> response = Calls.response(expectedDailyCommuteDetailsResponse);
        Mockito.when(dailyCommuteDetailsApi.getDailyCommuteDetails(Mockito.anyInt())).thenReturn(response);
        final int testJourneyId = 1;
        dailyCommuteRepository.getDailyCommuteDetails(testJourneyId);
        TestObserver.test(dailyCommuteRepository.dailyCommuteDetailsResponseLiveData)
                .awaitValue()
                .assertHistorySize(1)
                .assertValue(AsyncData.getSuccessState(expectedDailyCommuteDetailsResponse));

    }

    @Test
    public void getDailyCommuteDetailsForErrorResponse() throws InterruptedException {
        final DailyCommuteDetailsResponse expectedDailyCommuteDetailsErrorResponse = new DailyCommuteDetailsResponse("Fatal Error", "Error");
        Response<DailyCommuteDetailsResponse> mockErrorResponse = Response.error(401, ResponseBody.create(MediaType.parse("application/json"), "Error Msg"));
        Call<DailyCommuteDetailsResponse> response = Calls.response(mockErrorResponse);
        Mockito.when(dailyCommuteDetailsApi.getDailyCommuteDetails(Mockito.anyInt())).thenReturn(response);
        final int testJourneyId = 1;
        dailyCommuteRepository.getDailyCommuteDetails(testJourneyId);
        TestObserver.test(dailyCommuteRepository.dailyCommuteDetailsResponseLiveData)
                .awaitValue()
                .assertHistorySize(1)
                .assertValue(AsyncData.getFailureState(expectedDailyCommuteDetailsErrorResponse));
    }


    @NotNull
    private Call<DailyCommuteResponse> getMockCallObject() {
        return new Call<DailyCommuteResponse>() {
            @Override
            public Response<DailyCommuteResponse> execute() throws IOException {
                return null;
            }

            @Override
            public void enqueue(Callback<DailyCommuteResponse> callback) {

            }

            @Override
            public boolean isExecuted() {
                return false;
            }

            @Override
            public void cancel() {

            }

            @Override
            public boolean isCanceled() {
                return false;
            }

            @Override
            public Call<DailyCommuteResponse> clone() {
                return null;
            }

            @Override
            public Request request() {
                return null;
            }
        };
    }

}