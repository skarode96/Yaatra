package com.tcd.yaatra.ui.viewmodels;

import androidx.lifecycle.ViewModel;
import com.tcd.yaatra.repository.UserRepository;
import javax.inject.Inject;

public class RouteInfoViewModel extends ViewModel {

    private UserRepository userRepository;

    @Inject
    public RouteInfoViewModel(UserRepository userRepository){
        this.userRepository = userRepository;
    }
}
