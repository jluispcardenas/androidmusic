package club.codeexpert.musica.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Song {
    @PrimaryKey
    @NonNull
    public String id;

    @ColumnInfo(name = "link")
    public String link;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "duration")
    public String duration;

    @ColumnInfo(name = "views")
    public String views;

    @ColumnInfo(name = "thumbnail")
    public String thumbnail;

    @ColumnInfo(name = "downloaded")
    public boolean downloaded;

    public Song(String id, String link, String title, String duration, String views, String thumbnail, boolean downloaded) {
        this.id = id;
        this.link = link;
        this.title = title;
        this.duration = duration;
        this.views = views;
        this.thumbnail = thumbnail;
        this.downloaded = downloaded;
    }
}

