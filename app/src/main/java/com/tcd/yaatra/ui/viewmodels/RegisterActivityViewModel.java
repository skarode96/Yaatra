package com.tcd.yaatra.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.tcd.yaatra.repository.UserRepository;
import com.tcd.yaatra.repository.models.AsyncData;
import com.tcd.yaatra.services.api.yaatra.models.RegisterRequestBody;
import com.tcd.yaatra.services.api.yaatra.models.RegisterResponse;

import javax.inject.Inject;

public class RegisterActivityViewModel extends ViewModel {
    private UserRepository userRepository;

    @Inject
    public RegisterActivityViewModel(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public LiveData<AsyncData<RegisterResponse>> register(RegisterRequestBody registerRequestBody){
        return this.userRepository.registerUser(registerRequestBody);
    }
}
