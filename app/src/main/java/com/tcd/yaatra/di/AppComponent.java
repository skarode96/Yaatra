package com.tcd.yaatra.di;

import android.app.Application;

import com.tcd.yaatra.App;
import com.tcd.yaatra.di.modules.ActivityModule;
import com.tcd.yaatra.di.modules.AppModule;
import com.tcd.yaatra.di.modules.FragmentModule;
import com.tcd.yaatra.di.modules.RepositoryModule;
import com.tcd.yaatra.di.modules.RoomModule;
import com.tcd.yaatra.di.modules.WorkerBindingModule;
import com.tcd.yaatra.di.modules.YaatraApiModule;
import com.tcd.yaatra.synchronizationHelper.RatingSyncWorkerFactory;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;


@Singleton
@Component(modules = {
        AppModule.class,
        ActivityModule.class,
        FragmentModule.class,
        YaatraApiModule.class,
        RepositoryModule.class,
        ViewModelModule.class,
        AndroidInjectionModule.class,
        RoomModule.class,
        WorkerBindingModule.class
})
public interface AppComponent {

    RatingSyncWorkerFactory ratingSyncWorkerFactory();

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder bindApplication(Application application);

        AppComponent build();
    }

    void inject(App app);

//    UserInfoDao userInfoDao();
//    JourneySharingDatabase journeySharingDatabase();
//    UserInfoRepository userInfoRepository();
//    Application application();
}