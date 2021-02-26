package club.codeexpert.musica;

import club.codeexpert.musica.ui.discover.DiscoverFragment;
import club.codeexpert.musica.ui.downloads.DownloadsFragment;
import club.codeexpert.musica.ui.notifications.NotificationsFragment;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityModule {

    @ContributesAndroidInjector()
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector
    abstract DiscoverFragment provideDiscoverFragment();

    @ContributesAndroidInjector
    abstract DownloadsFragment provideDownloadFragment();

    @ContributesAndroidInjector
    abstract NotificationsFragment provideNotificationsFragment();
}
