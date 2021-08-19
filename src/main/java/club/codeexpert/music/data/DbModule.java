package club.codeexpert.music.data;

import android.app.Application;

import javax.inject.Singleton;

import androidx.annotation.NonNull;
import androidx.room.Room;
import club.codeexpert.music.data.db.AppDatabase;
import club.codeexpert.music.data.db.SongDao;
import dagger.Module;
import dagger.Provides;

@Module
public class DbModule {

    @Provides
    @Singleton
    AppDatabase provideDatabase(@NonNull Application application) {
        return Room.databaseBuilder(application, AppDatabase.class, "musicdb").build();
    }

    @Provides
    @Singleton
    SongDao provideMovieDao(@NonNull AppDatabase appDatabase) {
        return appDatabase.songDao();
    }
}