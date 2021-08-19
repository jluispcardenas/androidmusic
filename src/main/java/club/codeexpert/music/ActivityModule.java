package club.codeexpert.music;

import club.codeexpert.music.ui.discover.DiscoverFragment;
import club.codeexpert.music.ui.downloads.DownloadsFragment;
import club.codeexpert.music.ui.notifications.NotificationsFragment;
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
