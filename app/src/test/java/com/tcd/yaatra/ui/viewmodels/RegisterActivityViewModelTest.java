package com.tcd.yaatra.ui.viewmodels;

import androidx.lifecycle.MutableLiveData;

import com.tcd.yaatra.repository.UserRepository;
import com.tcd.yaatra.repository.models.AsyncData;
import com.tcd.yaatra.services.api.yaatra.models.RegisterRequestBody;
import com.tcd.yaatra.services.api.yaatra.models.RegisterResponse;

import net.bytebuddy.utility.RandomString;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RegisterActivityViewModelTest {

    @InjectMocks RegisterActivityViewModel registerActivityViewModel;

    @Mock
    UserRepository userRepository;

    @Test
    public void shouldReturnRegisterResponse() {
        final RegisterRequestBody registerRequestBody = new RegisterRequestBody();
        MutableLiveData<AsyncData<RegisterResponse>> registerResponseLiveData = new MutableLiveData<>();
        Mockito.when(userRepository.registerUser(Mockito.any(RegisterRequestBody.class))).thenReturn(registerResponseLiveData);
        Assert.assertEquals(registerActivityViewModel.register(registerRequestBody), registerResponseLiveData);
    }
}