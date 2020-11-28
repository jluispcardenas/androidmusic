package club.codeexpert.musica.data;

import android.content.Context;

import java.util.List;

import androidx.room.Room;
import club.codeexpert.musica.data.db.AppDatabase;
import club.codeexpert.musica.data.db.DbFactory;
import club.codeexpert.musica.data.db.Song;
import club.codeexpert.musica.data.db.SongDao;

public class SongRepository {
    SongDao dao = null;
    private static SongRepository instance;

    public static synchronized SongRepository getInstance(Context context) {
        if (null == instance)
            instance = new SongRepository(context);

        return instance;
    }

    public static synchronized SongRepository getInstance() {
        if (null == instance) {
            throw new IllegalStateException(SongRepository.class.getSimpleName() + " is not initialized, call getInstance(...) first");
        }
        return instance;
    }

    public SongRepository(Context context) {
        dao = DbFactory.getInstance(context).songDao();
    }

    public List<Song> getAll() {
        return dao.getAll();
    }

    public List<String> getIDs() {
        return dao.getIDs();
    }

    public void insertAll(Song... songs) {
        dao.insertAll(songs);
    }

    public void delete(Song song) {
        dao.delete(song);
    }
}

