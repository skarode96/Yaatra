package com.tcd.yaatra.di;

import android.app.Application;

import com.tcd.yaatra.App;
import com.tcd.yaatra.di.modules.ActivityModule;
import com.tcd.yaatra.di.modules.AppModule;
import com.tcd.yaatra.di.modules.YaatraApiModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;


@Singleton
@Component(modules = {
        AppModule.class,
        ActivityModule.class,
        YaatraApiModule.class,
        AndroidInjectionModule.class
})
public interface AppComponent {


    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder bindApplication(Application application);

        AppComponent build();
    }

    void inject(App app);
}