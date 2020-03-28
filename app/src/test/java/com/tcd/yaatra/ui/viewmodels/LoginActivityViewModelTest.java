package com.tcd.yaatra.ui.viewmodels;

import androidx.lifecycle.MutableLiveData;

import com.tcd.yaatra.repository.UserRepository;
import com.tcd.yaatra.repository.models.AsyncData;
import com.tcd.yaatra.services.api.yaatra.models.LoginResponse;

import net.bytebuddy.utility.RandomString;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LoginActivityViewModelTest {

    @InjectMocks
    LoginActivityViewModel loginActivityViewModel;

    @Mock
    UserRepository userRepository;

    @Test
    public void shouldReturnUserDetailsLiveData() {
        final String testUserName = RandomString.make();
        final String testPassword = RandomString.make();
        MutableLiveData<AsyncData<LoginResponse>> loginResponseLiveData = new MutableLiveData<>();
        Mockito.when(userRepository.authenticateUser(Mockito.anyString(),  Mockito.anyString())).thenReturn(loginResponseLiveData);
        Assert.assertEquals(loginActivityViewModel.authenticateUser(testUserName, testPassword), loginResponseLiveData);
    }
}