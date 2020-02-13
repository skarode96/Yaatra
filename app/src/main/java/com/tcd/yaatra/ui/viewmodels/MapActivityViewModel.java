package com.tcd.yaatra.ui.viewmodels;

import androidx.lifecycle.ViewModel;

import com.tcd.yaatra.repository.UserRepository;

import javax.inject.Inject;

public class MapActivityViewModel extends ViewModel {

    private UserRepository userRepository;

    @Inject
    public MapActivityViewModel(UserRepository userRepository){
        this.userRepository = userRepository;
    }
}
