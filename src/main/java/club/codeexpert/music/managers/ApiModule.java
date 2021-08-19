package club.codeexpert.music.managers;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.NonNull;
import club.codeexpert.music.data.SongRepository;
import club.codeexpert.music.data.db.Song;
import dagger.Module;
import dagger.Provides;

@Module
public class ApiModule {

    @Provides
    RequestQueue requestQueueProvider(@NonNull Application application) {
        return Volley.newRequestQueue(application.getApplicationContext());
    }

    @Provides
    @Singleton
    File fileProvider(@NonNull Application application) {
        ContextWrapper contextWrapper = new ContextWrapper(application);
        return contextWrapper.getDir(application.getFilesDir().getName(), Context.MODE_PRIVATE);
    }

    @Provides
    @Inject
    @Singleton
    ApiManager providesApiManager(SongRepository songRepository, RequestQueue requestQueue, RequestQueue requestQueueDownloads, File directory) {
        return  new ApiManager(songRepository, requestQueue, requestQueueDownloads, directory);
    }

    @Provides
    @Inject
    List<Song> providesListSong() {
        return new ArrayList<Song>();
    }

}
