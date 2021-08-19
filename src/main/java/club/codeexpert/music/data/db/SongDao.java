package club.codeexpert.music.data.db;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface SongDao {
    @Query("SELECT * FROM song")
    List<Song> getAll();

    @Query("SELECT id FROM song")
    List<String> getIDs();

    @Query("SELECT * FROM song WHERE title LIKE :title")
    Song findByName(String title);

    @Insert
    void insertAll(Song... songs);

    @Delete
    void delete(Song song);
}

