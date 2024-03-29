package club.codeexpert.music.data.db;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Song implements Parcelable {
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
        this.duration = duration;
        this.views = views;
        this.thumbnail = thumbnail;
        this.downloaded = downloaded;

        this.title = title;
        String[] parts = title.split("(\\(|\\||\\]|\\\\)");
        if (parts != null) {
            this.title = parts[0];
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}

