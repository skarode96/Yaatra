package com.tcd.yaatra.di.modules;

import com.tcd.yaatra.services.api.yaatra.api.DailyCommuteApi;
import com.tcd.yaatra.services.api.yaatra.api.LoginApi;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class YaatraApiModule {

    private final static String SERVER_URL = "https://yaatra-services.herokuapp.com";


    @Provides
    @Singleton
    @Named("yaatraRetrofit")
    Retrofit provideBuilder(){
        final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(SERVER_URL)
                .client(client)
                .build();
    }

    @Provides
    @Singleton
    LoginApi providesLoginApi(@Named("yaatraRetrofit") Retrofit retrofit){
        return retrofit.create(LoginApi.class);
    }

    @Provides
    @Singleton
    DailyCommuteApi providesDailyCommuteApi(@Named("yaatraRetrofit") Retrofit retrofit) {
        return retrofit.create(DailyCommuteApi.class);
    }


}
