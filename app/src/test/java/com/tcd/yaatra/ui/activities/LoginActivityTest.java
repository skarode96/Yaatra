package com.tcd.yaatra.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.example.loginjourneysharing.activities.User;
import com.tcd.yaatra.di.modules.YaatraApiModule;
import com.tcd.yaatra.repository.UserRepository;
import com.tcd.yaatra.repository.models.AsyncData;
import com.tcd.yaatra.services.api.yaatra.api.LoginApi;
import com.tcd.yaatra.services.api.yaatra.models.LoginResponse;
import com.tcd.yaatra.ui.viewmodels.LoginActivityViewModel;
import com.tcd.yaatra.utils.SharedPreferenceUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import androidx.lifecycle.LiveData;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LoginActivityTest {
    private final static String SERVER_URL = "https://yaatra-services.herokuapp.com";

    private LoginActivity loginActivity;
    private MockWebServer mockWebServer = new MockWebServer();
    private LoginApi loginApi;
    @Mock
    private LoginActivityViewModel loginActivityViewModel;

    @Mock
    private UserRepository userRepository;

    @Mock
    Context mMockContext;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockWebServer.start();

        loginApi = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(SERVER_URL)
                .client(new OkHttpClient())
                .build().create(LoginApi.class);
        loginActivity = new LoginActivity();
    }



    @Test
    @Ignore
    public void test1() {
        String testUser = "";
        String testPass = "";


        Mockito.when(loginActivityViewModel.authenticateUser(testUser, testPass)).thenReturn(new LiveData<AsyncData<LoginResponse>>() {

        });

        loginActivity.layoutDataBinding.login.performClick();

    }

    @After
    public void teardown() throws IOException {
        mockWebServer.shutdown();
    }


}