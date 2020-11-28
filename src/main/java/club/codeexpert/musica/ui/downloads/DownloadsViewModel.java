package club.codeexpert.musica.ui.downloads;

import android.content.Context;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import club.codeexpert.musica.data.SongRepository;
import club.codeexpert.musica.data.db.Song;

public class DownloadsViewModel extends ViewModel  {
    private MutableLiveData<List<Song>> songs = new MutableLiveData<>();
    SongRepository songRepository;

    public DownloadsViewModel() {
        songRepository = SongRepository.getInstance();
    }

    public MutableLiveData<List<Song>> getAll() {
        return (MutableLiveData<List<Song>>) songRepository.getAll();
    }
}
