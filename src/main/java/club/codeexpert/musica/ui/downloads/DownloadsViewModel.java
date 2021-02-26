package club.codeexpert.musica.ui.downloads;

import android.content.Context;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import club.codeexpert.musica.data.SongRepository;
import club.codeexpert.musica.data.db.Song;


public class DownloadsViewModel extends ViewModel  {
    private MutableLiveData<List<Song>> songs = new MutableLiveData<>();

    @Inject
    SongRepository songRepository;

    @Inject
    public DownloadsViewModel() {

    }

    public List<Song> getAll() {
        return songRepository.getAll();
    }
}
