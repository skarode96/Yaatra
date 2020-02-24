package com.tcd.yaatra.di.modules;

import com.tcd.yaatra.repository.UserInfoDataSource;
import com.tcd.yaatra.repository.UserRepository;
import com.tcd.yaatra.repository.dao.UserInfoDao;
import com.tcd.yaatra.services.api.yaatra.api.LoginApi;
import com.tcd.yaatra.services.api.yaatra.api.RegisterApi;

import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryModule {

    @Provides
    UserRepository providesUserRepository(LoginApi loginApi, RegisterApi registerApi){
        return new UserRepository(loginApi, registerApi);
    }

}
