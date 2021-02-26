package club.codeexpert.musica;


import android.app.Application;

import javax.inject.Singleton;

import club.codeexpert.musica.data.DbModule;
import club.codeexpert.musica.managers.ApiModule;
import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

@Component(modules = {
        ActivityModule.class,
        AndroidSupportInjectionModule.class,
        DbModule.class,
        ApiModule.class})

@Singleton
public interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }

    void inject(AppController appController);
}
