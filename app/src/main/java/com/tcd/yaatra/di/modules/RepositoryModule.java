package com.tcd.yaatra.di.modules;

import com.tcd.yaatra.repository.UserRepository;
import com.tcd.yaatra.services.api.yaatra.api.LoginApi;

import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryModule {

    @Provides
    UserRepository providesUserRepository(LoginApi loginApi){
        return new UserRepository(loginApi);
    }
}
