package com.tcd.yaatra.di.modules;

import com.tcd.yaatra.services.api.yaatra.api.CreateDailyCommuteApi;
import com.tcd.yaatra.services.api.yaatra.api.DailyCommuteApi;
import com.tcd.yaatra.services.api.yaatra.api.DailyCommuteDetailsApi;
import com.tcd.yaatra.services.api.yaatra.api.LoginApi;
import com.tcd.yaatra.services.api.yaatra.api.RegisterApi;
import com.tcd.yaatra.utils.SharedPreferenceUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
        final HttpLoggingInterceptor httpLoggingInterceptor = getHttpLoggingInterceptor();
        final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(YaatraApiModule::intercept)
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
    RegisterApi providesRegisterApi(@Named("yaatraRetrofit") Retrofit retrofit) {
        return retrofit.create(RegisterApi.class);
    }

    @Provides
    @Singleton
    DailyCommuteApi providesDailyCommuteApi(@Named("yaatraRetrofit") Retrofit retrofit) {
        return retrofit.create(DailyCommuteApi.class);
    }

    @Provides
    @Singleton
    CreateDailyCommuteApi providesCreateDailyCommuteApi(@Named("yaatraRetrofit") Retrofit retrofit) {
        return retrofit.create(CreateDailyCommuteApi.class);
    }

    @Provides
    @Singleton
    DailyCommuteDetailsApi providesDailyCommuteDetailsApi(@Named("yaatraRetrofit") Retrofit retrofit) {
        return retrofit.create(DailyCommuteDetailsApi.class);
    }

    @NotNull
    private HttpLoggingInterceptor getHttpLoggingInterceptor() {
        final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    private static Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request()
                .newBuilder()
                .addHeader("Authorization", getToken())
                .build();
        Response response = chain.proceed(request);
        return response;
    }

    private static String getToken() {
        return SharedPreferenceUtils.getAuthToken();
    }

}
