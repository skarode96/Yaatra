package com.tcd.yaatra.di.modules;

import com.tcd.yaatra.services.api.yaatra.api.LoginApi;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class YaatraApiModule {

    private final static String SERVER_URL = "https://yaatra-services.herokuapp.com";


    @Provides
    @Singleton
    @Named("yaatraRetrofit")
    Retrofit provieBuilder(){
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(SERVER_URL)
                .build();
    }

    @Provides
    @Singleton
    LoginApi providesLoginApi(@Named("yaatraRetrofit") Retrofit retrofit){
        return retrofit.create(LoginApi.class);
    }


}
