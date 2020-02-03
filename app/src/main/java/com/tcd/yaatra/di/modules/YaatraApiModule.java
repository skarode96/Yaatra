package com.tcd.yaatra.di.modules;

import com.tcd.yaatra.services.api.yaatra.api.DailyCommuteApi;
import com.tcd.yaatra.services.api.yaatra.api.LoginApi;

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

import static com.tcd.yaatra.App.getAppContext;


@Module
public class YaatraApiModule {

    private final static String SERVER_URL = "https://yaatra-services.herokuapp.com";

    private static Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request()
                .newBuilder()
                .addHeader("Authorization", getToken())
                .build();
        Response response = chain.proceed(request);
        return response;
    }

    private static String getToken() {
        return getAppContext().getSharedPreferences("LoginPref", 0).getString("token", "no token");
    }


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
    DailyCommuteApi providesDailyCommuteApi(@Named("yaatraRetrofit") Retrofit retrofit) {
        return retrofit.create(DailyCommuteApi.class);
    }

    @NotNull
    private HttpLoggingInterceptor getHttpLoggingInterceptor() {
        final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }




}
