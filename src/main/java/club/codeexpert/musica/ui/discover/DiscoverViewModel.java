package club.codeexpert.musica.ui.discover;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import club.codeexpert.musica.data.db.Song;
import club.codeexpert.musica.managers.ApiManager;

public class DiscoverViewModel extends ViewModel {
    private MutableLiveData<List<Song>> songs = new MutableLiveData<>();

    @Inject
    ApiManager apiManager;

    @Inject
    public DiscoverViewModel() {

    }

    public LiveData<List<Song>> getResults(String method) {
        this.apiManager.call(method, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray data = null;

                List<Song> newList = new ArrayList<Song>();
                try {
                    data = response.getJSONArray("results");
                    int length = data.length();
                    Log.d("APP", "Length: " + length);
                    for (int i = 0; i < length; i++) {
                        String id = data.getJSONObject(i).getString("id");
                        String link = data.getJSONObject(i).getString("link");
                        String title = data.getJSONObject(i).getString("title");
                        String duration = data.getJSONObject(i).getString("duration");
                        String views = data.getJSONObject(i).getString("views");
                        String thumbnail = data.getJSONObject(i).getJSONArray("thumbnails").getString(0);
                        boolean downloaded = DiscoverViewModel.this.apiManager.isDownloaded(id);

                        Song it = new Song(id, link, title, duration, views, thumbnail, downloaded);

                        newList.add(it);
                    }

                    songs.setValue(newList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return songs;
    }
}
