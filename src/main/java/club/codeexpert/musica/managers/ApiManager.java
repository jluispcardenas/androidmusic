package club.codeexpert.musica.managers;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import club.codeexpert.musica.R;
import club.codeexpert.musica.data.SongRepository;
import club.codeexpert.musica.data.db.Song;

public class ApiManager {
    private static final String TAG = "ApiManager";
    private static ApiManager instance = null;

    private static final String URL = "http://music.codeexpert.club:8000/";

    public RequestQueue requestQueue;
    public RequestQueue requestQueueDownloads;

    List<String> requested;
    List<String> songsDownloaded;

    static File directory = null;
    Context context;

    private ApiManager(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        requestQueueDownloads = Volley.newRequestQueue(context.getApplicationContext());

        songsDownloaded = SongRepository.getInstance().getIDs();
        requested = songsDownloaded;

        ContextWrapper contextWrapper = new ContextWrapper(context);
        directory = contextWrapper.getDir(context.getFilesDir().getName(), Context.MODE_PRIVATE);
    }

    public static synchronized ApiManager getInstance(Context context) {
        if (null == instance) instance = new ApiManager(context);

        return instance;
    }

    public static synchronized ApiManager getInstance() {
        if (null == instance) {
            throw new IllegalStateException(ApiManager.class.getSimpleName() + " is not initialized, call getInstance(...) first");
        }
        return instance;
    }

    public File getDir() {
        return directory;
    }

    public void addRequested(String id) {
        if (!requested.contains(id)) {
            requested.add(id);
        }
    }

    public boolean isDownloaded(String id) {
        return songsDownloaded.contains(id);
    }

    public boolean addDownloaded(String id) {
        return songsDownloaded.add(id);
    }

    /*public void callRecyclerView(String method, JSONObject jsonRequest, final ArrayList<MusicItem> mItems, final MyItemRecyclerViewAdapter mAdapter) {
        final String url = URL + method;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, jsonRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray data = null;
                Log.d("APP", "Request: " + url);
                ArrayList<MusicItem> newList = new ArrayList<>();
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
                        boolean downloaded = songsDownloaded.contains(id);

                        MusicItem it = new MusicItem(id, link, title, duration, views, thumbnail, downloaded);
                        newList.add(it);
                    }

                    mItems.clear();
                    mItems.addAll(newList);
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }*/

    public void call(String type, String method, JSONObject jsonRequest, Response.Listener listener, Response.ErrorListener errorListener) {
        String url = URL + method;

        if (errorListener == null) {
            errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            };
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, jsonRequest, listener,  errorListener);
        jsonObjectRequest.setTag("play");

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        if (type.equals("download")) {
            requestQueueDownloads.add(jsonObjectRequest);
        } else {
            requestQueue.cancelAll("play");

            requestQueue.add(jsonObjectRequest);
        }
    }

    public void call(String method, JSONObject jsonRequest, Response.Listener listener) {
        call("play", method, jsonRequest, listener, null);
    }

    public void callDownload(String method, JSONObject jsonRequest, Response.Listener listener) {
        call("download", method, jsonRequest, listener, null);
    }

    public void requestDownloadFile(final Song song) {
        final String songId = song.id;
        final String url = "play/" + song.id;

        if (!requested.contains(songId)) {
            requested.add(songId);

            callDownload(url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONArray data = null;
                    Log.d("APP", "Request: " + url);
                    try {
                        if (response.has("url")) {
                            String play_url = response.getString("url");
                            if (!play_url.equals("")) {
                                Log.d("APP", play_url);

                                song.link = play_url;

                                new DownloadFile().execute(song);
                            } else {
                                Log.d("APP", String.valueOf(R.string.file_pending));
                            }
                        } else {
                            Log.d("APP", String.valueOf(R.string.file_invalid));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static class DownloadFile extends AsyncTask<Song, Integer, String> {
        @Override
        protected String doInBackground(Song... urlParams) {
            final Song song = urlParams[0];
            final String play_url = song.link;

            downloadFileToDevice(song, play_url);

            SongRepository.getInstance().insertAll(song);

            getInstance().addDownloaded(song.id);

            return null;
        }

        private void downloadFileToDevice(Song song, String url_http) {
            int count;
            try {
                java.net.URL url = new URL(url_http);
                URLConnection conexion = url.openConnection();
                conexion.connect();

                int lenghtOfFile = conexion.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream());

                String filename = song.id + ".mp3";
                File file =  new File(directory, filename);
                OutputStream output = new FileOutputStream(file);
                Log.d("APP", file.getAbsolutePath());

                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress((int) (total * 100 / lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
