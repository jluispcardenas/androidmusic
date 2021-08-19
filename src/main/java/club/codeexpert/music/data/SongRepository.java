package club.codeexpert.music.data;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import club.codeexpert.music.data.db.Song;
import club.codeexpert.music.data.db.SongDao;

@Singleton
public class SongRepository {
    SongDao songDao;
    /*private static SongRepository instance;

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
    }*/

    @Inject
    public SongRepository(SongDao songDao) {
        this.songDao = songDao;
    }

    public List<Song> getAll() {
        return songDao.getAll();
    }

    public List<String> getIDs() {
        return songDao.getIDs();
    }

    public void insertAll(Song... songs) {
        songDao.insertAll(songs);
    }

    public void delete(Song song) {
        songDao.delete(song);
    }
}
