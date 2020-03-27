package com.tcd.yaatra.di.modules;

import android.app.Application;
import com.tcd.yaatra.TestApp;
import com.tcd.yaatra.di.AppComponent;
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
        AndroidInjectionModule.class,
        RoomModule.class,
        ExtendedFragmentModule.class
})
public interface TestAppComponent extends AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        TestAppComponent.Builder bindApplication(Application application);

        TestAppComponent build();
    }

    void inject(TestApp app);
}
