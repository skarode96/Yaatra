package com.tcd.yaatra.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.tcd.yaatra.repository.UserRepository;
import com.tcd.yaatra.services.api.yaatra.models.LoginResponse;

import javax.inject.Inject;

public class LoginActivityViewModel extends ViewModel {

    private UserRepository userRepository;

    @Inject
    public LoginActivityViewModel(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public LiveData<LoginResponse> authenticateUser(String username, String password){
        return this.userRepository.authenticateUser(username, password);
    }
}
