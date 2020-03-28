package com.tcd.yaatra.repository;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.jraska.livedata.TestObserver;
import com.tcd.yaatra.repository.models.AsyncData;
import com.tcd.yaatra.services.api.yaatra.api.LoginApi;
import com.tcd.yaatra.services.api.yaatra.api.RegisterApi;
import com.tcd.yaatra.services.api.yaatra.models.LoginResponse;
import com.tcd.yaatra.services.api.yaatra.models.RegisterRequestBody;
import com.tcd.yaatra.services.api.yaatra.models.RegisterResponse;

import net.bytebuddy.utility.RandomString;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.mock.Calls;

@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryTest {

    @InjectMocks UserRepository userRepository;

    @Mock
    LoginApi loginApi;
    @Mock
    RegisterApi registerApi;


    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void authenticateUserFor200OkResponse() throws InterruptedException {
        final String userName = RandomString.make();
        final String password = RandomString.make();
        final LoginResponse expectedSuccessResponse = new LoginResponse("Success Msg", "Success Response");
        Call<LoginResponse> response = Calls.response(expectedSuccessResponse);
        Mockito.when(loginApi.getToken(Mockito.anyString(), Mockito.anyString())).thenReturn(response);
        userRepository.authenticateUser(userName, password);
        TestObserver.test(userRepository.loginResponseLiveData)
                .awaitValue()
                .assertHistorySize(1)
                .assertValue(AsyncData.getSuccessState(expectedSuccessResponse));
    }

    @Test
    public void authenticateUserFor401ErrorResponse() throws InterruptedException {
        final String userName = RandomString.make();
        final String password = RandomString.make();
        Response<LoginResponse> mockErrorResponse = Response.error(401,ResponseBody.create(MediaType.parse("application/json"), "Error Msg"));
        Call<LoginResponse> response = Calls.response(mockErrorResponse);
        Mockito.when(loginApi.getToken(Mockito.anyString(), Mockito.anyString())).thenReturn(response);
        userRepository.authenticateUser(userName, password);
        LoginResponse expectedErrorResponse = new LoginResponse("User not Found! Please provide correct username and password", "Error");
        TestObserver.test(userRepository.loginResponseLiveData)
                .awaitValue()
                .assertHistorySize(1)
                .assertValue(AsyncData.getFailureState(expectedErrorResponse));
    }

    @Test
    public void authenticateUserFor400ErrorResponse() throws InterruptedException {
        final String userName = RandomString.make();
        final String password = RandomString.make();
        Response<LoginResponse> mockErrorResponse = Response.error(400,ResponseBody.create(MediaType.parse("application/json"), "Error Msg"));
        Call<LoginResponse> response = Calls.response(mockErrorResponse);
        Mockito.when(loginApi.getToken(Mockito.anyString(), Mockito.anyString())).thenReturn(response);
        userRepository.authenticateUser(userName, password);
        LoginResponse expectedErrorResponse = new LoginResponse("Use post request", "Error");
        TestObserver.test(userRepository.loginResponseLiveData)
                .awaitValue()
                .assertHistorySize(1)
                .assertValue(AsyncData.getFailureState(expectedErrorResponse));
    }


    @Test
    public void authenticateUserForAnyErrorResponseOtherThan400And401() throws InterruptedException {
        final String userName = RandomString.make();
        final String password = RandomString.make();
        Response<LoginResponse> mockErrorResponse = Response.error(403,ResponseBody.create(MediaType.parse("application/json"), "Error Msg"));
        Call<LoginResponse> response = Calls.response(mockErrorResponse);
        Mockito.when(loginApi.getToken(Mockito.anyString(), Mockito.anyString())).thenReturn(response);
        userRepository.authenticateUser(userName, password);
        LoginResponse expectedErrorResponse = new LoginResponse("Fatal Error", "Error");
        TestObserver.test(userRepository.loginResponseLiveData)
                .awaitValue()
                .assertHistorySize(1)
                .assertValue(AsyncData.getFailureState(expectedErrorResponse));
    }


    @Test
    public void registerUserFor201OkResponse() throws InterruptedException {

        final RegisterResponse expectedRegisterResponse = new RegisterResponse("Success Msg", "Success Response");

        ResponseBody body = ResponseBody.create(
                MediaType.parse("application/json"),
                "Test"
        );
        Response aResponse = Response.success(expectedRegisterResponse.toString(), new okhttp3.Response.Builder() //
                .code(201)
                .message("OK")
                .body(body)
                .protocol(Protocol.HTTP_1_1)
                .request(new Request.Builder().url("http://localhost/").build())
                .build());
        Call<RegisterResponse> response = Calls.response(aResponse);

        RegisterRequestBody mockRequestBody = new RegisterRequestBody();
        Mockito.when(registerApi.register(Mockito.any(RegisterRequestBody.class))).thenReturn(response);
        userRepository.registerUser(mockRequestBody);
        TestObserver.test(userRepository.registerResponseLiveData)
                .awaitValue()
                .assertHistorySize(1);
    }


    @Test
    public void registerUserFor400ErrorResponse() throws InterruptedException {
        Response<RegisterResponse> mockErrorResponse = Response.error(400,ResponseBody.create(MediaType.parse("application/json"), "Error Msg"));
        Call<RegisterResponse> response = Calls.response(mockErrorResponse);
        Mockito.when(registerApi.register(Mockito.any(RegisterRequestBody.class))).thenReturn(response);
        RegisterRequestBody mockRequestBody = new RegisterRequestBody();
        userRepository.registerUser(mockRequestBody);
        final RegisterResponse expectedRegisterResponse = new RegisterResponse("Incorrect data, Either email exists or username exists or password did not match", "Error");
        TestObserver.test(userRepository.registerResponseLiveData)
                .awaitValue()
                .assertHistorySize(1)
                .assertValue(AsyncData.getFailureState(expectedRegisterResponse));
    }

    @Test
    public void registerUserForAnyErrorResponseOtherThan400() throws InterruptedException {
        Response<RegisterResponse> mockErrorResponse = Response.error(402,ResponseBody.create(MediaType.parse("application/json"), "Error Msg"));
        Call<RegisterResponse> response = Calls.response(mockErrorResponse);
        Mockito.when(registerApi.register(Mockito.any(RegisterRequestBody.class))).thenReturn(response);
        RegisterRequestBody mockRequestBody = new RegisterRequestBody();
        userRepository.registerUser(mockRequestBody);
        final RegisterResponse expectedRegisterResponse = new RegisterResponse("Fatal Error", "Error");
        TestObserver.test(userRepository.registerResponseLiveData)
                .awaitValue()
                .assertHistorySize(1)
                .assertValue(AsyncData.getFailureState(expectedRegisterResponse));
    }
    
    
}