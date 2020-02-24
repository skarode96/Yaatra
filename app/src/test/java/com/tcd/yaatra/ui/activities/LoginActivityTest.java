package com.tcd.yaatra.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.example.loginjourneysharing.activities.User;
import com.tcd.yaatra.repository.UserRepository;
import com.tcd.yaatra.repository.models.AsyncData;
import com.tcd.yaatra.services.api.yaatra.models.LoginResponse;
import com.tcd.yaatra.ui.viewmodels.LoginActivityViewModel;
import com.tcd.yaatra.utils.SharedPreferenceUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import androidx.lifecycle.LiveData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LoginActivityTest {

    private LoginActivity loginActivity;
    @Mock
    private LoginActivityViewModel loginActivityViewModel;

    @Mock
    private UserRepository userRepository;

    @Mock
    Context mMockContext;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
//        loginActivity = new LoginActivity(userRepository,mMockContext);
    }


    @Test
    public void test1() {
        String testUser = "";
        String testPass = "";
        Mockito.when(loginActivityViewModel.authenticateUser(testUser, testPass)).thenReturn(new LiveData<AsyncData<LoginResponse>>() {

        });
    }


}