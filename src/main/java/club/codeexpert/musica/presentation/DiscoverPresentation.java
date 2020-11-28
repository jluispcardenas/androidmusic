package club.codeexpert.musica.presentation;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import club.codeexpert.musica.data.db.Song;
import club.codeexpert.musica.managers.ApiManager;

public class DiscoverPresentation {
    Response.Listener<ArrayList<Song>> responseListener = null;

    public void getResults(String method, Response.Listener<ArrayList<Song>> responseListener) {
        this.responseListener = responseListener;

        ApiManager.getInstance().call(method, null, getResponseListener());
    }

    Response.Listener<JSONObject> getResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray data = null;

                ArrayList<Song> newList = new ArrayList<>();
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
                        boolean downloaded = ApiManager.getInstance().isDownloaded(id);

                        Song it = new Song(id, link, title, duration, views, thumbnail, downloaded);

                        newList.add(it);
                    }

                    responseListener.onResponse(newList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
